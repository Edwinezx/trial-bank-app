package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.response.TransactionStatementResponse;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import com.edwin.trial_bank_app.utils.TransactionReferenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl implements TransactionRecordService {

    private final TransactionRepository transactionRepository;
    private final TransactionReferenceGenerator referenceGenerator;

    @Override
    public TransactionStatementResponse getTransactionHistory(String accountNumber, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Transaction> result = transactionRepository.findByAccount(accountNumber, pageable);

        return TransactionStatementResponse.builder()
                .accountNumber(accountNumber)
                .page(result.getNumber())
                .pageSize(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .transactions(result.getContent())
                .build();
    }

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
        transaction.setNarration(narration != null ? narration : "");
        transaction.setTransactionType(type);
        transaction.setStatus(status);
        transaction.setTransactionReference(referenceGenerator.generate(type));

        return transactionRepository.save(transaction);
    }
}
