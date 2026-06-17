package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;

public interface UserServices {
    BankResponse onboardNewUser(UserRequest userRequest);
}
