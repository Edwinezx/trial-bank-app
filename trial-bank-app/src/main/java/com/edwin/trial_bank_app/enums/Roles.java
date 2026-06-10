package com.edwin.trial_bank_app.enums;

public enum Roles {
    ROLE_USER,
    ROLE_ADMIN;

    public boolean hasUserRole() {
        return this == ROLE_USER;
    }

    public boolean hasAdminRole() {
        return this == ROLE_ADMIN;
    }

    }
