package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.service.AccountValidationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountValidationServiceImpl
        implements AccountValidationService {

    @Override
    public void validateDeposit(
            Account account,
            BigDecimal amount) {

        if (account == null)
            throw new RuntimeException("Account not found");

        if (!account.getStatus().isActive())
            throw new RuntimeException("Account inactive");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new RuntimeException("Invalid amount");
    }

    @Override
    public void validateWithdrawal(
            Account account,
            BigDecimal amount) {

        validateDeposit(account, amount);

        if (account.getAccountBalance()
                .compareTo(amount) < 0) {

            throw new RuntimeException(
                    "Insufficient funds");
        }
    }

    @Override
    public void validateTransfer(
            Account source,
            Account destination,
            BigDecimal amount) {

        validateWithdrawal(source, amount);

        if (destination == null)
            throw new RuntimeException(
                    "Destination account not found");

        if (!destination.getStatus().isActive())
            throw new RuntimeException(
                    "Destination account inactive");
    }
}
