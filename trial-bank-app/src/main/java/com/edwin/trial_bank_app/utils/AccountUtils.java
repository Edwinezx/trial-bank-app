package com.edwin.trial_bank_app.utils;


import java.time.Year;
import java.util.concurrent.ThreadLocalRandom;

public class AccountUtils {


    public static final String ACCOUNT_CREATION_SUCCESS = "002";

    public static final String ACCOUNT_CREATION_MSG = "Account Created Successfully";

    public static final String ACCOUNT_DOES_NOT_EXIST = "003";

    public static final String ACCOUNT_DOES_NOT_EXIST_MSG = "Account Does Not Exist";

    public static final String ACCOUNT_FOUND_CODE = "004";

    public static final String ACCOUNT_FOUND_MSG = "Account Found";

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



    private static String generateAccountNumber(String prefix) {
        int randomNumber = ThreadLocalRandom.current().nextInt(100000, 999999 + 1);
        return prefix + randomNumber;
    }

    public static String generateSavingsAccountNumber() {
        String year = String.valueOf(Year.now().getValue());
        return generateAccountNumber(year);
    }

    public static String generateCurrentAccountNumber() {
        return generateAccountNumber("1032");
    }

    public static String generateFixedAccountNumber() {
        return generateAccountNumber("2281");
    }

}

