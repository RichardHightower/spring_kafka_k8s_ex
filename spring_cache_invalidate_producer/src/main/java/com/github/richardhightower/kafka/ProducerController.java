package com.github.richardhightower.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProducerController {

    @Value("${topic.cache.invalidate}")
    private String topic;


    @Autowired
    private KafkaTemplate<Object, Object> producer;

    @PostMapping(path = "/send/cache/{page}")
    public void invalidatePage(@PathVariable String page) {
        this.producer.send(topic, page, page);
    }

}
