package com.edwin.trial_bank_app.exception;

public class SelfTransactionException extends RuntimeException {
    public SelfTransactionException() {
        super(
                "Transfer to self is invalid"
        );
    }
}
