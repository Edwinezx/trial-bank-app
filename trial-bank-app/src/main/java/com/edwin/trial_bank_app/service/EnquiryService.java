package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.response.BankResponse;

public interface EnquiryService {
    BankResponse balanceEnquiryByAccountNumber(String accountNumber);
    String nameEnquiryByAccountNumber(String accountNumber);
}
