package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.LoginRequest;
import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> appLogin(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(authenticationService.appLogin(loginRequest));
    }
}
