package com.edwin.trial_bank_app.enquiry.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.BankResponse;
import com.edwin.trial_bank_app.dto.EnquiryRequest;
import com.edwin.trial_bank_app.enquiry.service.EnquiryService;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.utils.AccountUtils;
import org.springframework.stereotype.Service;

@Service
public class EnquiryServiceImpl implements EnquiryService {
    private final AccountRepository accountRepository;


    public EnquiryServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

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
                        .accountType(foundAccount.getAccountType())
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
}
