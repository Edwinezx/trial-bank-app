package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.entity.Account;
import java.math.BigDecimal;

public interface FraudDetectionService {
    void assess(Account account, BigDecimal amount);
}
