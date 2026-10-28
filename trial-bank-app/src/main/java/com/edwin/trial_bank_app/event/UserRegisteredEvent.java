package com.edwin.trial_bank_app.event;

public record UserRegisteredEvent(
        String email,
        String firstName
) {
}