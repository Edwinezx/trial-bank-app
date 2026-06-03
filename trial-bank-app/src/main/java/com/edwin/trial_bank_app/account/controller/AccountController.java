package com.edwin.trial_bank_app.account.controller;

import com.edwin.trial_bank_app.account.service.AccountService;
import com.edwin.trial_bank_app.dto.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class AccountController {
     private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }


    @PostMapping("/register")
    public BankResponse createAccount(@RequestBody CreateAccountRequest request) {
        return  accountService.registerAccount(request.getUserRequest());
    }

    @PostMapping("/close")
    public BankResponse closeAccount(@RequestBody CloseAccountRequest request){
         return accountService.closeAccount(request);
    }
}
