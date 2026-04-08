package com.edwin.trial_bank_app.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private UserRequest userRequest;
    private AccountRequest accountRequest;
}
