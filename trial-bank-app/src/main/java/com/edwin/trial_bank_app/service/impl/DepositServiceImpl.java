package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.DepositRequest;
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
public class DepositServiceImpl implements DepositService {

    private final AccountRepository        accountRepository;
    private final AccountValidationService validationService;
    private final TransactionRecordService transactionRecordService;
    private final AuditService             auditService;

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

            transactionRecordService.recordTransaction(
                    null,
                    account.getAccountNumber(),
                    amount,
                    "Cash deposit",
                    TransactionType.DEPOSIT,
                    TransactionStatus.SUCCESS
            );

            // REQUIRES_NEW in AuditServiceImpl — persists even if main tx rolls back
            auditService.log("DEPOSIT", account.getUser().getEmail(), "SUCCESS",
                    "Deposited ₦" + amount, account.getAccountNumber());

        } catch (Exception ex) {
            // REQUIRES_NEW — this persists even though the main tx will roll back
            auditService.log("DEPOSIT", account.getUser().getEmail(), "FAILED",
                    ex.getMessage(), account.getAccountNumber());
            throw ex;
        }
    }
}
