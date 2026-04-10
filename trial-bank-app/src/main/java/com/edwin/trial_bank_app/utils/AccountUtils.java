package com.edwin.trial_bank_app.utils;

import com.edwin.trial_bank_app.dto.UserRequest;

import java.time.Year;

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

    public static String generateSavingsAccountNumber() {

        // 2026 + randomSixDigits
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;

        //generate a random number between min and max
        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) +min);

        //convert the current year and random number into strings and concatenate them

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);

        StringBuilder accountNumber = new StringBuilder();

        return accountNumber.append(year).append(randomNumber).toString();
    }

    public static String generateCurrentAccountNumber() {
        int min = 100000;
        int max = 999999;
        int starterNumber = 1032;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) +min);

        String starter = String.valueOf(starterNumber);
        String randomNumberString = String.valueOf(randomNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(starter).append(randomNumberString).toString();

    }
    public static String generateFixedAccountNumber() {
        int min = 100000;
        int max = 999999;
        int starterNumber = 2281;
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1) +min);

        String starter = String.valueOf(starterNumber);
        String randomNumberString = String.valueOf(randomNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(starter).append(randomNumberString).toString();
    }
}

