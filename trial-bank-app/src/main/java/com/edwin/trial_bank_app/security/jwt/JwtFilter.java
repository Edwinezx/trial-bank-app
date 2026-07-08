package com.edwin.trial_bank_app.security.jwt;

import com.edwin.trial_bank_app.enums.Roles;
import com.edwin.trial_bank_app.security.blacklist.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService blacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                // Reject blacklisted tokens — without this check, logout is
                // cosmetic only and the token remains usable until natural expiry
                if (blacklistService.isBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                // Reject refresh tokens used as access tokens — a refresh token
                // should only ever reach /refresh, never any business endpoint
                if (!jwtUtils.isAccessToken(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                String username = jwtUtils.extractUsername(token);
                Roles role      = jwtUtils.extractRole(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // SimpleGrantedAuthority("ROLE_ADMIN") satisfies hasRole('ADMIN')
                    // because Spring Security strips the "ROLE_" prefix when matching
                    var authority = new SimpleGrantedAuthority(role.name());
                    var auth = new UsernamePasswordAuthenticationToken(
                            username, null, List.of(authority));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

            } catch (Exception e) {
                // 401 not 403 — this is an authentication failure (bad/expired token),
                // not an authorization failure (valid token, wrong permissions)
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}