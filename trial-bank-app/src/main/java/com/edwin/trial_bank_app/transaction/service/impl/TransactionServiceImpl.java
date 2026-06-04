package com.edwin.trial_bank_app.transaction.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.transaction.service.TransactionService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MultiAccountBankResponse transferFunds(String destinationAccountNumber, BigDecimal amount) {
        // 🔑 Get logged-in user email from JWT
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInEmail = authentication.getName();

        // 🔎 Find user by email
        User sourceUser = userRepository.findByEmail(loggedInEmail);
        if (sourceUser == null) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.USER_NOT_FOUND_CODE)
                    .responseMessage("User not found for logged-in email")
                    .build();
        }

        // 🔎 Find accounts for this user
        List<Account> sourceAccounts = accountRepository.findByUser(sourceUser);
        if (sourceAccounts.isEmpty()) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage("No accounts found for logged-in user")
                    .build();
        }

        Account sourceAccount = sourceAccounts.get(0); // or let user specify

        // 💰 Check balance
        if (sourceAccount.getAccountBalance().compareTo(amount) < 0) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage("Insufficient funds")
                    .build();
        }

        // 🎯 Destination account lookup using Integer
        Account destinationAccount = accountRepository.findByAccountNumber(destinationAccountNumber);
        if (destinationAccount == null) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage("Destination account not found")
                    .build();
        }

        // 🔄 Perform transfer
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(amount));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        return MultiAccountBankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage("Transfer successful")
                .accountInfo(List.of(AccountUtils.mapToAccountInfo(sourceAccount)))
                .build();
    }
}