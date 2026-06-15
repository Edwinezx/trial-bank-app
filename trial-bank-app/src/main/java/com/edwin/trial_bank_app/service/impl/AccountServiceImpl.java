package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.dto.request.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.enums.AccountType;
import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.AccountService;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditService auditService;

    @Override
    public BankResponse registerAccount(UserRequest userRequest) {
        AccountType accountType = userRequest.getAccountType();
        User foundUser = userRepository.findByEmail(userRequest.getEmail());
        User savedUser;

        if (foundUser != null) {
            if (accountRepository.existsByUserAndAccountType(foundUser, accountType)) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MSG)
                        .build();
            }
            savedUser = foundUser;
        } else {
            savedUser = userRepository.save(User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .password(passwordEncoder.encode(userRequest.getPassword()))
                    .role(Roles.ROLE_USER)
                    .build());
        }

        String accountNumber = switch (accountType) {
            case SAVINGS -> AccountUtils.generateSavingsAccountNumber();
            case CURRENT -> AccountUtils.generateCurrentAccountNumber();
            case FIXED   -> AccountUtils.generateFixedAccountNumber();
        };

        Account savedAccount = accountRepository.save(Account.builder()
                .accountNumber(accountNumber)
                .availableBalance(BigDecimal.ZERO)
                .ledgerBalance(BigDecimal.ZERO)
                .accountType(accountType)
                .status(AccountStatus.ACTIVE)
                .user(savedUser)
                .build());

        auditService.log("ACCOUNT_CREATED", savedUser.getEmail(), "SUCCESS",
                "Account " + accountNumber + " created", accountNumber);

        sendEmail(savedUser.getEmail(), "ACCOUNT CREATION",
                "Your " + accountType + " account has been created.\n" +
                "Account Number: " + savedAccount.getAccountNumber() + "\n" +
                "Name: " + savedUser.getLastName() + " " + savedUser.getFirstName());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MSG)
                .accountInfo(toAccountInfo(savedAccount))
                .build();
    }

    @Override
    public BankResponse closeAccount(CloseAccountRequest request) {
        Account account = findAccount(request.getAccountNumber());
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
                .accountType(account.getAccountType())
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
