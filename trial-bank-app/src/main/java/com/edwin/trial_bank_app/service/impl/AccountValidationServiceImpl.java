package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.exception.*;
import com.edwin.trial_bank_app.service.AccountValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountValidationServiceImpl
        implements AccountValidationService {


    @Override
    public void validateDeposit(
            Account account,
            BigDecimal amount) {

        if (account == null)
            throw new AccountNotFoundException("Account not found");

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
    public void validateOwnership(Account account) {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        assert authentication != null;
        String loggedInEmail =
                authentication.getName();

        String accountOwnerEmail =
                account.getUser()
                        .getEmail();

        if (!accountOwnerEmail.equals(loggedInEmail)) {

            throw new UnauthorizedAccountAccessException();
        }
    }

    @Override
    public void validateTransfer(
            Account source,
            Account destination,
            BigDecimal amount) {

        validateWithdrawal(source, amount);
        validateOwnership(source);

        if (destination == null)
            throw new AccountNotFoundException("Account not found");

        if (destination == source)
            throw new SelfTransactionException();

        if (!destination.getStatus().isActive())
            throw new InactiveAccountException(destination.getAccountNumber());
    }
}
