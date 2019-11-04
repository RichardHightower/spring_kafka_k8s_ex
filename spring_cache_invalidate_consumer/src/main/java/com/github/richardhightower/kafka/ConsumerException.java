package com.github.richardhightower.kafka;

public class ConsumerException extends RuntimeException {
    public ConsumerException() {
    }

    public ConsumerException(String message) {
        super(message);
    }

    public ConsumerException(String message, Throwable cause) {
        super(message, cause);
    }
}
