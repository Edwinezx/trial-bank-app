package com.edwin.trial_bank_app.controller;

import com.edwin.trial_bank_app.dto.request.LoginRequest;
import com.edwin.trial_bank_app.dto.request.UserRequest;
import com.edwin.trial_bank_app.dto.response.AuthResponse;
import com.edwin.trial_bank_app.dto.response.BankResponse;
import com.edwin.trial_bank_app.entity.User;
import com.edwin.trial_bank_app.repository.UserRepository;
import com.edwin.trial_bank_app.security.blacklist.TokenBlacklistService;
import com.edwin.trial_bank_app.security.jwt.JwtUtils;
import com.edwin.trial_bank_app.service.AuthenticationService;
import com.edwin.trial_bank_app.service.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserServices          userServices;
    private final JwtUtils              jwtUtils;
    private final TokenBlacklistService blacklistService;
    private final UserRepository        userRepository;

    @PostMapping("/register")
    public ResponseEntity<BankResponse> register(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userServices.onboardNewUser(userRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.appLogin(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token required");
        }

        String refreshToken = authHeader.substring(7);

        try {
            if (!jwtUtils.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not a refresh token");
            }
            if (blacklistService.isBlacklisted(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token has been invalidated");
            }

            String email = jwtUtils.extractUsername(refreshToken);

            // DB lookup for role because refresh tokens carry no role claim —
            // this ensures an admin who was demoted gets the right role on next refresh
            User user = userRepository.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            String newAccessToken = jwtUtils.generateAccessToken(email, user.getRole());
            return ResponseEntity.ok(Map.of("token", newAccessToken));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader,
            @RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            // Blacklist until natural expiry — after that JWT validation rejects it
            // anyway so the blacklist entry is automatically evicted
            blacklistService.blacklist(accessToken, jwtUtils.getExpiryMillis(accessToken));
        }
        if (refreshToken != null && !refreshToken.isBlank()) {
            // Must blacklist refresh token too — without this, the client could
            // immediately refresh to get a new access token after logging out
            blacklistService.blacklist(refreshToken, jwtUtils.getExpiryMillis(refreshToken));
        }

        return ResponseEntity.ok(Map.of("responseMessage", "Logged out successfully"));
    }
}