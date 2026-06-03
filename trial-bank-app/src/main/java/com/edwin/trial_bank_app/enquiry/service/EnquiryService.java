package com.edwin.trial_bank_app.enquiry.service;

import com.edwin.trial_bank_app.dto.BankResponse;
import com.edwin.trial_bank_app.dto.EnquiryRequest;

public interface EnquiryService {
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
}
