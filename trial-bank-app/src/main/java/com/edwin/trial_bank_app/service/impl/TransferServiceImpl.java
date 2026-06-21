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

/**
 * Orchestrates a transfer between two accounts.

 * Auditing and notifications are NOT called directly from here.
 * Instead this service publishes TransferCompletedEvent / TransferFailedEvent
 * and lets listeners (AuditEventListener, TransferNotificationListener) react.
 * See those classes for why AFTER_COMMIT / AFTER_ROLLBACK phases matter.
 */
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
    public TransferResponse transferFunds(TransferRequest request) {
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
                    TransactionStatus.SUCCESS
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

            return new TransferResponse(
                    transaction.getTransactionReference(),
                    source.getAccountNumber(),
                    destination.getAccountNumber(),
                    amount,
                    TransactionStatus.SUCCESS,
                    transaction.getCreatedAt()
            );

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
}
