package com.edwin.trial_bank_app.dto;

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
    private List<AccountInfo> accountInfo;
}
