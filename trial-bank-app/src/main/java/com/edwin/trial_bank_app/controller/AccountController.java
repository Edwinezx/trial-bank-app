package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AccountController {
     private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/register")
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody UserRequest request) {
        return  ResponseEntity.ok().body(accountService.registerAccount(request));
    }

    @PostMapping("/close")
    public BankResponse closeAccount(@RequestBody CloseAccountRequest request){
         return accountService.closeAccount(request);
    }
}
