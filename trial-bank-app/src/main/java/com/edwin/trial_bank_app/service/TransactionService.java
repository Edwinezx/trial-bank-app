package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.*;


import java.math.BigDecimal;

public interface TransactionService {
    MultiAccountBankResponse transferFunds(String sourceAccountNumber, String destinationAccountNumber, BigDecimal amount);
}


