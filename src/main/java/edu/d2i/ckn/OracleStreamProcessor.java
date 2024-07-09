package main.java.edu.d2i.ckn;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Properties;

public class OracleStreamProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OracleStreamProcessor.class);

    public static void main(String[] args) {
        double criticalThreshold = Double.parseDouble(System.getenv().getOrDefault("ORACLE_ACC_CRITICAL_THRESHOLD", "0.3"));
        String ckn_brokers = System.getenv().getOrDefault("CKN_BROKERS", "localhost:9092");
        String input_topic = System.getenv().getOrDefault("ORACLE_INPUT_TOPIC", "oracle-events");
        String alert_topic = System.getenv().getOrDefault("ORACLE_ACC_ALERT_TOPIC", "oracle-alerts");
        String app_id = System.getenv().getOrDefault("APP_ID", "ckn-camera-traps-oracle-processor");


        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, app_id);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ckn_brokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> sourceStream = builder.stream(input_topic);

        sourceStream.peek((key, value) -> System.out.println("Input event: " + value));

        KStream<String, String> filteredStream = sourceStream.filter((key, value) -> {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(value);
                double probability = jsonNode.get("probability").asDouble();
                return probability < criticalThreshold;
            } catch (Exception e) {
                logger.error("Error processing input event", e);
                return false;
            }
        });

        filteredStream.mapValues(value -> {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(value);
                ObjectNode alert = new ObjectMapper().createObjectNode();
                alert.put("alert_name", "Low Accuracy Alert");
                alert.put("priority", "high");
                alert.put("description", "Probability below threshold");
                alert.put("source_topic", input_topic);
                alert.put("timestamp", System.currentTimeMillis());
                alert.set("event_data", jsonNode);
                // todo: remove this print
                System.out.println("Critical alert created: " + alert.toString());
                return alert.toString();
            } catch (Exception e) {
                logger.error("Error processing output event", e);
                return value;
            }
        }).to(alert_topic, Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }
}