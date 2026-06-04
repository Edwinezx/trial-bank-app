package com.edwin.trial_bank_app.transaction.service;

import com.edwin.trial_bank_app.dto.*;
import com.edwin.trial_bank_app.entity.Account;

import java.math.BigDecimal;

public interface TransactionService {
    MultiAccountBankResponse transferFunds(String destinationAccountNumber, BigDecimal amount);
}


