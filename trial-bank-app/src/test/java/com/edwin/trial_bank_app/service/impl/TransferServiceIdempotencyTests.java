package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.response.TransferResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.event.TransferCompletedEvent;
import com.edwin.trial_bank_app.exception.DuplicateTransactionException;
import com.edwin.trial_bank_app.exception.InvalidIdempotencyKeyException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.FraudDetectionService;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceIdempotencyTests {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidationService validationService;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private TransactionRecordService transactionRecordService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private TransferServiceImpl transferService;

    @Test
    void matchingRetryReturnsOriginalTransactionWithoutMovingMoneyAgain() {
        TransferRequest request = request("1000000001", "1000000002", "2500.00");
        Transaction existing = transaction("retry-key", request, "TRF-original");
        Account source = account("1000000001", "owner@example.com", "10000.00");

        when(transactionRecordService.findByIdempotencyKey("retry-key"))
                .thenReturn(Optional.of(existing));
        when(accountRepository.findByAccountNumber("1000000001"))
                .thenReturn(Optional.of(source));

        TransferResponse response = transferService.transferFunds(request, " retry-key ");

        assertEquals("TRF-original", response.getTransactionReference());
        assertEquals(new BigDecimal("10000.00"), source.getAvailableBalance());
        verify(validationService).validateOwnership(source);
        verify(accountRepository, never()).save(any(Account.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void reusingKeyForDifferentTransferReturnsConflict() {
        TransferRequest originalRequest = request("1000000001", "1000000002", "2500.00");
        TransferRequest changedRequest = request("1000000001", "1000000002", "3000.00");
        Transaction existing = transaction("retry-key", originalRequest, "TRF-original");
        Account source = account("1000000001", "owner@example.com", "10000.00");

        when(transactionRecordService.findByIdempotencyKey("retry-key"))
                .thenReturn(Optional.of(existing));
        when(accountRepository.findByAccountNumber("1000000001"))
                .thenReturn(Optional.of(source));

        assertThrows(
                DuplicateTransactionException.class,
                () -> transferService.transferFunds(changedRequest, "retry-key")
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void newKeyProcessesTransferAndStoresKey() {
        TransferRequest request = request("1000000001", "1000000002", "2500.00");
        Account source = account("1000000001", "owner@example.com", "10000.00");
        Account destination = account("1000000002", "recipient@example.com", "1000.00");
        Transaction saved = transaction("new-key", request, "TRF-new");

        when(transactionRecordService.findByIdempotencyKey("new-key"))
                .thenReturn(Optional.empty());
        when(accountRepository.findByAccountNumber("1000000001"))
                .thenReturn(Optional.of(source));
        when(accountRepository.findByAccountNumber("1000000002"))
                .thenReturn(Optional.of(destination));
        when(transactionRecordService.recordTransaction(
                "1000000001",
                "1000000002",
                new BigDecimal("2500.00"),
                "Portfolio transfer",
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "new-key"
        )).thenReturn(saved);

        TransferResponse response = transferService.transferFunds(request, "new-key");

        assertEquals("TRF-new", response.getTransactionReference());
        assertEquals(new BigDecimal("7500.00"), source.getAvailableBalance());
        assertEquals(new BigDecimal("3500.00"), destination.getAvailableBalance());
        verify(transactionRecordService).recordTransaction(
                "1000000001",
                "1000000002",
                new BigDecimal("2500.00"),
                "Portfolio transfer",
                TransactionType.TRANSFER,
                TransactionStatus.SUCCESS,
                "new-key"
        );
        verify(eventPublisher).publishEvent(any(TransferCompletedEvent.class));
    }

    @Test
    void blankKeyIsRejectedBeforeAnyDatabaseWork() {
        TransferRequest request = request("1000000001", "1000000002", "2500.00");

        assertThrows(
                InvalidIdempotencyKeyException.class,
                () -> transferService.transferFunds(request, "   ")
        );

        verify(transactionRecordService, never()).findByIdempotencyKey(any());
        verify(accountRepository, never()).findByAccountNumber(any());
    }

    private TransferRequest request(String source, String destination, String amount) {
        return TransferRequest.builder()
                .sourceAccountNumber(source)
                .destinationAccountNumber(destination)
                .amount(new BigDecimal(amount))
                .narration("Portfolio transfer")
                .build();
    }

    private Account account(String accountNumber, String email, String balance) {
        return Account.builder()
                .accountNumber(accountNumber)
                .availableBalance(new BigDecimal(balance))
                .ledgerBalance(new BigDecimal(balance))
                .status(AccountStatus.ACTIVE)
                .user(User.builder().email(email).build())
                .build();
    }

    private Transaction transaction(
            String key,
            TransferRequest request,
            String reference
    ) {
        Transaction transaction = new Transaction();
        transaction.setIdempotencyKey(key);
        transaction.setTransactionReference(reference);
        transaction.setSourceAccountNumber(request.getSourceAccountNumber());
        transaction.setDestinationAccountNumber(request.getDestinationAccountNumber());
        transaction.setAmount(request.getAmount());
        transaction.setNarration(request.getNarration());
        transaction.setTransactionType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(LocalDateTime.now());
        return transaction;
    }
}
