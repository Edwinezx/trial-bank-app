package com.edwin.trial_bank_app.dto;

import com.edwin.trial_bank_app.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

    private String accountName;
    @NotNull
    private BigDecimal availableBalance;
    private String accountNumber;
    private AccountType accountType;
}
