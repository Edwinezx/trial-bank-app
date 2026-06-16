package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.exception.AccountNotFoundException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.service.EnquiryService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import org.springframework.stereotype.Service;

@Service
public class EnquiryServiceImpl implements EnquiryService {

    private final AccountRepository accountRepository;

    public EnquiryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public BankResponse balanceEnquiryByAccountNumber(String accountNumber) {
        Account foundAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found"));

        User foundUser = foundAccount.getUser();
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_MSG)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName())
                        .accountNumber(foundAccount.getAccountNumber())
                        .availableBalance(foundAccount.getAvailableBalance())
                        .accountType(foundAccount.getAccountType().getTypeName())
                        .build())
                .build();
    }

    @Override
    public String nameEnquiryByAccountNumber(String accountNumber) {
        Account foundAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not Found"));

        User foundUser = foundAccount.getUser();
        return foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName();
    }
}
