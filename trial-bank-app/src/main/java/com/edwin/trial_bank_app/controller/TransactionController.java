package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.DepositRequest;
import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.request.WithdrawRequest;
import com.edwin.trial_bank_app.dto.response.TransactionStatementResponse;
import com.edwin.trial_bank_app.dto.response.TransferResponse;
import com.edwin.trial_bank_app.service.DepositService;
import com.edwin.trial_bank_app.service.TransactionRecordService;
import com.edwin.trial_bank_app.service.TransferService;
import com.edwin.trial_bank_app.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TransactionController {

    private final TransferService transferService;
    private final DepositService depositService;
    private final WithdrawService withdrawService;
    private final TransactionRecordService transactionRecordService;

    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.transferFunds(request));
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> deposit(@Valid @RequestBody DepositRequest request) {
        depositService.depositMoney(request);
        return ResponseEntity.ok("Deposit successful");
    }

    @PostMapping("/withdraw")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<String> withdraw(@Valid @RequestBody WithdrawRequest request) {
        withdrawService.withdrawMoney(request);
        return ResponseEntity.ok("Withdrawal successful");
    }

    @GetMapping("/history/{accountNumber}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionStatementResponse> getHistory(
            @PathVariable String accountNumber,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionRecordService.getTransactionHistory(accountNumber, page, size));
    }
}