package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.entity.Account;

import java.math.BigDecimal;

public interface AccountValidationService {

    void validateTransfer(
            Account source,
            Account destination,
            BigDecimal amount);

    void validateDeposit(
            Account account,
            BigDecimal amount);

    void validateWithdrawal(
            Account account,
            BigDecimal amount);
}
