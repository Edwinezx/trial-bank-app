package com.edwin.trial_bank_app.security.blacklist;

public interface TokenBlacklistService {

    void blacklist(String token, long expiryMillis);
    boolean isBlacklisted(String token);
}