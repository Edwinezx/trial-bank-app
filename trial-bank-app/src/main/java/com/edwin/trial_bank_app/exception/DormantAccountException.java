package com.edwin.trial_bank_app.exception;

public class DormantAccountException extends RuntimeException {
    public DormantAccountException(String accountNumber) {
        super("Account " + accountNumber + " is dormant. Please contact support to reactivate.");
    }
}
