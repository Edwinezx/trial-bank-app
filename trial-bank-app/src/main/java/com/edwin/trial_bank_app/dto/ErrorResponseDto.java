package com.edwin.trial_bank_app.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponseDto(String apiPath, HttpStatus errorCode, String message, LocalDateTime timestamp) {
}
