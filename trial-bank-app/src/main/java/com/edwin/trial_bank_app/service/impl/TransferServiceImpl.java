package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.response.TransferResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.event.TransferCompletedEvent;
import com.edwin.trial_bank_app.event.TransferFailedEvent;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.exception.DuplicateTransactionException;
import com.edwin.trial_bank_app.exception.InvalidIdempotencyKeyException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.FraudDetectionService;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import com.edwin.trial_bank_app.service.TransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository         accountRepository;
    private final AccountValidationService  validationService;
    private final FraudDetectionService     fraudDetectionService;
    private final TransactionRecordService  transactionRecordService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public TransferResponse transferFunds(TransferRequest request, String idempotencyKey) {
        String normalizedKey = normalizeIdempotencyKey(idempotencyKey);

        Transaction existingTransaction = transactionRecordService
                .findByIdempotencyKey(normalizedKey)
                .orElse(null);

        if (existingTransaction != null) {
            return handleRepeatedRequest(request, existingTransaction, normalizedKey);
        }

        Account source = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        Account destination = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));

        BigDecimal amount = request.getAmount();

        try {
            validationService.validateTransfer(source, destination, amount);
            fraudDetectionService.assess(source, amount);

            source.setAvailableBalance(source.getAvailableBalance().subtract(amount));
            destination.setAvailableBalance(destination.getAvailableBalance().add(amount));
            accountRepository.save(source);
            accountRepository.save(destination);

            Transaction transaction = transactionRecordService.recordTransaction(
                    source.getAccountNumber(),
                    destination.getAccountNumber(),
                    amount,
                    request.getNarration(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS,
                    normalizedKey
            );

            // Published now, delivered to listeners only AFTER this transaction commits.
            eventPublisher.publishEvent(new TransferCompletedEvent(
                    transaction.getTransactionReference(),
                    source.getAccountNumber(),
                    source.getUser().getEmail(),
                    source.getAvailableBalance(),
                    destination.getAccountNumber(),
                    destination.getUser().getEmail(),
                    destination.getAvailableBalance(),
                    amount,
                    LocalDateTime.now()
            ));

            return toTransferResponse(transaction);

        } catch (Exception ex) {
            // The @Transactional method is about to roll back, so there will be no
            // commit for an AFTER_COMMIT listener to react to. The failure listener
            // is registered for AFTER_ROLLBACK instead — see AuditEventListener.
            eventPublisher.publishEvent(new TransferFailedEvent(
                    source.getAccountNumber(),
                    source.getUser().getEmail(),
                    request.getDestinationAccountNumber(),
                    amount,
                    ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        }
    }

    private String normalizeIdempotencyKey(String key) {
        if (key == null || key.isBlank()) {
            throw new InvalidIdempotencyKeyException(
                    "Idempotency-Key header is required"
            );
        }

        String normalizedKey = key.trim();

        if (normalizedKey.length() > 100) {
            throw new InvalidIdempotencyKeyException(
                    "Idempotency-Key must not exceed 100 characters"
            );
        }

        return normalizedKey;
    }

    private TransferResponse handleRepeatedRequest(
            TransferRequest request,
            Transaction existingTransaction,
            String idempotencyKey
    ) {
        Account originalSource = accountRepository
                .findByAccountNumber(existingTransaction.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));

        validationService.validateOwnership(originalSource);

        if (!sameTransfer(existingTransaction, request)) {
            throw new DuplicateTransactionException(idempotencyKey);
        }

        return toTransferResponse(existingTransaction);
    }

    private boolean sameTransfer(
            Transaction transaction,
            TransferRequest request
    ) {
        boolean sameAmount = transaction.getAmount() != null
                && request.getAmount() != null
                && transaction.getAmount().compareTo(request.getAmount()) == 0;

        return Objects.equals(
                        transaction.getSourceAccountNumber(),
                        request.getSourceAccountNumber()
                )
                && Objects.equals(
                        transaction.getDestinationAccountNumber(),
                        request.getDestinationAccountNumber()
                )
                && sameAmount
                && Objects.equals(
                        transaction.getNarration(),
                        request.getNarration()
                )
                && transaction.getTransactionType() == TransactionType.TRANSFER;
    }

    private TransferResponse toTransferResponse(Transaction transaction) {
        return TransferResponse.builder()
                .transactionReference(transaction.getTransactionReference())
                .sourceAccount(transaction.getSourceAccountNumber())
                .destinationAccount(transaction.getDestinationAccountNumber())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .timestamp(transaction.getCreatedAt())
                .build();
    }
}
