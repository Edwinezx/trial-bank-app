package com.edwin.trial_bank_app.security.ratelimit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper;

    // Only sensitive endpoints are rate-limited — login, register, and account
    // opening are the abuse targets. Applying this to every endpoint would
    // penalize legitimate users browsing their history or checking balances.
    private static final Set<String> RATE_LIMITED_PATHS = Set.of(
            "/api/user/login",
            "/api/user/register",
            "/api/account/open"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (RATE_LIMITED_PATHS.contains(path)) {
            String key = getClientIp(request);
            if (!rateLimiterService.tryConsume(key)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                objectMapper.writeValue(response.getWriter(), Map.of(
                        "responseCode", "429",
                        "responseMessage", "Too many requests. Please wait before trying again."
                ));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        // X-Forwarded-For is set by proxies and load balancers to carry the
        // original client IP. Without this, all proxied requests look like
        // they come from the proxy's IP, defeating per-IP rate limiting.
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}