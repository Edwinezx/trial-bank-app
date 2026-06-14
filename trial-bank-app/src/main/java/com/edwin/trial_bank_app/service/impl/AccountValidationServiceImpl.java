package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.exception.InactiveAccountException;
import com.edwin.trial_bank_app.exception.InsufficientFundsException;
import com.edwin.trial_bank_app.exception.InvalidAmountException;
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
            throw new AccountNotFoundException(account.getAccountNumber());

        if (!account.getStatus().isActive())
            throw new InactiveAccountException(account.getAccountNumber());

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException(amount);
    }

    @Override
    public void validateWithdrawal(
            Account account,
            BigDecimal amount) {

        validateDeposit(account, amount);

        if (account.getAvailableBalance()
                .compareTo(amount) < 0) {

            throw new InsufficientFundsException(account.getAvailableBalance(),amount);
        }
    }

    @Override
    public void validateTransfer(
            Account source,
            Account destination,
            BigDecimal amount) {

        validateWithdrawal(source, amount);

        if (destination == null)
            throw new AccountNotFoundException(destination.getAccountNumber());


        if (!destination.getStatus().isActive())
            throw new InactiveAccountException(destination.getAccountNumber());
    }
}
