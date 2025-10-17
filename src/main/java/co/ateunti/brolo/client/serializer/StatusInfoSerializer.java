package co.ateunti.brolo.client.serializer;

import co.ateunti.brolo.client.model.StatusInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class StatusInfoSerializer implements Serializer<StatusInfo> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public byte[] serialize(String topic, StatusInfo data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing CustomObject to byte[]", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}