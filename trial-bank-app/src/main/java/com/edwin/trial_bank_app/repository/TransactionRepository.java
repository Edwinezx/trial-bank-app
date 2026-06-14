package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccountNumber(String accountNumber);
    List<Transaction> findByDestinationAccountNumber(String accountNumber);
    List<Transaction> findBySourceAccountNumberOrDestinationAccountNumber(
            String source,
            String destination
    );
}