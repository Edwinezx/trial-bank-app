package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.service.AuthenticationService;
import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.dto.request.LoginRequest;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.security.jwt.JwtUtils;
import com.edwin.trial_bank_app.utils.AccountUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AuthenticationServiceImpl(PasswordEncoder passwordEncoder,
                                     UserRepository userRepository,
                                     AccountRepository accountRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public AuthResponse appLogin(LoginRequest loginRequest) {
        boolean doesUserExist = userRepository.existsByEmail(loginRequest.getUsername());
        if (!doesUserExist) {
            return AuthResponse.builder()
                    .responseCode(AccountUtils.USER_NOT_FOUND_CODE)
                    .responseMessage(AccountUtils.USER_NOT_FOUND_MSG)
                    .accountInfo(null)
                    .build();
        }

        User foundUser = userRepository.findByEmail(loginRequest.getUsername());
        List<Account> userAccounts = accountRepository.findByUser(foundUser);

        if (passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            List<AccountInfo> accountInfos = userAccounts.stream()
                    .map(acc -> AccountInfo.builder()
                            .accountName(foundUser.getLastName() + " " + foundUser.getFirstName() + " " + foundUser.getOtherName())
                            .accountNumber(acc.getAccountNumber())
                            .availableBalance(acc.getAvailableBalance())
                            .accountType(acc.getAccountType())
                            .build())
                    .toList();


            String token = JwtUtils.generateToken(foundUser.getEmail());

            return AuthResponse.builder()
                    .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                    .responseMessage(AccountUtils.LOGIN_SUCCESS_MSG)
                    .token(token)
                    .accountInfo(accountInfos)
                    .build();

        } else {
            return AuthResponse.builder()
                    .responseCode(AccountUtils.LOGIN_FAILURE_CODE)
                    .responseMessage(AccountUtils.LOGIN_FAILURE_MSG)
                    .accountInfo(null)
                    .build();
        }
    }
}
