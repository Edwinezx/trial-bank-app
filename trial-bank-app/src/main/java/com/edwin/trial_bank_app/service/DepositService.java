package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.DepositRequest;

public interface DepositService {
    void depositMoney(DepositRequest request);
}
