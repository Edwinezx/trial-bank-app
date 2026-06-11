package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.entity.Account;

import java.math.BigDecimal;

public interface TransactionNotificationService {

    void sendDebitAlert(
            Account account,
            BigDecimal amount,
            String reference);

    void sendCreditAlert(
            Account account,
            BigDecimal amount,
            String reference);
}