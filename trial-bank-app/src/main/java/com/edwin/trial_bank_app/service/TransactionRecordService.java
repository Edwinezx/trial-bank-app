package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionRecordService {

    List<Transaction> getTransactionHistory(
            String accountNumber
    );

    Transaction recordTransaction(
            String source,
            String destination,
            BigDecimal amount,
            String narration,
            TransactionType type,
            TransactionStatus status

        );

}
