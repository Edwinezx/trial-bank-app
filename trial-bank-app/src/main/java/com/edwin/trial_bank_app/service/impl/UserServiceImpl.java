package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.AccountType;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.AccountStatus;
import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.exception.AccountTypeNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.UserServices;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServices {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    public BankResponse onboardNewUser(UserRequest userRequest) {

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
                .user(savedUser)
                .build());

        auditService.log("ACCOUNT_CREATED", savedUser.getEmail(), "SUCCESS",
                "Account " + accountNumber + " created", accountNumber);

        sendEmail(savedUser.getEmail(), "ACCOUNT CREATION",
                "Your " + accountType.getTypeName() + " account has been created.\n" +
                        "Account Number: " + savedAccount.getAccountNumber() + "\n" +
                        "Name: " + savedUser.getLastName() + " " + savedUser.getFirstName());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MSG)
                .accountInfo(toAccountInfo(savedAccount))
                .build();
    }
}
