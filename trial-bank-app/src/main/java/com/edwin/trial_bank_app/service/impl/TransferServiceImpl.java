package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.response.TransferResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository              accountRepository;
    private final AccountValidationService       validationService;
    private final FraudDetectionService          fraudDetectionService;
    private final TransactionRecordService       transactionRecordService;
    private final TransactionNotificationService notificationService;
    private final AuditService                   auditService;

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

            // Record transaction and send notifications inside the try block
            // so any failure here is also caught and audited
            Transaction transaction = transactionRecordService.recordTransaction(
                    source.getAccountNumber(),
                    destination.getAccountNumber(),
                    amount,
                    request.getNarration(),
                    TransactionType.TRANSFER,
                    TransactionStatus.SUCCESS
            );

            // Audit SUCCESS — runs in its own tx (REQUIRES_NEW), survives any rollback
            auditService.log("TRANSFER", source.getUser().getEmail(), "SUCCESS",
                    "Transferred ₦" + amount + " to " + destination.getAccountNumber(),
                    source.getAccountNumber());

            // Notifications are best-effort — log but don't fail the transfer
            try {
                notificationService.sendDebitAlert(source, amount, transaction.getTransactionReference());
                notificationService.sendCreditAlert(destination, amount, transaction.getTransactionReference());
            } catch (Exception emailEx) {
                auditService.log("NOTIFICATION_FAILED", source.getUser().getEmail(), "WARN",
                        "Email notification failed: " + emailEx.getMessage(),
                        source.getAccountNumber());
            }

            return new TransferResponse(
                    transaction.getTransactionReference(),
                    source.getAccountNumber(),
                    destination.getAccountNumber(),
                    amount,
                    TransactionStatus.SUCCESS,
                    transaction.getCreatedAt()
            );

        } catch (Exception ex) {
            // Audit FAILED — also runs in REQUIRES_NEW, so this persists even after rollback
            auditService.log("TRANSFER", source.getUser().getEmail(), "FAILED",
                    ex.getMessage(), source.getAccountNumber());
            throw ex;
        }
    }
}
