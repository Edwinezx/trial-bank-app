package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.CloseAccountRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(accountService.registerAccount(request));
    }

    @PostMapping("/close")
    public ResponseEntity<BankResponse> closeAccount(@RequestBody CloseAccountRequest request) {
        return ResponseEntity.ok(accountService.closeAccount(request));
    }

    @PatchMapping("/freeze/{accountNumber}")
    public ResponseEntity<BankResponse> freezeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.freezeAccount(accountNumber));
    }

    @PatchMapping("/unfreeze/{accountNumber}")
    public ResponseEntity<BankResponse> unfreezeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.unfreezeAccount(accountNumber));
    }

    @PatchMapping("/dormant/{accountNumber}")
    public ResponseEntity<BankResponse> markDormant(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.markDormant(accountNumber));
    }
}
