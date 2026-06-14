package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.WithdrawRequest;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.WithdrawService;
import com.edwin.trial_bank_app.utils.TransactionUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private final AccountValidationService validationService;


    @Override
    @Transactional
    public void withdrawMoney(WithdrawRequest request) {


        Account account =
                accountRepository.findByAccountNumber(request.getSourceAccountNumber()
                ).orElseThrow(() ->
                new AccountNotFoundException("Source account not found")
        );

        BigDecimal amount = request.getAmount();

        validationService.validateWithdrawal(account, amount);

        account.setAvailableBalance(
                account.getAvailableBalance().subtract(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                TransactionUtils.generateReference());

        transaction.setSourceAccountNumber(
                account.getAccountNumber());

        transaction.setAmount(request.getAmount());

        transaction.setTransactionType(
                TransactionType.WITHDRAWAL);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transaction.setTransactionDate(
                java.time.LocalDateTime.now());

        transactionRepository.save(transaction);

    }
}
