package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private final TransactionRepository transactionRepository;

    @Override
    public Transaction recordTransaction(
            String sourceAccount,
            String destinationAccount,
            BigDecimal amount,
            String narration,
            TransactionType type,
            TransactionStatus status
    ) {

        Transaction transaction = new Transaction();

        transaction.setSourceAccountNumber(sourceAccount);
        transaction.setDestinationAccountNumber(destinationAccount);
        transaction.setAmount(amount);
        transaction.setNarration(narration);
        transaction.setTransactionType(type);
        transaction.setStatus(status);
        transaction.setTransactionReference(UUID.randomUUID().toString());
        transaction.setCreatedAt(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
}