package com.edwin.trial_bank_app.exception;


public class AccountNotFoundException
        extends RuntimeException {

    public AccountNotFoundException(
            String accountNumber) {

        super(
                "No account with account number " + accountNumber + " found."
        );
    }
}