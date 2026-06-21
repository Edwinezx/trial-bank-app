package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.EmailDetails;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.EmailService;
import com.edwin.trial_bank_app.service.UserServices;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final EmailService emailService;

    @Override
    public BankResponse onboardNewUser(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage("A user with this email already exists")
                    .build();
        }

        User savedUser = userRepository.save(User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(Roles.ROLE_USER)
                .build());

        auditService.log("USER_ONBOARDED", savedUser.getEmail(), "SUCCESS",
                "New user registered");

        sendEmail(savedUser.getEmail(), "WELCOME",
                "Hi " + savedUser.getFirstName() + ", your registration was successful. " +
                        "You can now open an account.");

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage("User registered successfully")
                .build();
    }

    private void sendEmail(String email, String subject, String body) {
        if (email != null && !email.isBlank()) {
            emailService.sendEmailAlert(EmailDetails.builder()
                    .recipientEmail(email)
                    .subject(subject)
                    .messageBody(body)
                    .build());
        }
    }
}
