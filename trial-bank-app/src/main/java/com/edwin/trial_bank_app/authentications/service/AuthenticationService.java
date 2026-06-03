package com.edwin.trial_bank_app.authentications.service;


import com.edwin.trial_bank_app.dto.AuthResponse;
import com.edwin.trial_bank_app.dto.LoginRequest;
import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;

public interface AuthenticationService {
    AuthResponse appLogin(LoginRequest loginRequest);

}
