package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.WithdrawService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import com.edwin.trial_bank_app.utils.TransactionUtils;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class WithdrawServiceImpl implements WithdrawService {
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    @Override
    @Transactional
    public MultiAccountBankResponse withdraw(
            String accountNumber,
            BigDecimal amount) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            return MultiAccountBankResponse.builder()
                    .responseMessage("Invalid withdrawal amount")
                    .build();
        }

        Account account =
                accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {

            return MultiAccountBankResponse.builder()
                    .responseMessage("Account not found")
                    .build();
        }

        if (!account.getStatus().isActive()) {

            return MultiAccountBankResponse.builder()
                    .responseMessage("Account inactive")
                    .build();
        }

        if (account.getAccountBalance()
                .compareTo(amount) < 0) {

            return MultiAccountBankResponse.builder()
                    .responseMessage("Insufficient funds")
                    .build();
        }

        account.setAccountBalance(
                account.getAccountBalance().subtract(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                TransactionUtils.generateReference());

        transaction.setSourceAccountNumber(
                account.getAccountNumber());

        transaction.setAmount(amount);

        transaction.setTransactionType(
                TransactionType.WITHDRAWAL);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transaction.setTransactionDate(
                java.time.LocalDateTime.now());

        transactionRepository.save(transaction);

        return MultiAccountBankResponse.builder()
                .responseMessage("Withdrawal successful")
                .accountInfo(
                        List.of(
                                AccountUtils.mapToAccountInfo(account)
                        )
                )
                .build();
    }
}
