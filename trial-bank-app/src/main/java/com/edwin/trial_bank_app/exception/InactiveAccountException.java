package com.edwin.trial_bank_app.exception;


public class InactiveAccountException extends RuntimeException{

    public InactiveAccountException(String accountNumber) {

        super(
                "Account with account number " + accountNumber + " is inactive."
        );
    }
}
