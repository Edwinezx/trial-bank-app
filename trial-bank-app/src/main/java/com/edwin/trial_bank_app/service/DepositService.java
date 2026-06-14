package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.DepositRequest;
import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;

import java.math.BigDecimal;

public interface DepositService {
    void depositMoney(DepositRequest request);
}
