package com.edwin.trial_bank_app.authentications.controller;

import com.edwin.trial_bank_app.authentications.service.AuthenticationService;
import com.edwin.trial_bank_app.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AuthenticationController {
    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public MultiAccountBankResponse appLogin(@RequestBody LoginRequest loginRequest) {
        return authenticationService.appLogin(loginRequest);
    }
}
