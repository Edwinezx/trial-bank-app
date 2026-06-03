package com.edwin.trial_bank_app.transaction.service.impl;

import com.edwin.trial_bank_app.dto.*;
import com.edwin.trial_bank_app.email.service.EmailService;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.AccountRepository;

import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.transaction.service.TransactionService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public TransactionServiceImpl(AccountRepository accountRepository,
                                  EmailService emailService, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    //  find account or return null response
    private BankResponse accountNotFoundResponse(String code, String message) {
        return BankResponse.builder()
                .responseCode(code)
                .responseMessage(message)
                .accountInfo(null)
                .build();
    }

    // build AccountInfo
    private AccountInfo buildAccountInfo(Account account) {
        User user = account.getUser();
        return AccountInfo.builder()
                .accountName(user.getLastName() + " " + user.getFirstName() + " " + user.getOtherName())
                .accountBalance(account.getAccountBalance())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .build();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber());
        if (account == null) {
            return accountNotFoundResponse(AccountUtils.ACCOUNT_DOES_NOT_EXIST, AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG);
        }

        account.setAccountBalance(account.getAccountBalance().add(request.getAmount()));
        accountRepository.save(account);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_CREDIT_SUCCESS_MSG)
                .accountInfo(buildAccountInfo(account))
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber());
        if (account == null) {
            return accountNotFoundResponse(AccountUtils.ACCOUNT_DOES_NOT_EXIST, AccountUtils.ACCOUNT_DOES_NOT_EXIST_MSG);
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(account.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MSG)
                    .accountInfo(buildAccountInfo(account))
                    .build();
        }

        account.setAccountBalance(account.getAccountBalance().subtract(amount));
        accountRepository.save(account);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_DEBIT_SUCCESS_CODE)
                .responseMessage(AccountUtils.ACCOUNT_DEBIT_SUCCESS_MSG)
                .accountInfo(buildAccountInfo(account))
                .build();
    }

    @Override
    public BankResponse transferMoney(TransferRequest request) {
        Account sourceAccount = accountRepository.findByAccountNumber(request.getSourceAccountNumber());

        if (sourceAccount == null) {
            return accountNotFoundResponse(AccountUtils.SOURCE_ACCOUNT_DOES_NOT_EXIST,
                    "Source account does not belong to logged-in user");
        }

        Account destinationAccount = accountRepository.findByAccountNumber(request.getDestinationAccountNumber());
        if (destinationAccount == null) {
            return accountNotFoundResponse(AccountUtils.DESTINATION_ACCOUNT_DOES_NOT_EXIST, AccountUtils.DESTINATION_ACCOUNT_DOES_NOT_EXIST_MSG);
        }

        BigDecimal amount = request.getAmount();
        if (amount.compareTo(sourceAccount.getAccountBalance()) > 0) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_FUNDS_MSG)
                    .accountInfo(buildAccountInfo(sourceAccount))
                    .build();
        }

        sourceAccount.setAccountBalance(sourceAccount.getAccountBalance().subtract(amount));
        destinationAccount.setAccountBalance(destinationAccount.getAccountBalance().add(amount));
        accountRepository.saveAll(List.of(sourceAccount, destinationAccount));

        // Alerts
        emailService.sendEmailAlert(EmailDetails.builder()
                .recipientEmail(sourceAccount.getUser().getEmail())
                .messageBody("₦" + amount + " has been deducted from account " + sourceAccount.getAccountNumber() +
                        ". Current balance: ₦" + sourceAccount.getAccountBalance())
                .subject("Debit Alert")
                .build());

        emailService.sendEmailAlert(EmailDetails.builder()
                .subject("Credit Alert")
                .recipientEmail(destinationAccount.getUser().getEmail())
                .messageBody("₦" + amount + " has been credited to account " + destinationAccount.getAccountNumber() +
                        ". Current balance: ₦" + destinationAccount.getAccountBalance())
                .build());

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESS_MSG)
                .accountInfo(buildAccountInfo(sourceAccount))
                .build();
    }
}

