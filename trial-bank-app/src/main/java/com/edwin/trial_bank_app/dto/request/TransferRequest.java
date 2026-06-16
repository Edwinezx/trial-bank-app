package com.edwin.trial_bank_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferRequest {

    @NotBlank(message = "Source account is required")
    private String sourceAccountNumber;

    @NotBlank(message = "Destination account is required")
    private String destinationAccountNumber;

    @NotBlank(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank
    private String narration;
}
