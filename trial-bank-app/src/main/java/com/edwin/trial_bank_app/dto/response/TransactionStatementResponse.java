package com.edwin.trial_bank_app.dto.response;

import com.edwin.trial_bank_app.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatementResponse {
    private String accountNumber;
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private List<Transaction> transactions;
}
