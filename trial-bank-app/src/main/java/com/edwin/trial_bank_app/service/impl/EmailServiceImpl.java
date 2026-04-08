package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javamailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipientEmail());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javamailSender.send(mailMessage);
            System.out.println("Sending mail to "+emailDetails.getRecipientEmail());
            System.out.println("Message : "+emailDetails.getMessageBody());
            System.out.println("Email sent successfully");
        }
        catch (MailException e) {
            System.out.println("Email Sending Failed" + e.getMessage());;
        }
    }

}
