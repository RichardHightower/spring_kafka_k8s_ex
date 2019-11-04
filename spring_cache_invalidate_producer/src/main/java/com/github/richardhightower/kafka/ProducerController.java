package com.github.richardhightower.kafka;

import com.github.richardhightower.model.CacheInvalidateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class ProducerController {

    private final Logger logger = LoggerFactory.getLogger(ProducerController.class);

    private final Counter counter;
    private final Counter errorCount;

    public ProducerController(MeterRegistry registry) {
        this.counter = registry.counter("producer.received");
        this.errorCount = registry.counter("producer.error");
    }

    @Value("${topic.cache.invalidate}")
    private String topic;


    @Autowired
    private KafkaTemplate<Object, Object> producer;

    @PostMapping(path = "/send/cache")
    public void invalidatePage(@RequestBody CacheInvalidateMessage invalidateMessage) {
        counter.increment();
        try {
            this.producer.send(topic, invalidateMessage.getKey(), invalidateMessage);
        } catch (Exception ex) {
            errorCount.increment();
            logger.error("Unable to send message via producer", ex);
        }
    }


}
