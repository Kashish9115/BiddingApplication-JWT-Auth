package com.example.BiddingApplication.service.impl;

import com.example.BiddingApplication.entity.User;
import com.example.BiddingApplication.repo.UserRepository;
import com.example.BiddingApplication.service.AuthService;
import com.example.BiddingApplication.service.EmailService;
import com.example.BiddingApplication.service.JwtService;
import com.example.BiddingApplication.service.TwoFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final TwoFactorService twoFactor;
    private final EmailService email;

    private final SecureRandom random = new SecureRandom();

    @Override
    public User findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return repo
                .findByEmailIgnoreCaseAndDeletedFalse(email.trim())
                .orElse(null);
    }

    @Override
    public User findById(Integer id) {
        if (id == null) {
            return null;
        }

        return repo.findById(id)
                .filter(user -> !user.isDeleted())
                .orElse(null);
    }

    @Override
    public boolean verifyPassword(
            User user,
            String password) {

        return user != null &&
                password != null &&
                user.getPasswordHash() != null &&
                encoder.matches(
                        password,
                        user.getPasswordHash()
                );
    }

    @Override
    public void startNewSession(User user) {
        user.setTokenRevoked(false);
        repo.save(user);
    }

    @Override
    public String createAccessToken(User user) {

        String token =
                jwt.generateAccessToken(user);

        user.setAccessTokenHash(token);
        user.setAccessTokenExpiry(
                LocalDateTime.now().plusMinutes(15)
        );

        repo.save(user);

        return token;
    }

    @Override
    public String createRefreshToken(User user) {

        String token =
                jwt.generateRefreshToken(user);

        user.setRefreshTokenHash(token);
        user.setRefreshTokenExpiry(
                LocalDateTime.now().plusDays(30)
        );

        repo.save(user);

        return token;
    }

    @Override
    public boolean validateAccessToken(
            User user,
            String token) {

        if (user == null ||
                token == null ||
                token.isBlank() ||
                user.isTokenRevoked()) {

            return false;
        }

        if (!token.equals(user.getAccessTokenHash())) {
            return false;
        }

        if (user.getAccessTokenExpiry() == null ||
                user.getAccessTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            return false;
        }

        return jwt.isValid(token) &&
                jwt.isAccessToken(token);
    }

    @Override
    public boolean validateRefreshToken(
            User user,
            String token) {

        if (user == null ||
                token == null ||
                token.isBlank() ||
                user.isTokenRevoked()) {

            return false;
        }

        if (!token.equals(user.getRefreshTokenHash())) {
            return false;
        }

        if (user.getRefreshTokenExpiry() == null ||
                user.getRefreshTokenExpiry()
                        .isBefore(LocalDateTime.now())) {

            return false;
        }

        return jwt.isValid(token) &&
                jwt.isRefreshToken(token);
    }

    @Override
    public void revokeRefreshToken(User user) {
        if (user == null) {
            return;
        }

        /*
         * Keep JWTs in DB as requested.
         * Only mark the pair revoked.
         */
        user.setTokenRevoked(true);
        repo.save(user);
    }

    @Override
    public String createPendingToken(User user) {
        return jwt.generate2FAPendingToken(user);
    }

    @Override
    public String createRecoveryToken(User user) {
        return jwt.generateRecoveryToken(user);
    }

    @Override
    public String createNewSecret(User user) {

        String secret =
                twoFactor.generateSecret();

        user.setSecretKey(secret);
        user.setTwoFactorEnabled(false);

        repo.save(user);

        return secret;
    }

    @Override
    public boolean verify2FA(
            User user,
            String code) {

        if (user == null ||
                code == null ||
                !code.trim().matches("\\d{6}") ||
                user.getSecretKey() == null ||
                user.getSecretKey().isBlank()) {

            return false;
        }

        return twoFactor.verifyCode(
                user.getSecretKey(),
                code.trim()
        );
    }

    @Override
    public void enable2FA(User user) {
        user.setTwoFactorEnabled(true);
        user.setFirstTimeLogin(false);
        repo.save(user);
    }

    @Override
    public void reset2FA(User user) {

        user.setSecretKey(null);
        user.setTwoFactorEnabled(false);
        user.setFirstTimeLogin(true);
        user.setTokenRevoked(true);

        repo.save(user);
    }

    @Override
    public void generateEmailOtp(User user) {

        String otp =
                String.format(
                        "%06d",
                        random.nextInt(1_000_000)
                );

        user.setEmailOtpHash(
                encoder.encode(otp)
        );

        user.setEmailOtpExpiry(
                LocalDateTime.now().plusMinutes(10)
        );

        repo.save(user);

        email.sendOtp(
                user.getEmail(),
                otp
        );
    }

    @Override
    public boolean verifyEmailOtp(
            User user,
            String otp) {

        if (user == null ||
                otp == null ||
                !otp.matches("\\d{6}") ||
                user.getEmailOtpHash() == null ||
                user.getEmailOtpExpiry() == null ||
                user.getEmailOtpExpiry()
                        .isBefore(LocalDateTime.now())) {

            return false;
        }

        boolean valid =
                encoder.matches(
                        otp,
                        user.getEmailOtpHash()
                );

        if (valid) {

            user.setEmailOtpHash(null);
            user.setEmailOtpExpiry(null);

            repo.save(user);
        }

        return valid;
    }
}
