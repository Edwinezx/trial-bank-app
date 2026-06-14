package com.edwin.trial_bank_app.exception;

import java.math.BigDecimal;

public class InsufficientFundsException
        extends RuntimeException {

    public InsufficientFundsException(
            BigDecimal balance,
            BigDecimal amount) {

        super(
                "Insufficient funds. Balance: "
                        + balance
                        + ", Amount: "
                        + amount
        );
    }
}