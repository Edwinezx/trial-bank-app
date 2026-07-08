package com.edwin.trial_bank_app.dto.response;

import com.edwin.trial_bank_app.dto.AccountInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String responseCode;
    private String responseMessage;
    private String token;
    private String refreshToken;
    private List<AccountInfo> accountInfo;
}