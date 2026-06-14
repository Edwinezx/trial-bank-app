package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.dto.request.EnquiryRequest;

public interface EnquiryService {
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
}
