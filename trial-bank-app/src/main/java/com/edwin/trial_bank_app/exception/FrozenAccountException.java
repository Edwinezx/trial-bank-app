package com.edwin.trial_bank_app.exception;

public class FrozenAccountException extends RuntimeException {
    public FrozenAccountException(String accountNumber) {
        super("Account " + accountNumber + " is frozen and cannot perform transactions.");
    }
}
