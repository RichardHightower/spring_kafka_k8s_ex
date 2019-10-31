package com.github.richardhightower.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.LongAdder;


/**
 * Mock stub object that talks to Redis or CDN or something
 */
@RestController
public class CacheInvalidateController {

    private final Logger logger = LoggerFactory.getLogger(CacheInvalidateController.class);

    private final LongAdder count = new LongAdder();


    @PostMapping(path = "/cache/invalidate/{page}")
    public boolean invalidatePage(@PathVariable String page) {
        count.increment();
        logger.info("Received invalidate request: " + page);
        return true;
    }

    @GetMapping(path = "/cache/invalidate/count")
    public long count() {
        return count.longValue();
    }

}
