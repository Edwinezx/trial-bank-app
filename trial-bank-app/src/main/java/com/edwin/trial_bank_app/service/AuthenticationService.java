package com.edwin.trial_bank_app.service;


import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.dto.request.LoginRequest;

public interface AuthenticationService {
    AuthResponse appLogin(LoginRequest loginRequest);

}
