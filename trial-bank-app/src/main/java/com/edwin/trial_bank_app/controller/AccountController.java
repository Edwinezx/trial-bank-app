package com.edwin.trial_bank_app.controller;

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
        return ResponseEntity.ok(accountService.onboardNewUser(request));
    }

    @PatchMapping("/close/{accountNumber}")
    public ResponseEntity<BankResponse> closeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
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

    @PatchMapping("/activate/{accountNumber}")
    public ResponseEntity<BankResponse> activateAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.activateAccount(accountNumber));
    }
}
