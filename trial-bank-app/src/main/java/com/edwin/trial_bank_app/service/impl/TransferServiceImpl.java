package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.service.*;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.utils.AccountUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;

    private final AccountValidationService validationService;

    private final TransactionRecordService transactionRecordService;

    private final TransactionNotificationService notificationService;

    @Transactional
    @Override
    public MultiAccountBankResponse transferFunds(
            String sourceAccountNumber,
            String destinationAccountNumber,
            BigDecimal amount) {

        Account source =
                accountRepository.findByAccountNumber(
                        sourceAccountNumber);

        Account destination =
                accountRepository.findByAccountNumber(
                        destinationAccountNumber);

        validationService.validateTransfer(
                source,
                destination,
                amount);

        source.setAccountBalance(
                source.getAccountBalance()
                        .subtract(amount));

        destination.setAccountBalance(
                destination.getAccountBalance()
                        .add(amount));

        accountRepository.save(source);
        accountRepository.save(destination);

        Transaction transaction =
                transactionRecordService.recordTransaction(
                        sourceAccountNumber,
                        destinationAccountNumber,
                        amount,
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

        return MultiAccountBankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(
                        "Transfer successful")
                .accountInfo(
                List.of(
                        AccountUtils.mapToAccountInfo(source)
                )
        )
                .build();
    }
}