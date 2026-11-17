package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.exception.UnauthorizedAccountAccessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountValidationServiceImplTests {

    private final AccountValidationServiceImpl validationService =
            new AccountValidationServiceImpl();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accountOwnerCanMoveMoneyFromOwnAccount() {
        authenticate("owner@example.com", "ROLE_USER");

        assertDoesNotThrow(() ->
                validationService.validateOwnership(accountOwnedBy("owner@example.com")));
    }

    @Test
    void administratorCannotMoveMoneyFromCustomerAccount() {
        authenticate("admin@example.com", "ROLE_ADMIN");

        assertThrows(UnauthorizedAccountAccessException.class, () ->
                validationService.validateOwnership(accountOwnedBy("customer@example.com")));
    }

    @Test
    void administratorCanViewCustomerAccount() {
        authenticate("admin@example.com", "ROLE_ADMIN");

        assertDoesNotThrow(() ->
                validationService.validateOwnershipOrAdmin(
                        accountOwnedBy("customer@example.com")));
    }

    @Test
    void anotherCustomerCannotViewCustomerAccount() {
        authenticate("other@example.com", "ROLE_USER");

        assertThrows(UnauthorizedAccountAccessException.class, () ->
                validationService.validateOwnershipOrAdmin(
                        accountOwnedBy("customer@example.com")));
    }

    private void authenticate(String email, String role) {
        var authentication = new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Account accountOwnedBy(String email) {
        User owner = User.builder()
                .email(email)
                .build();

        return Account.builder()
                .accountNumber("1234567890")
                .user(owner)
                .build();
    }
}
