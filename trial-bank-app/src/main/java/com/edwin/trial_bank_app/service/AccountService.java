package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.request.NewAccountRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;

public interface AccountService {
    BankResponse createAccount(NewAccountRequest newAccountRequest, String userEmail);
    BankResponse closeAccount(String accountNumber);
    BankResponse freezeAccount(String accountNumber);
    BankResponse unfreezeAccount(String accountNumber);
    BankResponse markDormant(String accountNumber);
    BankResponse activateAccount(String accountNumber);
}
