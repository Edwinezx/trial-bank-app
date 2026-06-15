package com.edwin.trial_bank_app.exception;

public class FraudSuspectedException extends RuntimeException {
    public FraudSuspectedException(String reason) {
        super("Transaction blocked due to suspected fraud: " + reason);
    }
}
