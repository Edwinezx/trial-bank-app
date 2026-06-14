package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.TransferRequest;
import com.edwin.trial_bank_app.dto.TransferResponse;
import com.edwin.trial_bank_app.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class TransactionController {

    private final TransferService transferService;

    // POST /transactions/transfer
    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        TransferResponse transferResponse = transferService.transferFunds(transferRequest);

        return   ResponseEntity.ok(transferResponse);
    }
}
