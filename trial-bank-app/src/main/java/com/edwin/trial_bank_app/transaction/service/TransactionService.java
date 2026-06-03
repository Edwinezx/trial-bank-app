package com.edwin.trial_bank_app.transaction.service;

import com.edwin.trial_bank_app.dto.*;


public interface TransactionService {
    BankResponse creditAccount(CreditDebitRequest request);

    BankResponse debitAccount(CreditDebitRequest request);

    BankResponse transferMoney(TransferRequest request);
}
