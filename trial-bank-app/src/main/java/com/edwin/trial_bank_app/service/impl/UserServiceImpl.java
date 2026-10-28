package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.event.UserRegisteredEvent;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.service.AuditService;
import com.edwin.trial_bank_app.service.UserServices;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public BankResponse onboardNewUser(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXISTS_CODE)
                    .responseMessage("A user with this email already exists")
                    .build();
        }

        User newUser = User.builder()
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
                .build();

        User savedUser = userRepository.save(newUser);

        auditService.log(
                "USER_ONBOARDED",
                savedUser.getEmail(),
                "SUCCESS",
                "New user registered"
        );

        eventPublisher.publishEvent(
                new UserRegisteredEvent(
                        savedUser.getEmail(),
                        savedUser.getFirstName()
                )
        );

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage("User registered successfully")
                .build();
    }
}