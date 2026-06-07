package com.edwin.trial_bank_app.account.service.impl;

import com.edwin.trial_bank_app.account.service.AccountService;
import com.edwin.trial_bank_app.dto.*;
import com.edwin.trial_bank_app.email.service.EmailService;
import com.edwin.trial_bank_app.entity.*;
import com.edwin.trial_bank_app.enums.*;
import com.edwin.trial_bank_app.repository.*;
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


    @Override
    public BankResponse registerAccount(UserRequest userRequest) {
        AccountType accountType = userRequest.getAccountType();

        // Check if user exists
        User foundUser = userRepository.findByEmail(userRequest.getEmail());
        User savedUser;

        if (foundUser != null) {
            boolean accountExists = accountRepository.existsByUserAndAccountType(foundUser, accountType);
            if (accountExists) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MSG)
                        .accountInfo(null)
                        .build();
            }
            savedUser = foundUser;
        } else {
            // Create new user
            String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
            User newUser = User.builder()
                    .firstName(userRequest.getFirstName())
                    .lastName(userRequest.getLastName())
                    .otherName(userRequest.getOtherName())
                    .gender(userRequest.getGender())
                    .address(userRequest.getAddress())
                    .stateOfOrigin(userRequest.getStateOfOrigin())
                    .email(userRequest.getEmail())
                    .phoneNumber(userRequest.getPhoneNumber())
                    .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                    .password(encodedPassword)
                    .build();

            savedUser = userRepository.save(newUser);
        }

        // Generate account number based on type
        String accountNumber = switch (accountType) {
            case SAVINGS -> AccountUtils.generateSavingsAccountNumber();
            case CURRENT -> AccountUtils.generateCurrentAccountNumber();
            case FIXED -> AccountUtils.generateFixedAccountNumber();
        };

        Account newAccount = Account.builder()
                .accountNumber(accountNumber)
                .accountBalance(BigDecimal.ZERO)
                .accountType(accountType)
                .status(AccountStatus.ACTIVE)
                .user(savedUser)
                .build();

        Account savedAccount = accountRepository.save(newAccount);

        if (savedUser.getEmail() != null && !savedUser.getEmail().isBlank()) {

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipientEmail(savedUser.getEmail())
                    .messageBody(
                            "Your Account Has Been Successfully Created. \nYour Account Details: \n" +
                                    "Account name: " + savedUser.getLastName() + " " + savedUser.getFirstName() + " " + savedUser.getOtherName() +
                                    "\nAccount Type: " + savedAccount.getAccountType() +
                                    "\nAccount Number: " + savedAccount.getAccountNumber() +
                                    "\nAccount Balance: ₦" + savedAccount.getAccountBalance()
                    )
                    .subject("ACCOUNT CREATION")
                    .build();

            emailService.sendEmailAlert(emailDetails);

        } else {
            System.out.println("Warning: User email is null or empty. Skipping email alert.");
        }

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(savedAccount.getAccountNumber())
                        .accountName(savedAccount.getUser().getFirstName() + " "+savedAccount.getUser().getOtherName()+" " + savedAccount.getUser().getLastName())
                        .accountBalance(savedAccount.getAccountBalance())
                        .accountType(savedAccount.getAccountType())
                        .build())
                .build();
    }

    @Override
    public BankResponse closeAccount(CloseAccountRequest request) {
        Account foundAccount = accountRepository.findByAccountNumber(request.getAccountNumber());

        if (foundAccount == null) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }

        foundAccount.setStatus(AccountStatus.valueOf("CLOSED"));
        accountRepository.save(foundAccount);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CLOSURE_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CLOSURE_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountNumber(foundAccount.getAccountNumber())
                        .accountName(foundAccount.getUser().getFirstName() + " " + foundAccount.getUser().getLastName())
                        .accountBalance(foundAccount.getAccountBalance())
                        .accountType(foundAccount.getAccountType())
                        .build())
                .build();
    }
}
