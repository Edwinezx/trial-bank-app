package com.edwin.trial_bank_app.dto;

import com.edwin.trial_bank_app.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest{

    @NotBlank(message = "First Name Cannot Be Left Blank")
    private String firstName;
    @NotBlank(message = "Last Name Cannot Be Left Blank")
    private String lastName;
    private String otherName;
    @NotBlank
    private String gender;
    @NotBlank
    private String address;
    @NotBlank
    private String stateOfOrigin;

    @NotBlank(message = "Email Cannot Be Left Blank")
    @Email
    private String email;

    @NotBlank
    private String phoneNumber;
    private String alternativePhoneNumber;

    @NotBlank
    private String password;

    @NotBlank
    private String role;

    @NotBlank
    private String accountNumber;
    private BigDecimal accountBalance;

    @NotBlank
    private AccountType accountType;


}
