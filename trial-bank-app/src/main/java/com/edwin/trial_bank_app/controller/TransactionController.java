package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.dto.TransferRequest;
import com.edwin.trial_bank_app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /transactions/transfer
    @PostMapping("/transfer")
    public MultiAccountBankResponse transferFunds(@Valid @RequestBody TransferRequest transferRequest) {
        return transactionService.transferFunds(
                transferRequest.getSourceAccountNumber(),
                transferRequest.getDestinationAccountNumber(),
                transferRequest.getAmount());
    }
}
