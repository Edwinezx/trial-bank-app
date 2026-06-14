package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.dto.request.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;

public interface AccountService {
        BankResponse registerAccount(UserRequest userRequest);
        BankResponse closeAccount(CloseAccountRequest request);
    }
