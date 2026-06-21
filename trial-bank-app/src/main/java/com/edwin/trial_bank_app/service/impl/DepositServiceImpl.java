package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.DepositRequest;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.event.DepositCompletedEvent;
import com.edwin.trial_bank_app.event.DepositFailedEvent;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.DepositService;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final AccountRepository         accountRepository;
    private final AccountValidationService  validationService;
    private final TransactionRecordService  transactionRecordService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void depositMoney(DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getDestinationAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + request.getDestinationAccountNumber()));

        BigDecimal amount = request.getAmount();

        try {
            validationService.validateDeposit(account, amount);

            account.setAvailableBalance(account.getAvailableBalance().add(amount));
            accountRepository.save(account);

            Transaction transaction = transactionRecordService.recordTransaction(
                    null,
                    account.getAccountNumber(),
                    amount,
                    "Cash deposit",
                    TransactionType.DEPOSIT,
                    TransactionStatus.SUCCESS
            );

            eventPublisher.publishEvent(new DepositCompletedEvent(
                    transaction.getTransactionReference(),
                    account.getAccountNumber(),
                    account.getUser().getEmail(),
                    amount,
                    account.getAvailableBalance(),
                    LocalDateTime.now()
            ));

        } catch (Exception ex) {
            eventPublisher.publishEvent(new DepositFailedEvent(
                    account.getAccountNumber(),
                    account.getUser().getEmail(),
                    amount,
                    ex.getMessage(),
                    LocalDateTime.now()
            ));
            throw ex;
        }
    }
}
