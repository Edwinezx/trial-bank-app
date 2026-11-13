package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.exception.*;
import com.edwin.trial_bank_app.service.AccountValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccountValidationServiceImpl implements AccountValidationService {

    @Override
    public void validateDeposit(Account account, BigDecimal amount) {
        if (account == null)
            throw new AccountNotFoundException("Account not found");

        validateAccountOperational(account);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidAmountException(amount);
    }

    @Override
    public void validateWithdrawal(Account account, BigDecimal amount) {
        validateDeposit(account, amount);

        if (account.getAvailableBalance().compareTo(amount) < 0)
            throw new InsufficientFundsException(account.getAvailableBalance(), amount);
    }

    @Override
    public void validateOwnership(Account account) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedAccountAccessException();
        }

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        Objects.equals(authority.getAuthority(), "ROLE_ADMIN"));

        if (isAdmin) {
            return;
        }

        String loggedInEmail = authentication.getName();
        String accountOwnerEmail = account.getUser().getEmail();

        if (!accountOwnerEmail.equalsIgnoreCase(loggedInEmail)) {
            throw new UnauthorizedAccountAccessException();
        }
    }

    @Override
    public void validateTransfer(Account source, Account destination, BigDecimal amount) {
        if (destination == null)
            throw new AccountNotFoundException("Destination account not found");

        // Use account number equality (equals() is overridden on Account)
        if (source.equals(destination))
            throw new SelfTransactionException();

        validateWithdrawal(source, amount);
        validateOwnership(source);
        validateAccountOperational(destination);
    }

    // ── internal helper ──────────────────────────────────────────────────────
    private void validateAccountOperational(Account account) {
        switch (account.getStatus()) {
            case CLOSED  -> throw new InactiveAccountException(account.getAccountNumber());
            case FROZEN  -> throw new FrozenAccountException(account.getAccountNumber());
            case DORMANT -> throw new DormantAccountException(account.getAccountNumber());
            case ACTIVE  -> { /* ok */ }
        }
    }
}
