package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.*;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.accountRepository;
import com.edwin.trial_bank_app.repository.userRepository;
import com.edwin.trial_bank_app.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    userRepository userRepository;

    @Autowired
    accountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EmailService emailService;

    @Override
    public BankResponse createAccount(UserRequest userRequest, AccountRequest accountRequest) {

        /*
         * Creating an account - saving a new user into the db
         * check if user already has an account created with a particular phone number
         */

        //create holding values for entities to equate later
        User savedUser = null;
        Account savedAccount = null;

        AccountType accountType = accountRequest.getAccountType();

        //create found user to be used for checking account exists
        User foundUser = userRepository.findByEmail(userRequest.getEmail());

        if (foundUser != null) {

            boolean accountExists = accountRepository
                    .existsByUserAndAccountType(foundUser, accountType);

            if (accountExists) {
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_EXISTS_MSG)
                        .accountInfo(null)
                        .build();
            }

            savedUser = foundUser;

            String accountNumber = switch (accountType) {
                case SAVINGS -> AccountUtils.generateSavingsAccountNumber();
                case CURRENT -> AccountUtils.generateCurrentAccountNumber();
                case FIXED -> AccountUtils.generateFixedAccountNumber();
            };

            Account account = Account.builder()
                    .accountNumber(accountNumber)
                    .accountBalance(BigDecimal.ZERO)
                    .accountType(accountType)
                    .status("ACTIVE")
                    .user(foundUser)
                    .build();

            savedAccount = accountRepository.save(account);
        }

        // TODO
        // 1. Check if the type is either current, savings or fixed
        // 2. Check if the user has that particular account
        // 3. Create account for that user if not
        String encodedPassword = passwordEncoder.encode(userRequest.getPassword());
        if (foundUser == null) {

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

            if (accountType.isSavingsAccount()) {
                Account newAccount = Account.builder()
                        .accountNumber(AccountUtils.generateSavingsAccountNumber())
                        .accountBalance(BigDecimal.ZERO)
                        .accountType(accountType)
                        .status("ACTIVE")
                        .user(savedUser)
                        .build();

                savedAccount = accountRepository.save(newAccount);
            }

            if (accountType.isCurrentAccount()) {
                Account newAccount = Account.builder()
                        .accountNumber(AccountUtils.generateCurrentAccountNumber())
                        .accountBalance(BigDecimal.ZERO)
                        .accountType(accountType)
                        .status("ACTIVE")
                        .user(savedUser)
                        .build();

                savedAccount = accountRepository.save(newAccount);
            }

            if (accountType.isFixedAccount()) {
                Account newAccount = Account.builder()
                        .accountNumber(AccountUtils.generateFixedAccountNumber())
                        .accountBalance(BigDecimal.ZERO)
                        .accountType(accountType)
                        .status("ACTIVE")
                        .user(savedUser)
                        .build();

                savedAccount = accountRepository.save(newAccount);
            }
        }

        //send email alert
        if (savedUser.getEmail() != null && !savedUser.getEmail().isBlank()) {

            assert savedAccount != null;
            EmailDetails emailDetails = EmailDetails.builder()
                    .recipientEmail(savedUser.getEmail())
                    .messageBody(
                            "Your Account Has Been Successfully Created. \nYour Account Details: \n" +
                                    "Account name: " + savedUser.getLastName() + " " + savedUser.getFirstName() + " " + savedUser.getOtherName() +
                                    "\nAccount Type: " + savedAccount.getAccountType() +
                                    "\nAccount Number: " + savedAccount.getAccountNumber() +
                                    "\nAccount Balance: ₦" + savedAccount.getAccountBalance()
                    )
                    .Subject("ACCOUNT CREATION")
                    .build();

            emailService.sendEmailAlert(emailDetails);

        } else {
            System.out.println("Warning: User email is null or empty. Skipping email alert.");
        }

        // Return response
        assert savedAccount != null;
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedAccount.getAccountBalance())
                        .accountNumber(savedAccount.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .build())
                .build();
    }

        //Balance Enquiry, Name Enquiry, Debit, Credit, Transfer
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if provided account number exists in the db
        boolean doesAccountExist = accountRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }
        Account foundAccount = accountRepository.findByAccountNumber(request.getAccountNumber());
        User foundUser = foundAccount.getUser();
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName())
                        .accountNumber(foundAccount.getAccountNumber())
                        .accountBalance(foundAccount.getAccountBalance())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean doesAccountExist = accountRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG;
        }
        Account foundAccount = accountRepository.findByAccountNumber(request.getAccountNumber());
        User foundUser = foundAccount.getUser();
        return foundUser.getLastName()  + " " + foundUser.getFirstName() + " " + foundUser.getOtherName();
    }

    //Login test
    @Override
    public BankResponse appLogin(LoginRequest loginRequest) {
        //check if account exists
        boolean doesUserExist = userRepository.existsByEmail(loginRequest.getUsername());
        if (!doesUserExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.USER_NOT_FOUND_MSG)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByEmail(loginRequest.getUsername());
        Account userAccount = accountRepository.findByUser(foundUser);

        if (passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            // success
            return BankResponse.builder()
                    .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                    .responseMessage(AccountUtils.LOGIN_SUCCESS_MSG)
                    .accountInfo(AccountInfo.builder()
                            .accountName(foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName())
                            .accountNumber(userAccount.getAccountNumber())
                            .accountBalance(userAccount.getAccountBalance())
                            .build())
                    .build();
        } else {
            // invalid password
            return BankResponse.builder()
                    .responseCode(AccountUtils.LOGIN_FAILURE_CODE)
                    .responseMessage(AccountUtils.LOGIN_FAILURE_MSG)
                    .accountInfo(null)
                    .build();
        }
    }
    // Crediting an account
    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //check if account exists
        boolean doesAccountExist = accountRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }
        //credit the specified account
        Account accountToCredit = accountRepository.findByAccountNumber(request.getAccountNumber());
        User userToCredit = accountToCredit.getUser();

        accountToCredit.setAccountBalance(accountToCredit.getAccountBalance().add(request.getAmount()));
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getLastName() + " " + userToCredit.getFirstName() + " " + userToCredit.getOtherName())
                        .accountBalance(accountToCredit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }

    // Debiting an account
    @Override
    public BankResponse DebitAccount(CreditDebitRequest request) {
        //check if account exists
        boolean doesAccountExist = accountRepository.existsByAccountNumber(request.getAccountNumber());
        if (!doesAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }
        //debit the specified account
        Account accountToDebit = accountRepository.findByAccountNumber(request.getAccountNumber());
        User userToDebit = accountToDebit.getUser();
        BigDecimal amount = request.getAmount();
        BigDecimal balance = accountToDebit.getAccountBalance();

        if (amount.compareTo(balance) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MSG)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getLastName() + " " + userToDebit.getFirstName() + " " + userToDebit.getOtherName())
                            .accountBalance(accountToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .build())
                    .build();
        }
        accountToDebit.setAccountBalance(accountToDebit.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(userToDebit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToDebit.getLastName() + " " + userToDebit.getFirstName() + " " + userToDebit.getOtherName())
                        .accountBalance(accountToDebit.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .build())
                .build();
    }
    // transferring between accounts
    @Override
    public BankResponse TransferMoney(TransferRequest request) {
        // get the account to debit(check if it exists)


        boolean isSourceAccountExist = accountRepository.existsByAccountNumber(request.getSourceAccountNumber());
        boolean isDestinationAccountExist = accountRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if (!isSourceAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.SOURCE_ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.SOURCE_ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }
        if (!isDestinationAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.DESTINATION_ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage(AccountUtils.DESTINATION_ACCOUNT_DOES_NOT_EXIST_MSG)
                    .accountInfo(null)
                    .build();
        }

        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber());
        User sourceAccountUser = sourceAccount.getUser();
        BigDecimal amount = request.getAmount();
        BigDecimal balance = sourceAccount.getAccountBalance();

        //check if account has sufficient money
        if (amount.compareTo(balance) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MSG)
                    .accountInfo(null)
                    .build();

        }
        //debit the account
        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(amount));
        userRepository.save(sourceAccountUser);

        //get the account to be credited
        //Credit the account
        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber());
        User  destinationAccountUser = destinationAccount.getUser();
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(amount));
        userRepository.save(destinationAccountUser);

        EmailDetails debitAlert = EmailDetails.builder()
                .Subject("Debit Alert")
                .recipientEmail(sourceAccountUser.getEmail())
                .messageBody("Dear esteemed customer, the sum of ₦" + amount + " has been deducted from your account with account number " + sourceAccount.getAccountNumber() + ". Your current balance is ₦" + sourceAccount.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        EmailDetails creditAlert = EmailDetails.builder()
                .Subject("Credit Alert")
                .recipientEmail(destinationAccountUser.getEmail())
                .messageBody("Dear user, the sum of ₦" + amount + " has been added to your account with account number " + destinationAccount.getAccountNumber() + ". Your current balance is ₦" + destinationAccount.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(sourceAccountUser.getLastName() + " " + sourceAccountUser.getFirstName() + " " + sourceAccountUser.getOtherName())
                        .accountBalance(sourceAccount.getAccountBalance())
                        .accountNumber(sourceAccount.getAccountNumber())
                        .build())
                .build();



    }
}

