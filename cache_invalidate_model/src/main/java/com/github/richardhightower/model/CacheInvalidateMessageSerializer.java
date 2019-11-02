package com.github.richardhightower.model;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CacheInvalidateMessageSerializer implements Serializer<CacheInvalidateMessage> {

    private final static ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(String topic, CacheInvalidateMessage data) {
        try {
            return objectMapper.writeValueAsString(data).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}
