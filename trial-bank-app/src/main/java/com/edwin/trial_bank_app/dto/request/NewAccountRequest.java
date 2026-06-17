package com.edwin.trial_bank_app.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
public class NewAccountRequest {

    private String accountNumber;
    private BigDecimal availableBalance;

    @NotBlank(message = "Account Type Cannot Be Left Blank")
    private String accountType;
}
