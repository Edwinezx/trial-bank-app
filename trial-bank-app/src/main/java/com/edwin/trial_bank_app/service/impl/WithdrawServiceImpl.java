package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.WithdrawRequest;
import com.edwin.trial_bank_app.entity.Account;
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
public class WithdrawServiceImpl implements WithdrawService {

    private final AccountRepository        accountRepository;
    private final AccountValidationService validationService;
    private final FraudDetectionService    fraudDetectionService;
    private final TransactionRecordService transactionRecordService;
    private final AuditService             auditService;

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

            transactionRecordService.recordTransaction(
                    account.getAccountNumber(),
                    null,
                    amount,
                    "Cash withdrawal",
                    TransactionType.WITHDRAWAL,
                    TransactionStatus.SUCCESS
            );

            // REQUIRES_NEW in AuditServiceImpl — persists even if main tx rolls back
            auditService.log("WITHDRAWAL", account.getUser().getEmail(), "SUCCESS",
                    "Withdrew ₦" + amount, account.getAccountNumber());

        } catch (Exception ex) {
            // REQUIRES_NEW — this persists even though the main tx will roll back
            auditService.log("WITHDRAWAL", account.getUser().getEmail(), "FAILED",
                    ex.getMessage(), account.getAccountNumber());
            throw ex;
        }
    }
}
