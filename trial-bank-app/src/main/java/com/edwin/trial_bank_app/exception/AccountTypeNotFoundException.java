package com.edwin.trial_bank_app.exception;

public class AccountTypeNotFoundException extends RuntimeException {
    public AccountTypeNotFoundException(String message) {
        super(message);
    }
}
