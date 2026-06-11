package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.BankResponse;
import com.edwin.trial_bank_app.dto.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.UserRequest;

public interface AccountService {
        BankResponse registerAccount(UserRequest userRequest);
        BankResponse closeAccount(CloseAccountRequest request);
    }
