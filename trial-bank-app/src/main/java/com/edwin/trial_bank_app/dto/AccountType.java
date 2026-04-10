package com.edwin.trial_bank_app.dto;

public enum AccountType {
    SAVINGS,
    CURRENT,
    FIXED;

    public boolean isSavingsAccount() {
        return this == SAVINGS;
    }

    public boolean isCurrentAccount() {
        return this == CURRENT;
    }

    public boolean isFixedAccount() {
        return this == FIXED;
    }
}
