package com.edwin.trial_bank_app.config;

import com.edwin.trial_bank_app.entity.AccountType;
import com.edwin.trial_bank_app.repository.AccountTypeRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final AccountTypeRepository accountTypeRepository;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        seedIfAbsent("SAVINGS", "Standard savings account",
                new BigDecimal("500"), new BigDecimal("500000"), new BigDecimal("200000"));
        seedIfAbsent("CURRENT", "Current account for businesses",
                new BigDecimal("10000"), new BigDecimal("5000000"), new BigDecimal("2000000"));
        seedIfAbsent("FIXED DEPOSIT", "Fixed deposit account",
                new BigDecimal("50000"), BigDecimal.ZERO, BigDecimal.ZERO);
    }

    private void seedIfAbsent(String name, String description,
                              BigDecimal minBalance, BigDecimal dailyLimit,
                              BigDecimal withdrawalLimit) {
        if (accountTypeRepository.findByTypeNameIgnoreCase(name).isEmpty()) {
            AccountType type = new AccountType();
            type.setTypeName(name);
            type.setDescription(description);
            type.setMinimumBalance(minBalance);
            type.setDailyTransferLimit(dailyLimit);
            type.setWithdrawalLimit(withdrawalLimit);
            type.setActive(true);
            accountTypeRepository.save(type);
        }
    }
}