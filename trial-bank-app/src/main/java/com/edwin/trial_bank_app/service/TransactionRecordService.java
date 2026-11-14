package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.response.TransactionStatementResponse;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionRecordService {

    TransactionStatementResponse getTransactionHistory(String accountNumber, int page, int size);

    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    Transaction recordTransaction(
            String source,
            String destination,
            BigDecimal amount,
            String narration,
            TransactionType type,
            TransactionStatus status
    );

    Transaction recordTransaction(
            String source,
            String destination,
            BigDecimal amount,
            String narration,
            TransactionType type,
            TransactionStatus status,
            String idempotencyKey
    );
}
