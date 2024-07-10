package edu.d2i.ckn;

import edu.d2i.ckn.model.JSONSerde;
import edu.d2i.ckn.model.OracleAlert;
import edu.d2i.ckn.model.OracleEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

/*
Streams the Oracle events and sends an alert if the probability of a particular image is below the set threshold value.
Deleted images are ignored.
Input: OracleEvent
Output: OracleAlert
 */
public class OracleAccAlertProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OracleAccAlertProcessor.class);

    public static void main(String[] args) {
        // Read the environment variables
        double criticalThreshold = Double.parseDouble(System.getenv().getOrDefault("ORACLE_ACC_CRITICAL_THRESHOLD", "0.3"));
        String ckn_brokers = System.getenv().getOrDefault("CKN_BROKERS", "localhost:9092");
        String input_topic = System.getenv().getOrDefault("ORACLE_INPUT_TOPIC", "oracle-events");
        String alert_topic = System.getenv().getOrDefault("ORACLE_ACC_ALERT_TOPIC", "oracle-alerts");
        String app_id = System.getenv().getOrDefault("APP_ID", "ckn-camera-traps-oracle-processor");
        String deleted_decision = System.getenv().getOrDefault("ORACLE_DELETED_DECISION", "Deleted");

        // Load the properties
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, app_id);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ckn_brokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        // Build the stream processing pipeline.
        StreamsBuilder builder = new StreamsBuilder();

        // Serializer and deserializer classes for input and output events.
        Serde<OracleEvent> oracleEventSerde = new JSONSerde<>(OracleEvent.class);
        Serde<OracleAlert> alertSerde = new JSONSerde<>(OracleAlert.class);

        // Initializing the input stream read.
        KStream<String, OracleEvent> sourceStream = builder.stream(input_topic, Consumed.with(Serdes.String(), oracleEventSerde));

        sourceStream.peek((key, value) -> logger.info("Input event: " + value));

        // Filtering events based on decision!='Deleted' and probability < threshold.
        KStream<String, OracleEvent> filteredStream = sourceStream.filter((key, value) -> {
            try {
                double probability = value.getProbability();
                String decision = value.getImage_decision();
                // if the image is already deleted don't send the alert.
                if (decision.equals(deleted_decision)){
                    return false;
                }
                else {
                    return probability < criticalThreshold;
                }
            } catch (Exception e) {
                logger.error("Error processing input event", e);
                return false;
            }
        });

        // Creating the output alert if the above filter is matched.
        filteredStream.mapValues(value -> {
                    try {
                        OracleAlert alert = new OracleAlert();
                        alert.setAlert_name("CKN Accuracy Alert");
                        alert.setPriority("HIGH");
                        alert.setDescription("Accuracy below threshold: " + criticalThreshold);
                        alert.setSource_topic(input_topic);
                        alert.setTimestamp(System.currentTimeMillis());
                        alert.setEvent_data(value);
                        return alert;
                    } catch (Exception e) {
                        logger.error("Error processing output event", e);
                        return null;
                    }
                }).filter((key, value) -> value != null)
                .to(alert_topic, Produced.with(Serdes.String(), alertSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}