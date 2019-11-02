package com.github.richardhightower.kafka;

import com.github.richardhightower.model.CacheInvalidateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.LongAdder;


/**
 * Mock stub object that talks to Redis or CDN or something
 */
@RestController
public class CacheInvalidateController {

    private final Logger logger = LoggerFactory.getLogger(CacheInvalidateController.class);

    private final LongAdder count = new LongAdder();


    @PostMapping(path = "/cache/invalidate")
    public boolean invalidatePage(@RequestBody CacheInvalidateMessage invalidateMessage) {
        count.increment();
        logger.info("Received invalidate request: " + invalidateMessage);
        return true;
    }

    @GetMapping(path = "/cache/invalidate/count")
    public long count() {
        return count.longValue();
    }

}
