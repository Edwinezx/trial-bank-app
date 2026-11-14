package com.edwin.trial_bank_app.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String idempotencyKey) {
        super("Idempotency key has already been used for a different transfer: "
                + idempotencyKey);
    }
}
