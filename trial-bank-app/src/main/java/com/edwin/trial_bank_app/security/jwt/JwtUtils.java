package com.edwin.trial_bank_app.security.jwt;

import com.edwin.trial_bank_app.enums.Roles;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final long ACCESS_TOKEN_EXPIRY  = 1000L * 60 * 60 * 24;     // 24 hours
    private static final long REFRESH_TOKEN_EXPIRY = 1000L * 60 * 60 * 24 * 7; // 7 days
    private static final String CLAIM_ROLE       = "role";
    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TYPE_ACCESS      = "access";
    private static final String TYPE_REFRESH     = "refresh";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateAccessToken(String email, Roles role) {
        // Role is embedded as a claim so JwtFilter can read it and give Spring
        // Security a real GrantedAuthority — without this, @PreAuthorize always fails
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_TOKEN_TYPE, TYPE_ACCESS)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRY))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        // Refresh tokens carry no role claim — they can only reach /refresh
        // and should never be accepted by JwtFilter for any other endpoint
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TOKEN_TYPE, TYPE_REFRESH)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRY))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Roles extractRole(String token) {
        String roleName = extractAllClaims(token).get(CLAIM_ROLE, String.class);
        return roleName != null ? Roles.valueOf(roleName) : Roles.ROLE_USER;
    }

    public boolean isRefreshToken(String token) {
        return TYPE_REFRESH.equals(extractAllClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    public boolean isAccessToken(String token) {
        return TYPE_ACCESS.equals(extractAllClaims(token).get(CLAIM_TOKEN_TYPE, String.class));
    }

    public long getExpiryMillis(String token) {
        // Used when blacklisting tokens on logout so the entry can be evicted
        // once the token would have expired naturally anyway
        return extractAllClaims(token).getExpiration().getTime();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }
}