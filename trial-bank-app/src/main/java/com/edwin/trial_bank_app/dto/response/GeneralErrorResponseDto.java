package com.edwin.trial_bank_app.dto.response;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record GeneralErrorResponseDto(String apiPath, HttpStatus errorCode, String errorMessage, LocalDateTime timestamp) {
}
