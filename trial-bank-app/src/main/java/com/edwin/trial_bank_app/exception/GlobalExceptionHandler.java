
package com.edwin.trial_bank_app.exception;

import com.edwin.trial_bank_app.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse>
    handleAccountNotFound(
            AccountNotFoundException ex){

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .code("404")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse>
    handleInsufficientFunds(
            InsufficientFundsException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .code("400")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(InactiveAccountException.class)
    public ResponseEntity<ErrorResponse>
    handleInactiveAccount(
            InactiveAccountException ex){

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .code("403")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<ErrorResponse>
    handleUnauthorizedAccountAccess(
            UnauthorizedAccountAccessException ex){

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .code("401")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse>
    handleInvalidAmount(
            InvalidAmountException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .code("400")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(SelfTransactionException.class)
    public ResponseEntity<ErrorResponse>
    handleSelfTransaction(
            SelfTransactionException ex){

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .code("400")
                                .message(ex.getMessage())
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse>
    handleGeneralException(
            Exception ex){

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        ErrorResponse.builder()
                                .code("500")
                                .message(
                                        ex.getMessage()
                                )
                                .timestamp(
                                        LocalDateTime.now()
                                )
                                .build()
                );
    }
}
