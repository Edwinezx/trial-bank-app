package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.dto.request.NewAccountRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.entity.AccountType;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.exception.AccountTypeNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.AccountTypeRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.AccountService;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountTypeRepository accountTypeRepository;
    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    public BankResponse createAccount(NewAccountRequest newAccountRequest, String userEmail) {

        User user = userRepository.findByEmail(userEmail);

        AccountType accountType = accountTypeRepository
                .findByTypeNameIgnoreCase(newAccountRequest.getAccountType())
                .orElseThrow(() -> new AccountTypeNotFoundException(
                        "Unknown account type: " + newAccountRequest.getAccountType()));

        if (accountRepository.existsByUserAndAccountType(user, accountType)) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXISTS_MSG)
                    .build();
        }

        String accountNumber = switch (accountType.getTypeName().toUpperCase()) {
            case "SAVINGS" -> AccountUtils.generateSavingsAccountNumber();
            case "CURRENT" -> AccountUtils.generateCurrentAccountNumber();
            case "FIXED DEPOSIT" -> AccountUtils.generateFixedAccountNumber();
            default -> throw new IllegalStateException("Unexpected value: " + accountType.getTypeName());
        };

        Account savedAccount = accountRepository.save(Account.builder()
                .accountNumber(accountNumber)
                .availableBalance(BigDecimal.ZERO)
                .ledgerBalance(BigDecimal.ZERO)
                .accountType(accountType)
                .status(AccountStatus.ACTIVE)
                .user(user)
                .build());

        auditService.log("ACCOUNT_CREATED", user.getEmail(), "SUCCESS",
                "Account " + accountNumber + " created", accountNumber);

        sendEmail(user.getEmail(), "ACCOUNT CREATION",
                "Your " + accountType.getTypeName() + " account has been created.\n" +
                        "Account Number: " + savedAccount.getAccountNumber() + "\n" +
                        "Name: " + user.getLastName() + " " + user.getFirstName());

        return successResponse(AccountUtils.ACCOUNT_CREATION_SUCCESS,
                AccountUtils.ACCOUNT_CREATION_MSG, savedAccount);
    }

    @Override
    public BankResponse closeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
        auditService.log("ACCOUNT_CLOSED", account.getUser().getEmail(), "SUCCESS",
                "Account closed", account.getAccountNumber());
        return successResponse(AccountUtils.ACCOUNT_CLOSURE_SUCCESS_CODE,
                AccountUtils.ACCOUNT_CLOSURE_SUCCESS_MSG, account);
    }

    @Override
    public BankResponse freezeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setStatus(AccountStatus.FROZEN);
        accountRepository.save(account);
        auditService.log("ACCOUNT_FROZEN", account.getUser().getEmail(), "SUCCESS",
                "Account frozen", accountNumber);
        sendEmail(account.getUser().getEmail(), "Account Frozen",
                "Your account " + accountNumber + " has been frozen. Contact support if this was not requested.");
        return successResponse("016", "Account frozen successfully", account);
    }

    @Override
    public BankResponse unfreezeAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        auditService.log("ACCOUNT_UNFROZEN", account.getUser().getEmail(), "SUCCESS",
                "Account unfrozen", accountNumber);
        sendEmail(account.getUser().getEmail(), "Account Reactivated",
                "Your account " + accountNumber + " has been reactivated.");
        return successResponse("017", "Account unfrozen successfully", account);
    }

    @Override
    public BankResponse markDormant(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setStatus(AccountStatus.DORMANT);
        accountRepository.save(account);
        auditService.log("ACCOUNT_DORMANT", account.getUser().getEmail(), "SUCCESS",
                "Account marked dormant", accountNumber);
        sendEmail(account.getUser().getEmail(), "Account Dormant",
                "Your account " + accountNumber + " has been marked dormant due to inactivity.");
        return successResponse("018", "Account marked as dormant", account);
    }

    @Override
    public BankResponse activateAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        auditService.log("ACCOUNT_ACTIVE", account.getUser().getEmail(), "SUCCESS",
                "Account marked active", accountNumber);
        sendEmail(account.getUser().getEmail(), "Account Activated",
                "Your account " + accountNumber + " has been successfully activated.");
        return successResponse("018", "Account activated", account);
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private Account findAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
    }

    private BankResponse successResponse(String code, String msg, Account account) {
        return BankResponse.builder()
                .responseCode(code)
                .responseMessage(msg)
                .accountInfo(toAccountInfo(account))
                .build();
    }

    private AccountInfo toAccountInfo(Account account) {
        return AccountInfo.builder()
                .accountNumber(account.getAccountNumber())
                .accountName(account.getUser().getFirstName() + " "
                        + account.getUser().getOtherName() + " "
                        + account.getUser().getLastName())
                .availableBalance(account.getAvailableBalance())
                .accountType(account.getAccountType().getTypeName())
                .build();
    }

    private void sendEmail(String email, String subject, String body) {
        if (email != null && !email.isBlank()) {
            emailService.sendEmailAlert(EmailDetails.builder()
                    .recipientEmail(email)
                    .subject(subject)
                    .messageBody(body)
                    .build());
        }
    }
}
