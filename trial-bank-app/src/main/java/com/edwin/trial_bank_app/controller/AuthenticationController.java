package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.LoginRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.service.AuthenticationService;
import com.edwin.trial_bank_app.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserServices userServices;

    public AuthenticationController(AuthenticationService authenticationService, UserServices userServices) {
        this.authenticationService = authenticationService;
        this.userServices = userServices;
    }

    @PostMapping("/register")
    public ResponseEntity<BankResponse> register(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userServices.onboardNewUser(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> appLogin(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authenticationService.appLogin(loginRequest));
    }
}
