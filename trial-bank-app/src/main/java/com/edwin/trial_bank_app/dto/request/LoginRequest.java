package com.edwin.trial_bank_app.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "User Login requires Email input")
    @Email(message = "Requires proper Email format")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
