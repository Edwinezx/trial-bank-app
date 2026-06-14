package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.response.TransferResponse;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.service.*;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;

    private final AccountValidationService validationService;

    private final TransactionRecordService transactionRecordService;

    private final TransactionNotificationService notificationService;

    @Transactional
    @Override
    public TransferResponse transferFunds(TransferRequest request) {

        Account source = accountRepository.findByAccountNumber(
                request.getSourceAccountNumber()
        ).orElseThrow(() ->
                new AccountNotFoundException("Source account not found")
        );

        Account destination = accountRepository.findByAccountNumber(
                request.getDestinationAccountNumber()
        ).orElseThrow(() ->
                new AccountNotFoundException("Destination account not found")
        );

        BigDecimal amount = request.getAmount();

        String narration = request.getNarration();

        validationService.validateTransfer(source, destination, amount);

        source.setAvailableBalance(
                source.getAvailableBalance().subtract(amount)
        );

        destination.setAvailableBalance(
                destination.getAvailableBalance().add(amount)
        );

        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction transaction =
                transactionRecordService.recordTransaction(
                        source.getAccountNumber(),
                        destination.getAccountNumber(),
                        amount,
                        narration,
                        TransactionType.TRANSFER,
                        TransactionStatus.SUCCESS
                );

        notificationService.sendDebitAlert(
                source,
                amount,
                transaction.getTransactionReference()
        );

        notificationService.sendCreditAlert(
                destination,
                amount,
                transaction.getTransactionReference()
        );

        return new TransferResponse(
                transaction.getTransactionReference(),
                source.getAccountNumber(),
                destination.getAccountNumber(),
                amount,
                TransactionStatus.SUCCESS,
                transaction.getCreatedAt()
        );
    }
}