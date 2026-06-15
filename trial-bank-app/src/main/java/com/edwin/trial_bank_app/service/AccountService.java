package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.request.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;

public interface AccountService {
    BankResponse registerAccount(UserRequest userRequest);
    BankResponse closeAccount(CloseAccountRequest request);
    BankResponse freezeAccount(String accountNumber);
    BankResponse unfreezeAccount(String accountNumber);
    BankResponse markDormant(String accountNumber);
}
