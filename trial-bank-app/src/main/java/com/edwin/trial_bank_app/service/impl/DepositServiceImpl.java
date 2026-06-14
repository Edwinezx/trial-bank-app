package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.DepositRequest;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.Transaction;
import com.edwin.trial_bank_app.enums.TransactionStatus;
import com.edwin.trial_bank_app.enums.TransactionType;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.TransactionRepository;
import com.edwin.trial_bank_app.service.AccountValidationService;
import com.edwin.trial_bank_app.service.DepositService;
import com.edwin.trial_bank_app.utils.TransactionUtils;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class DepositServiceImpl implements DepositService {

    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;
    private AccountValidationService validationService;


    @Override
    @Transactional
    public void depositMoney(DepositRequest request) {


        Account account =
                accountRepository.findByAccountNumber(request.getDestinationAccountNumber()
                ).orElseThrow(()->
                        new AccountNotFoundException("Source account not found")
                );

        BigDecimal amount = request.getAmount();

        validationService.validateDeposit(account, amount);


        account.setAvailableBalance(
                account.getAvailableBalance().add(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setTransactionReference(
                TransactionUtils.generateReference());

        transaction.setDestinationAccountNumber(
                account.getAccountNumber());

        transaction.setAmount(amount);

        transaction.setTransactionType(
                TransactionType.DEPOSIT);

        transaction.setStatus(
                TransactionStatus.SUCCESS);

        transaction.setTransactionDate(
                java.time.LocalDateTime.now());

        transactionRepository.save(transaction);

    }
}
