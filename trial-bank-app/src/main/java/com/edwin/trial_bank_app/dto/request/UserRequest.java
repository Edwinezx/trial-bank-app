package com.edwin.trial_bank_app.dto.request;

import com.edwin.trial_bank_app.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[0-9]{11}$",
            message ="Invalid Phone number format")
    private String phoneNumber;

    @Pattern(regexp = "^[0-9]{11}$",
            message ="Invalid Phone number format")
    private String alternativePhoneNumber;

    @NotBlank
    private String password;
    private String role;
    private String accountNumber;
    private BigDecimal availableBalance;

    private AccountType accountType;
}
