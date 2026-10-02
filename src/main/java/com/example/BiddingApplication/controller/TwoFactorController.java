
package com.example.BiddingApplication.controller;

import com.example.BiddingApplication.entity.User;
import com.example.BiddingApplication.service.AuthService;
import com.example.BiddingApplication.service.JwtService;
import com.example.BiddingApplication.service.QrCodeService;
import com.example.BiddingApplication.service.TwoFactorService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/2fa")
@RequiredArgsConstructor
public class TwoFactorController {

    private final AuthService auth;
    private final JwtService jwt;
    private final TwoFactorService two;
    private final QrCodeService qr;


    // ============================================================
    // FIRST-TIME 2FA SETUP PAGE
    // ============================================================

    @GetMapping("/setup")
    public String setup(
            @RequestParam(required = false) String token,
            Model model) throws Exception {

        /*
         * Validate pending JWT.
         */
        User user = pendingUser(token);

        if (user == null) {
            return "redirect:/login";
        }


        /*
         * Generate Google Authenticator secret
         * only if user does not already have one.
         */
        if (user.getSecretKey() == null ||
                user.getSecretKey().isBlank()) {

            auth.createNewSecret(user);
        }


        /*
         * Generate OTP Authenticator URI.
         */
        String otpAuthUri =
                two.generateOtpAuthUri(
                        user.getEmail(),
                        user.getSecretKey()
                );


        /*
         * Generate QR code.
         */
        String qrCode =
                qr.generateQrBase64(otpAuthUri);


        model.addAttribute(
                "qrCode",
                qrCode
        );


        /*
         * Send pending JWT to Thymeleaf.
         *
         * setup-2fa.html should submit
         * this token as a hidden field.
         */
        model.addAttribute(
                "pendingToken",
                token
        );


        return "setup-2fa";
    }


    // ============================================================
    // VERIFY FIRST-TIME 2FA SETUP
    // ============================================================

    @PostMapping("/setup")
    public String setupVerify(
            @RequestParam String code,
            @RequestParam String token,
            HttpServletResponse response,
            Model model) {

        /*
         * Validate pending JWT.
         */
        User user = pendingUser(token);

        if (user == null) {
            return "redirect:/login";
        }


        /*
         * Verify Google Authenticator OTP.
         */
        if (!auth.verify2FA(user, code)) {

            /*
             * Regenerate QR code so the page
             * can be rendered again.
             */
            try {

                String otpAuthUri =
                        two.generateOtpAuthUri(
                                user.getEmail(),
                                user.getSecretKey()
                        );

                String qrCode =
                        qr.generateQrBase64(otpAuthUri);

                model.addAttribute(
                        "qrCode",
                        qrCode
                );

            } catch (Exception ignored) {
                // QR regeneration is optional.
            }


            model.addAttribute(
                    "pendingToken",
                    token
            );

            model.addAttribute(
                    "error",
                    "Invalid authenticator code."
            );

            return "setup-2fa";
        }


        // ========================================================
        // 2FA SUCCESS
        // ========================================================

        /*
         * Enable Google Authenticator.
         */
        auth.enable2FA(user);


        /*
         * Start NEW login.
         *
         * This sets:
         *
         * is_token_revoked = false
         */
        auth.startNewSession(user);


        /*
         * Generate Access JWT.
         */
        String accessToken =
                auth.createAccessToken(user);


        /*
         * Generate Refresh JWT.
         */
        String refreshToken =
                auth.createRefreshToken(user);


        // ========================================================
        // SEND JWTs
        // ========================================================

        /*
         * Send actual JWTs in response headers.
         */
        response.setHeader(
                "X-Access-Token",
                accessToken
        );

        response.setHeader(
                "X-Refresh-Token",
                refreshToken
        );


        /*
         * Prevent caching of authentication response.
         */
        response.setHeader(
                "Cache-Control",
                "no-store"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );


        // ========================================================
        // SEND DATA TO DASHBOARD
        // ========================================================

        /*
         * Dashboard needs the Access JWT so that
         * its logout JavaScript can send:
         *
         * Authorization: Bearer <JWT>
         */
        model.addAttribute(
                "accessToken",
                accessToken
        );


        model.addAttribute(
                "refreshToken",
                refreshToken
        );


        model.addAttribute(
                "user",
                user
        );


        /*
         * Render dashboard directly.
         *
         * IMPORTANT:
         * Do NOT redirect to /dashboard here.
         */
        return "dashboard";
    }


    // ============================================================
    // NORMAL 2FA VERIFICATION PAGE
    // ============================================================

    @GetMapping("/verify")
    public String verify(
            @RequestParam(required = false) String token,
            Model model) {

        /*
         * Validate pending JWT.
         */
        User user = pendingUser(token);

        if (user == null) {
            return "redirect:/login";
        }


        /*
         * Send pending token to Thymeleaf.
         */
        model.addAttribute(
                "pendingToken",
                token
        );


        return "verify-2fa";
    }


    // ============================================================
    // NORMAL 2FA VERIFICATION
    // ============================================================

    @PostMapping("/verify")
    public String verifyPost(
            @RequestParam String code,
            @RequestParam String token,
            HttpServletResponse response,
            Model model) {

        /*
         * Validate pending JWT.
         */
        User user = pendingUser(token);

        if (user == null) {
            return "redirect:/login";
        }


        /*
         * Verify Google Authenticator OTP.
         */
        if (!auth.verify2FA(user, code)) {

            model.addAttribute(
                    "error",
                    "Invalid authenticator code."
            );

            model.addAttribute(
                    "pendingToken",
                    token
            );

            return "verify-2fa";
        }


        // ========================================================
        // 2FA SUCCESS
        // ========================================================

        /*
         * This is a NEW login.
         */
        auth.startNewSession(user);


        /*
         * Generate Access JWT.
         */
        String accessToken =
                auth.createAccessToken(user);


        /*
         * Generate Refresh JWT.
         */
        String refreshToken =
                auth.createRefreshToken(user);


        // ========================================================
        // SEND JWTs
        // ========================================================

        response.setHeader(
                "X-Access-Token",
                accessToken
        );

        response.setHeader(
                "X-Refresh-Token",
                refreshToken
        );


        response.setHeader(
                "Cache-Control",
                "no-store"
        );

        response.setHeader(
                "Pragma",
                "no-cache"
        );


        // ========================================================
        // SEND DATA TO DASHBOARD
        // ========================================================

        model.addAttribute(
                "accessToken",
                accessToken
        );


        model.addAttribute(
                "refreshToken",
                refreshToken
        );


        model.addAttribute(
                "user",
                user
        );


        /*
         * IMPORTANT:
         *
         * Do NOT:
         *
         * return "redirect:/dashboard";
         *
         * because redirect creates a NEW GET request
         * and loses the model attributes.
         *
         * Render dashboard directly.
         */
        return "dashboard";
    }


    // ============================================================
    // GET USER FROM PENDING JWT
    // ============================================================

    private User pendingUser(String token) {

        /*
         * Token missing.
         */
        if (token == null ||
                token.isBlank()) {

            return null;
        }


        try {

            /*
             * Validate JWT signature + expiration.
             */
            if (!jwt.isValid(token)) {
                return null;
            }


            /*
             * Token must specifically be
             * a 2FA pending token.
             */
            if (!jwt.is2FAPendingToken(token)) {
                return null;
            }


            /*
             * Extract user ID.
             */
            Integer userId =
                    jwt.extractUserId(token);


            if (userId == null) {
                return null;
            }


            /*
             * Find user.
             */
            return auth.findById(userId);

        } catch (Exception e) {

            return null;
        }
    }
}