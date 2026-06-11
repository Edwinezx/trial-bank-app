package com.edwin.trial_bank_app.service;

import com.edwin.trial_bank_app.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
}
