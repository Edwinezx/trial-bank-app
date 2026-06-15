package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(String reference);

    // paginated — used for statements
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.sourceAccountNumber = :acct
           OR t.destinationAccountNumber = :acct
        ORDER BY t.createdAt DESC
        """)
    Page<Transaction> findByAccount(@Param("acct") String accountNumber, Pageable pageable);

    // filtered by type
    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccountNumber = :acct OR t.destinationAccountNumber = :acct)
          AND t.transactionType = :type
        ORDER BY t.createdAt DESC
        """)
    Page<Transaction> findByAccountAndType(
            @Param("acct") String accountNumber,
            @Param("type") TransactionType type,
            Pageable pageable);

    // date-range filter for fraud detection
    @Query("""
        SELECT t FROM Transaction t
        WHERE (t.sourceAccountNumber = :acct OR t.destinationAccountNumber = :acct)
          AND t.createdAt BETWEEN :from AND :to
        ORDER BY t.createdAt DESC
        """)
    List<Transaction> findByAccountAndDateRange(
            @Param("acct") String accountNumber,
            @Param("from") LocalDateTime from,
            @Param("to")   LocalDateTime to);

    // for duplicate detection
    boolean existsByTransactionReference(String reference);

    // rapid-fire detection: count source transactions in window
    @Query("""
        SELECT COUNT(t) FROM Transaction t
        WHERE t.sourceAccountNumber = :acct
          AND t.createdAt >= :since
        """)
    long countRecentDebits(@Param("acct") String accountNumber, @Param("since") LocalDateTime since);
}
