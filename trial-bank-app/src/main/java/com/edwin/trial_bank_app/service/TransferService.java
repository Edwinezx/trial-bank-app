package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.request.TransferRequest;
import com.edwin.trial_bank_app.dto.response.TransferResponse;


public interface TransferService {
    TransferResponse transferFunds(TransferRequest request);

}


