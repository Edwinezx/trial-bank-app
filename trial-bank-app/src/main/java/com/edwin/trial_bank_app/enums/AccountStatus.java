package com.edwin.trial_bank_app.enums;

public enum AccountStatus {
    ACTIVE,
    CLOSED,
    DORMANT,
    FROZEN;

    public boolean isActive() {
        return this == ACTIVE;
    }

    public boolean isClosed() {
        return this == CLOSED;
    }

    public boolean isDormant() {
        return this == DORMANT;
    }
    public boolean isFrozen() {
        return this == FROZEN;
    }
}
