package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;

import java.math.BigDecimal;

public interface WithdrawService {
    MultiAccountBankResponse withdraw(
            String accountNumber,
            BigDecimal amount);
}
