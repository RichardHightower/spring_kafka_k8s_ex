package com.github.richardhightower.model;

import java.io.Serializable;
import java.util.Objects;

public class CacheInvalidateMessage implements Serializable {

    private long timestamp;
    private String key;

    public CacheInvalidateMessage(long timestamp, String key) {
        this.timestamp = timestamp;
        this.key = key;
    }

    public CacheInvalidateMessage(String key) {
        this.timestamp = System.currentTimeMillis();
        this.key = key;
    }

    public CacheInvalidateMessage() {
        this.timestamp = System.currentTimeMillis();
        this.key = "";
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheInvalidateMessage that = (CacheInvalidateMessage) o;
        return timestamp == that.timestamp &&
                key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, key);
    }

    @Override
    public String toString() {
        return "CacheInvalidateMessage{" +
                "timestamp=" + timestamp +
                ", key='" + key + '\'' +
                '}';
    }
}
