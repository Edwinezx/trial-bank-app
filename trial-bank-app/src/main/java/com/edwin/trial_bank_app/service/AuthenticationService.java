package com.edwin.trial_bank_app.service;


import com.edwin.trial_bank_app.dto.AuthResponse;
import com.edwin.trial_bank_app.dto.LoginRequest;

public interface AuthenticationService {
    AuthResponse appLogin(LoginRequest loginRequest);

}
