package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import com.edwin.trial_bank_app.utils.TransactionUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Getter
@RequiredArgsConstructor
public class TransactionRecordServiceImpl
        implements TransactionRecordService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction recordTransaction(
            String source,
            String destination,
            BigDecimal amount,
            TransactionType type,
            TransactionStatus status) {

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                TransactionUtils.generateReference());

        transaction.setSourceAccountNumber(source);

        transaction.setDestinationAccountNumber(destination);

        transaction.setAmount(amount);

        transaction.setTransactionType(type);

        transaction.setStatus(status);

        transaction.setTransactionDate(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}