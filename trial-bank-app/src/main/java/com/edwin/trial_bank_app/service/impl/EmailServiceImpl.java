package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    private final String senderEmail;

    public EmailServiceImpl(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username}") String senderEmail
    ) {
        this.javaMailSender = javaMailSender;
        this.senderEmail = senderEmail;
    }

    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipientEmail());
            mailMessage.setSubject(emailDetails.getSubject());
            mailMessage.setText(emailDetails.getMessageBody());

            javaMailSender.send(mailMessage);

            log.info(
                    "Email sent successfully to {}",
                    emailDetails.getRecipientEmail()
            );

        } catch (MailException exception) {
            log.error(
                    "Failed to send email to {}: {}",
                    emailDetails.getRecipientEmail(),
                    exception.getMessage(),
                    exception
            );
        }
    }
}