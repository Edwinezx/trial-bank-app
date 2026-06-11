package com.edwin.trial_bank_app.utils;

public class TransactionUtils {

    public static String generateReference() {

        return "TRX" +
                System.currentTimeMillis();
    }
}