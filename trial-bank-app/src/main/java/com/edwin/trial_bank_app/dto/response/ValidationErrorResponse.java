package com.edwin.trial_bank_app.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ValidationErrorResponse {
    private String code;
    private String message;
    private Map<String, String> fieldErrors;
    private LocalDateTime timestamp;
}
