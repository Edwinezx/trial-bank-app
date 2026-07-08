package com.edwin.trial_bank_app.service.impl;

import com.edwin.trial_bank_app.dto.AccountInfo;
import com.edwin.trial_bank_app.dto.request.LoginRequest;
import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.exception.InvalidUsernameOrPasswordException;
import com.edwin.trial_bank_app.repository.AccountRepository;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.security.jwt.JwtUtils;
import com.edwin.trial_bank_app.service.AuthenticationService;
import com.edwin.trial_bank_app.utils.AccountUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncoder   passwordEncoder;
    private final UserRepository    userRepository;
    private final AccountRepository accountRepository;
    private final JwtUtils          jwtUtils;

    @Override
    public AuthResponse appLogin(LoginRequest loginRequest) {
        User foundUser = userRepository.findByEmail(loginRequest.getUsername());

        // Single query replaces existsByEmail + findByEmail — null means not found,
        // same information with one fewer round trip to the database
        if (foundUser == null || !passwordEncoder.matches(
                loginRequest.getPassword(), foundUser.getPassword())) {
            // Throwing here lets GlobalExceptionHandler return 401 with a consistent
            // error body — returning AuthResponse with a failure code would be 200 OK,
            // which is the wrong HTTP status for failed authentication
            throw new InvalidUsernameOrPasswordException("Invalid username or password");
        }

        // Account fetch only runs after credentials are verified — a wrong password
        // previously still loaded all accounts unnecessarily
        List<Account> userAccounts = accountRepository.findByUser(foundUser);

        List<AccountInfo> accountInfos = userAccounts.stream()
                .map(acc -> AccountInfo.builder()
                        .accountName(foundUser.getLastName() + " "
                                + foundUser.getFirstName() + " "
                                + foundUser.getOtherName())
                        .accountNumber(acc.getAccountNumber())
                        .availableBalance(acc.getAvailableBalance())
                        .accountType(acc.getAccountType().getTypeName())
                        .build())
                .toList();

        String accessToken  = jwtUtils.generateAccessToken(foundUser.getEmail(), foundUser.getRole());
        String refreshToken = jwtUtils.generateRefreshToken(foundUser.getEmail());

        return AuthResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESS_CODE)
                .responseMessage(AccountUtils.LOGIN_SUCCESS_MSG)
                .token(accessToken)
                .refreshToken(refreshToken)
                .accountInfo(accountInfos)
                .build();
    }
}