package com.edwin.trial_bank_app.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String reference) {
        super("Duplicate transaction detected. Reference: " + reference);
    }
}
