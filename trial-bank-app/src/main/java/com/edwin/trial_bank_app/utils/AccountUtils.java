package com.edwin.trial_bank_app.utils;


import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;

import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountUtils {

    public static final String ACCOUNT_PHONE_EXISTS_CODE = "001";

    public static final String ACCOUNT_PHONE_EXISTS_MSG = "Account With This Phone Number Already Exists";

    public static final String ACCOUNT_CREATION_SUCCESS = "002";

    public static final String ACCOUNT_CREATION_MSG = "Account Created Successfully";

    public static final String ACCOUNT_DOES_NOT_EXIST = "003";

    public static final String ACCOUNT_DOES_NOT_EXIST_MSG = "Account Does Not Exist";

    public static final String ACCOUNT_FOUND_CODE = "004";

    public static final String ACCOUNT_FOUND_MSG = "Account Found";

    public static final String ACCOUNT_CREDIT_SUCCESS_CODE = "005";

    public static final String ACCOUNT_CREDIT_SUCCESS_MSG = "User Account Credited Successfully";

    public static final String INSUFFICIENT_FUNDS_CODE = "006";

    public static final String INSUFFICIENT_FUNDS_MSG = "Insufficient Funds";

    public static final String ACCOUNT_DEBIT_SUCCESS_CODE = "007";

    public static final String ACCOUNT_DEBIT_SUCCESS_MSG = "Account Debit Successfully";

    public static final String SOURCE_ACCOUNT_DOES_NOT_EXIST = "008";

    public static final String SOURCE_ACCOUNT_DOES_NOT_EXIST_MSG = "Source Account Does Not Exist";

    public static final String DESTINATION_ACCOUNT_DOES_NOT_EXIST = "009";

    public static final String DESTINATION_ACCOUNT_DOES_NOT_EXIST_MSG = "Destination Account Does Not Exist";

    public static final String TRANSFER_SUCCESS_CODE = "010";

    public static final String TRANSFER_SUCCESS_MSG = "Transfer Successful";

    public static final String ACCOUNT_EXISTS_CODE = "011";

    public static final String ACCOUNT_EXISTS_MSG = "Account Type For This User Already Exists";

    public static final String USER_NOT_FOUND_CODE = "012";

    public static final String USER_NOT_FOUND_MSG = "User Not Found";

    public static final String LOGIN_SUCCESS_CODE = "013";

    public static final String LOGIN_SUCCESS_MSG = "Login Successful";

    public static final String LOGIN_FAILURE_CODE = "014";

    public static final String LOGIN_FAILURE_MSG = "Login Failed, Incorrect Password";

    public static final String ACCOUNT_CLOSURE_SUCCESS_CODE = "015";

    public static final String ACCOUNT_CLOSURE_SUCCESS_MSG = "Account Closed Successfully";

    public static final String ACCOUNT_INACTIVE_CODE = "016";


    private static String generateAccountNumber(String prefix, int min, int max) {
        int randomNumber = ThreadLocalRandom.current().nextInt(min, max + 1);
        return prefix + randomNumber;
    }

    public static String generateSavingsAccountNumber() {
        String year = String.valueOf(Year.now().getValue());
        return generateAccountNumber(year, 100000, 999999);
    }

    public static String generateCurrentAccountNumber() {
        return generateAccountNumber("1032", 100000, 999999);
    }

    public static String generateFixedAccountNumber() {
        return generateAccountNumber("2281", 100000, 999999);
    }

    public static AccountInfo mapToAccountInfo(Account account) {
        User sourceUser = account.getUser();
        User destinationUser = account.getUser();

        String sourceAccountName = String.format("%s %s %s",
                sourceUser.getLastName() != null ? sourceUser.getLastName() : "",
                sourceUser.getOtherName() != null ? sourceUser.getOtherName() : "",
                sourceUser.getFirstName() != null ? sourceUser.getFirstName() : ""
        ).trim();

        String destinationAccountName = String.format("%s %s %s",
                sourceUser.getLastName() != null ? destinationUser.getLastName() : "",
                sourceUser.getOtherName() != null ? destinationUser.getOtherName() : "",
                sourceUser.getFirstName() != null ? destinationUser.getFirstName() : ""
        ).trim();

        return AccountInfo.builder()
                .accountNumber(account.getAccountNumber())
                .accountName(sourceAccountName)
                .accountBalance(account.getAccountBalance())
                .accountType(account.getAccountType())
                .build();

    }
}

