package com.edwin.trial_bank_app.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

public class JwtUtils {
   // @Value("${jwt.secret}")
    private static String secretKey = "b7f1c9d2a8e4f6c1d9a3f8e7b2c6d4a9f1e8c3b5d7a0f9e2c6b1d4a8f3c7e9d2";
    private static final long EXPIRATION_TIME = 86400000; // 1 day

    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
