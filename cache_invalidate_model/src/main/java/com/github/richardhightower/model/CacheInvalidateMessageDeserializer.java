package com.github.richardhightower.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class CacheInvalidateMessageDeserializer implements Deserializer<CacheInvalidateMessage> {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public CacheInvalidateMessage deserialize(String topic, byte[] bytes) {
        try {
            return objectMapper.readValue(bytes, CacheInvalidateMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }
}
