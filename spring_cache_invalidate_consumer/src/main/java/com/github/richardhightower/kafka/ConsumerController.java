package com.github.richardhightower.kafka;



import com.github.richardhightower.model.CacheInvalidateMessage;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.LongAdder;

@RestController
public class ConsumerController {
    private final Logger logger = LoggerFactory.getLogger(ConsumerController.class);
    private final LongAdder counts = new LongAdder();

    private final LongAdder okCount = new LongAdder();
    private final LongAdder errorCount = new LongAdder();

    @Value("${cache.invalidate.url}")
    private String cacheInvalidateUrl;

    @KafkaListener(id = "${consumer.group.cache.invalidate}", topics = "${topic.cache.invalidate}")
    public void listen(CacheInvalidateMessage invalidateMessage, KafkaConsumer kafkaConsumer) {

        counts.increment();
        logger.info("Received: " + invalidateMessage);
        final RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Boolean> response = restTemplate.postForEntity(cacheInvalidateUrl, invalidateMessage, Boolean.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                okCount.increment();
                logger.info(String.format("success sent message %s %s %d", invalidateMessage, response.getStatusCode(),
                        response.getStatusCode().value()));

                kafkaConsumer.commitSync();
            } else {
                errorCount.increment();
                var errorMessage = String.format("error sending message %s %s %d", invalidateMessage, response.getStatusCode(),
                        response.getStatusCode().value());
                logger.error(errorMessage);
                throw new ConsumerException(errorMessage);
            }
        } catch (RestClientException rce) {
            errorCount.increment();
            var errorMessage = String.format("exception sending message %s", invalidateMessage);
            logger.error(errorMessage, rce);
            throw new ConsumerException(errorMessage, rce);
        }

    }

    @GetMapping(path = "/consumer/counts/all")
    public long counts() {
        return counts.longValue();
    }

    @GetMapping(path = "/consumer/counts/success")
    public long successCounts() {
        return okCount.longValue();
    }

    @GetMapping(path = "/consumer/counts/error")
    public long errorCounts() {
        return errorCount.longValue();
    }
}
