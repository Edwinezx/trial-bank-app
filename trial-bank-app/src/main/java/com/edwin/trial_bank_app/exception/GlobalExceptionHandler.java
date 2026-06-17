package com.edwin.trial_bank_app.exception;

import com.edwin.trial_bank_app.dto.response.ErrorResponse;
import com.edwin.trial_bank_app.dto.response.GeneralErrorResponseDto;
import com.edwin.trial_bank_app.dto.response.ValidationErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(error("404", ex.getMessage()));
    }

    @ExceptionHandler(AccountTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountTypeNotFound(AccountTypeNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("019", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("400", ex.getMessage()));
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse> handleInactiveAccount(InactiveAccountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("403", ex.getMessage()));
    }

    @ExceptionHandler(FrozenAccountException.class)
    public ResponseEntity<ErrorResponse> handleFrozenAccount(FrozenAccountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("403", ex.getMessage()));
    }

    @ExceptionHandler(DormantAccountException.class)
    public ResponseEntity<ErrorResponse> handleDormantAccount(DormantAccountException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("403", ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedAccountAccessException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(error("401", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("400", ex.getMessage()));
    }

    @ExceptionHandler(SelfTransactionException.class)
    public ResponseEntity<ErrorResponse> handleSelfTransaction(SelfTransactionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(error("400", ex.getMessage()));
    }

    @ExceptionHandler(DuplicateTransactionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateTransactionException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error("409", ex.getMessage()));
    }

    @ExceptionHandler(FraudSuspectedException.class)
    public ResponseEntity<ErrorResponse> handleFraud(FraudSuspectedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(error("403", ex.getMessage()));
    }

    // Phase 4: validation errors return per-field messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ValidationErrorResponse.builder()
                        .code("400")
                        .message("Validation failed")
                        .fieldErrors(fieldErrors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLock(Exception ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(error("409", "Transaction conflict detected. Please retry."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GeneralErrorResponseDto> handleGeneral(Exception exception, WebRequest webRequest) {
        GeneralErrorResponseDto errorResponseDto = new GeneralErrorResponseDto(
                webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse error(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
