package edu.d2i.ckn.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import edu.d2i.ckn.util.ObjectMapperUtil;

/*
JSON serializer for all the event classes in the models.
 */
public class JSONSerde<T> implements Serde<T> {
    private static final ObjectMapper OBJECT_MAPPER = ObjectMapperUtil.getObjectMapper();
    private final Class<T> type;

    public JSONSerde(Class<T> type) {
        this.type = type;
    }

    @Override
    public Serializer<T> serializer() {
        return (key, data) -> serialize(data);
    }

    @Override
    public Deserializer<T> deserializer() {
        return (key, bytes) -> deserialize(bytes);
    }

    private byte[] serialize(T data) {
        try {
            return OBJECT_MAPPER.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }

    private T deserialize(byte[] bytes) {
        try {
            return OBJECT_MAPPER.readValue(bytes, type);
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }
}