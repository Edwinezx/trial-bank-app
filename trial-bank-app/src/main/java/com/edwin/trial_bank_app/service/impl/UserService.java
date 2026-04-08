package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.*;

public interface UserService {
    BankResponse createAccount(UserRequest userRequest, AccountRequest accountRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAccount(CreditDebitRequest request);
    BankResponse DebitAccount(CreditDebitRequest request);
    BankResponse TransferMoney(TransferRequest request);
}
