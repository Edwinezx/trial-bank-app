package com.edwin.trial_bank_app.security.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryRateLimiterService implements RateLimiterService {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean tryConsume(String key) {
        // computeIfAbsent is atomic — safe when multiple threads hit the same
        // new IP simultaneously, preventing duplicate bucket creation
        return buckets.computeIfAbsent(key, _ -> newBucket()).tryConsume(1);
    }

    private Bucket newBucket() {
        // 10 tokens capacity, refills 10 tokens every 1 minute.
        // Greedy refill means all 10 tokens are added at once after the minute,
        // rather than one token every 6 seconds (intervally).
        Bandwidth limit = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(10, Duration.ofMinutes(1))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}