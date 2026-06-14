package com.edwin.trial_bank_app.exception;

import java.math.BigDecimal;

public class InvalidAmountException extends RuntimeException{

    public InvalidAmountException(BigDecimal amount){
        super(
                "The amount "  + amount + " is invalid for transactions."
        );
    }
}
