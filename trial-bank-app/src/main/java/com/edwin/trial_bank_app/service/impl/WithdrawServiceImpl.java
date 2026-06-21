package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.WithdrawRequest;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.event.WithdrawalCompletedEvent;
import com.edwin.trial_bank_app.event.WithdrawalFailedEvent;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.FraudDetectionService;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import com.edwin.trial_bank_app.service.WithdrawService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private final AccountRepository         accountRepository;
    private final AccountValidationService  validationService;
    private final FraudDetectionService     fraudDetectionService;
    private final TransactionRecordService  transactionRecordService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public void withdrawMoney(WithdrawRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + request.getSourceAccountNumber()));

        BigDecimal amount = request.getAmount();

        try {
            validationService.validateWithdrawal(account, amount);
            validationService.validateOwnership(account);
            fraudDetectionService.assess(account, amount);

            account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
            accountRepository.save(account);

            Transaction transaction = transactionRecordService.recordTransaction(
                    account.getAccountNumber(),
                    null,
                    amount,
                    "Cash withdrawal",
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.SUCCESS
            );

            eventPublisher.publishEvent(new WithdrawalCompletedEvent(
                    transaction.getTransactionReference(),
                    account.getAccountNumber(),
                    account.getUser().getEmail(),
                    amount,
                    account.getAvailableBalance(),
                    LocalDateTime.now()
            ));

        } catch (Exception ex) {
            eventPublisher.publishEvent(new WithdrawalFailedEvent(
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
