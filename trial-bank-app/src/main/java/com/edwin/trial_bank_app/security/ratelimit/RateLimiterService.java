package com.edwin.trial_bank_app.security.ratelimit;

public interface RateLimiterService {
    /**
     * Attempt to consume one token for the given key (typically an IP address).
     * Returns true if the request is allowed, false if the rate limit is exceeded.
     */
    boolean tryConsume(String key);
}