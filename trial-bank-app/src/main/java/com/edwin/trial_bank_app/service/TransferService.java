package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.*;


public interface TransferService {
    TransferResponse transferFunds(TransferRequest request);

}


