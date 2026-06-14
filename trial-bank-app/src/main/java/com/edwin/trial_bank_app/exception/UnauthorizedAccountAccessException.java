package com.edwin.trial_bank_app.exception;


public class UnauthorizedAccountAccessException extends RuntimeException{

    public UnauthorizedAccountAccessException(){
        super(
                "Unauthorized account access"
        );
    }
}
