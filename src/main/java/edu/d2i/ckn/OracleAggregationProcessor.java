package edu.d2i.ckn;

import edu.d2i.ckn.model.JSONSerde;
import edu.d2i.ckn.model.OracleAggregatedEvent;
import edu.d2i.ckn.model.OracleAggregator;
import edu.d2i.ckn.model.OracleEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.util.Properties;

public class OracleAggregationProcessor {
    private static final Logger logger = LoggerFactory.getLogger(OracleAggregationProcessor.class);

    public static void main(String[] args) {
        String ckn_brokers = System.getenv().getOrDefault("CKN_BROKERS", "localhost:9092");
        String input_topic = System.getenv().getOrDefault("ORACLE_INPUT_TOPIC", "oracle-events");
        String aggregated_topic = System.getenv().getOrDefault("ORACLE_AGG_ALERT_TOPIC", "oracle-aggregated");
        String app_id = System.getenv().getOrDefault("APP_ID", "ckn-camera-traps-oracle-aggregator");
        long window_time =  Long.parseLong(System.getenv().getOrDefault("CKN_ORACLE_WINDOW_TIME","1"));
        String group_key = "-group-";

        Properties props = getProperties(ckn_brokers, app_id);

        Serde<OracleEvent> oracleEventSerde = new JSONSerde<>(OracleEvent.class);
        Serde<OracleAggregator> oracleAggregatorSerde = new JSONSerde<>(OracleAggregator.class);
        Serde<OracleAggregatedEvent> oracleAggregatedEventSerde = new JSONSerde<>(OracleAggregatedEvent.class);

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, OracleEvent> sourceStream = builder.stream(input_topic, Consumed.with(Serdes.String(), oracleEventSerde));

        KGroupedStream<String, OracleEvent> groupedStream = sourceStream
                .selectKey((key, value) -> value.getDevice_id() + group_key + value.getModel_id() + group_key + value.getImage_decision())
                .groupByKey(Grouped.with(Serdes.String(), oracleEventSerde));

        KTable<Windowed<String>, OracleAggregator> aggregatedTable = groupedStream
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(window_time)))
                .aggregate(
                        OracleAggregator::new,
                        (key, value, aggregate) -> {
                            aggregate.process(value);
                            return aggregate;
                        },
                        Materialized.with(Serdes.String(), oracleAggregatorSerde)
                );

        aggregatedTable.toStream().map((key, value) -> {
            OracleAggregatedEvent aggregatedEvent = new OracleAggregatedEvent();
            aggregatedEvent.setDevice_id(value.getDevice_id());
            aggregatedEvent.setModel_id(value.getModel_id());
            aggregatedEvent.setImage_decision(value.getImage_decision());
            aggregatedEvent.setWindow_start(key.window().startTime());
            aggregatedEvent.setWindow_end(key.window().endTime());
            aggregatedEvent.setAverage_probability(value.getAverage_probability());
            aggregatedEvent.setEvent_count(value.getCount());
            return new KeyValue<>(key.key(), aggregatedEvent);
        }).to(aggregated_topic, Produced.with(Serdes.String(), oracleAggregatedEventSerde));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static Properties getProperties(String ckn_brokers, String app_id) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, app_id);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ckn_brokers);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return props;
    }
}