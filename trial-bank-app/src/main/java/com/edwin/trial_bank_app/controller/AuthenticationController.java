package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.service.AuthenticationService;
import com.edwin.trial_bank_app.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public AuthResponse appLogin(@RequestBody LoginRequest loginRequest) {
        return authenticationService.appLogin(loginRequest);
    }
}
