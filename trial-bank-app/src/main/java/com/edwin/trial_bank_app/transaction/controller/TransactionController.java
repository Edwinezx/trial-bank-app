package com.edwin.trial_bank_app.transaction.controller;

import com.edwin.trial_bank_app.dto.MultiAccountBankResponse;
import com.edwin.trial_bank_app.dto.TransferRequest;
import com.edwin.trial_bank_app.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // POST /transactions/transfer
    @PostMapping("/transfer")
    public MultiAccountBankResponse transferFunds(@RequestBody TransferRequest transferRequest) {
        return transactionService.transferFunds(
                transferRequest.getDestinationAccountNumber(),
                transferRequest.getAmount());
    }
}
