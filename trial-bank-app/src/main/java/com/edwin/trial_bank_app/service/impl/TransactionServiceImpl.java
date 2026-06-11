package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.TransactionService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import com.edwin.trial_bank_app.utils.TransactionUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public MultiAccountBankResponse transferFunds(
            String sourceAccountNumber,
            String destinationAccountNumber,
            BigDecimal amount) {

        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return MultiAccountBankResponse.builder()
                    .responseCode("009")
                    .responseMessage("Invalid transfer amount")
                    .build();
        }

        // Prevent self-transfer
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            return MultiAccountBankResponse.builder()
                    .responseCode("010")
                    .responseMessage("Cannot transfer to same account")
                    .build();
        }

        // Get authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assert authentication != null;
        String loggedInEmail = authentication.getName();

        User sourceUser = userRepository.findByEmail(loggedInEmail);

        if (sourceUser == null) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.USER_NOT_FOUND_CODE)
                    .responseMessage("User not found")
                    .build();
        }

        // Find source account
        Account sourceAccount =
                accountRepository.findByAccountNumber(sourceAccountNumber);

        if (sourceAccount == null) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage("Source account not found")
                    .build();
        }

        // Verify ownership
        if (!sourceAccount.getUser().getId()
                .equals(sourceUser.getId())) {

            return MultiAccountBankResponse.builder()
                    .responseCode("011")
                    .responseMessage("Unauthorized account access")
                    .build();
        }

        // Verify source account active
        if (!sourceAccount.getStatus().isActive()) {

            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_INACTIVE_CODE)
                    .responseMessage("User account inactive")
                    .build();
        }

        // Find destination account
        Account destinationAccount =
                accountRepository.findByAccountNumber(destinationAccountNumber);

        if (destinationAccount == null) {
            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DOES_NOT_EXIST)
                    .responseMessage("Destination account not found")
                    .build();
        }

        // Verify destination account active
        if (!destinationAccount.getStatus().isActive()) {

            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_INACTIVE_CODE)
                    .responseMessage("Destination account inactive")
                    .build();
        }

        // Verify sufficient funds
        if (sourceAccount.getAccountBalance()
                .compareTo(amount) < 0) {

            return MultiAccountBankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_FUNDS_CODE)
                    .responseMessage("Insufficient funds")
                    .build();
        }

        // Debit source
        sourceAccount.setAccountBalance(
                sourceAccount.getAccountBalance()
                        .subtract(amount));

        // Credit destination
        destinationAccount.setAccountBalance(
                destinationAccount.getAccountBalance()
                        .add(amount));

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);

        // Save transaction record
        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                TransactionUtils.generateReference());

        transaction.setSourceAccountNumber(
                sourceAccount.getAccountNumber());

        transaction.setDestinationAccountNumber(
                destinationAccount.getAccountNumber());

        transaction.setAmount(amount);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transaction.setTransactionType(
                TransactionType.TRANSFER);

        transaction.setTransactionDate(
                LocalDateTime.now());

        transactionRepository.save(transaction);

        // Debit email
        EmailDetails debitEmail = EmailDetails.builder()
                .recipientEmail(sourceUser.getEmail())
                .subject("Debit Alert")
                .messageBody(
                        "Transfer Successful.\n\n" +
                                "Account Number: "
                                + sourceAccount.getAccountNumber()
                                + "\nAmount Debited: ₦"
                                + amount
                                + "\nNew Balance: ₦"
                                + sourceAccount.getAccountBalance()
                )
                .build();

        emailService.sendEmailAlert(debitEmail);

        // Credit email
        EmailDetails creditEmail = EmailDetails.builder()
                .recipientEmail(destinationAccount.getUser().getEmail())
                .subject("Credit Alert")
                .messageBody(
                        "Credit Successful.\n\n" +
                                "Account Number: "
                                + destinationAccount.getAccountNumber()
                                + "\nAmount Credited: ₦"
                                + amount
                                + "\nNew Balance: ₦"
                                + destinationAccount.getAccountBalance()
                )
                .build();

        emailService.sendEmailAlert(creditEmail);

        return MultiAccountBankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESS_CODE)
                .responseMessage("Transfer successful")
                .accountInfo(
                        List.of(
                                AccountUtils.mapToAccountInfo(
                                        sourceAccount
                                )
                        )
                )
                .build();
    }
}