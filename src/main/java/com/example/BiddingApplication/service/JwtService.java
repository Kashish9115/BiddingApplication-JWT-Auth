package com.example.BiddingApplication.service;

import com.example.BiddingApplication.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final long pendingExpiration;
    private final long recoveryExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-expiration}") long accessExpiration,
            @Value("${jwt.refresh-expiration}") long refreshExpiration,
            @Value("${jwt.pending-expiration}") long pendingExpiration,
            @Value("${jwt.recovery-expiration}") long recoveryExpiration) {

        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                    "jwt.secret must contain at least 32 characters"
            );
        }

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.pendingExpiration = pendingExpiration;
        this.recoveryExpiration = recoveryExpiration;
    }

    public String generateAccessToken(User user) {
        return generateToken(user, "access", accessExpiration);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, "refresh", refreshExpiration);
    }

    public String generate2FAPendingToken(User user) {
        return generateToken(user, "2fa_pending", pendingExpiration);
    }

    public String generateRecoveryToken(User user) {
        return generateToken(user, "recovery", recoveryExpiration);
    }

    private String generateToken(
            User user,
            String type,
            long expiration) {

        Date now = new Date();

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("role", user.getRole())
                .claim("type", type)
                .issuedAt(now)
                .expiration(
                        new Date(now.getTime() + expiration)
                )
                .signWith(key)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Integer extractUserId(String token) {
        Object value = extractAllClaims(token).get("userId");

        if (value instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(String.valueOf(value));
    }

    public String extractRole(String token) {
        return extractAllClaims(token)
                .get("role", String.class);
    }

    public String extractType(String token) {
        return extractAllClaims(token)
                .get("type", String.class);
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = extractAllClaims(token);

            Date expiry = claims.getExpiration();

            return expiry != null &&
                    expiry.after(new Date());

        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isAccessToken(String token) {
        return "access".equals(extractTypeSafe(token));
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractTypeSafe(token));
    }

    public boolean is2FAPendingToken(String token) {
        return "2fa_pending".equals(extractTypeSafe(token));
    }

    public boolean isRecoveryToken(String token) {
        return "recovery".equals(extractTypeSafe(token));
    }

    private String extractTypeSafe(String token) {
        try {
            return extractType(token);
        } catch (Exception exception) {
            return null;
        }
    }
}
