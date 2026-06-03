package com.edwin.trial_bank_app.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiAccountBankResponse {
    private String responseCode;
    private String responseMessage;
    private List<AccountInfo> accountInfo; // multiple accounts
}


