package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.request.WithdrawRequest;


public interface WithdrawService {
    void withdrawMoney(WithdrawRequest request);
}
