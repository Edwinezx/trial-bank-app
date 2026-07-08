package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.NewAccountRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/open")
    @PreAuthorize("hasAnyRole('User','Admin')")
    public ResponseEntity<BankResponse> createAccount(@Valid @RequestBody NewAccountRequest request,
                                                        Authentication authentication) {
        return ResponseEntity.ok(accountService.createAccount(request, authentication.getName()));
    }

    @PatchMapping("/close/{accountNumber}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<BankResponse> closeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }

    @PatchMapping("/freeze/{accountNumber}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<BankResponse> freezeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.freezeAccount(accountNumber));
    }

    @PatchMapping("/unfreeze/{accountNumber}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<BankResponse> unfreezeAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.unfreezeAccount(accountNumber));
    }

    @PatchMapping("/dormant/{accountNumber}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<BankResponse> markDormant(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.markDormant(accountNumber));
    }

    @PatchMapping("/activate/{accountNumber}")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<BankResponse> activateAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.activateAccount(accountNumber));
    }
}
