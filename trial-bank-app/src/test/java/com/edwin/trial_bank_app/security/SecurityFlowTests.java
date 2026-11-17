package com.edwin.trial_bank_app.security;

import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.security.jwt.JwtUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFlowTests {

    private static final String AUTHORIZATION = "Authorization";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void publicRegistrationRouteIsNotBlockedBySecurity() throws Exception {
        mockMvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedRouteWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/nameEnquiry")
                        .param("accountNumber", "0000000000"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void invalidAccessTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/user/nameEnquiry")
                        .header(AUTHORIZATION, "Bearer invalid-token")
                        .param("accountNumber", "0000000000"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired access token"));
    }

    @Test
    void validUserTokenPassesAuthenticationAndRoleChecks() throws Exception {
        String accessToken = jwtUtils.generateAccessToken(
                "security-test@example.com",
                Roles.ROLE_USER
        );

        mockMvc.perform(get("/api/user/nameEnquiry")
                        .header(AUTHORIZATION, "Bearer " + accessToken)
                        .param("accountNumber", "0000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void userTokenCannotCallAdminEndpoint() throws Exception {
        String accessToken = jwtUtils.generateAccessToken(
                "security-test@example.com",
                Roles.ROLE_USER
        );

        mockMvc.perform(patch("/api/account/freeze/0000000000")
                        .header(AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void refreshTokenReachesRefreshController() throws Exception {
        String refreshToken = jwtUtils.generateRefreshToken(
                "missing-security-test-user@example.com"
        );

        mockMvc.perform(post("/api/user/refresh")
                        .header(AUTHORIZATION, "Bearer " + refreshToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User not found"));
    }
}
