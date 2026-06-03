package com.edwin.trial_bank_app.transaction.controller;

import com.edwin.trial_bank_app.dto.BankResponse;
import com.edwin.trial_bank_app.dto.CreditDebitRequest;
import com.edwin.trial_bank_app.dto.TransferRequest;
import com.edwin.trial_bank_app.transaction.service.TransactionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class TransactionController {
    private TransactionService transactionService;

    private TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    @PostMapping("/credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return transactionService.creditAccount(request);
    }

    @PostMapping("/debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return transactionService.debitAccount(request);
    }

    @PostMapping("/transfer")
    public BankResponse transferAccount(@RequestBody TransferRequest request){
        return transactionService.transferMoney(request);
    }

}
