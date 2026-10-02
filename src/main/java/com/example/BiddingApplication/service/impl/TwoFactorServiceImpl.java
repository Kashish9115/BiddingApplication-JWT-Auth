//package com.example.BiddingApplication.service.impl;
//
//import com.example.BiddingApplication.service.TwoFactorService;
//import com.warrenstrange.googleauth.GoogleAuthenticator;
//import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
//import com.warrenstrange.googleauth.GoogleAuthenticatorConfigBuilder;
//import com.warrenstrange.googleauth.KeyRepresentation;
//import org.springframework.stereotype.Service;
//
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//
//@Service
//public class TwoFactorServiceImpl implements TwoFactorService {
//
//    private static final int CODE_DIGITS = 6;
//    private static final int TIME_STEP_SECONDS = 30;
//
//    /*
//     * Allow a small clock drift window.
//     * This prevents valid codes from failing because the
//     * phone and server clocks differ slightly.
//     */
//    private final GoogleAuthenticator authenticator;
//
//    public TwoFactorServiceImpl() {
//        GoogleAuthenticatorConfig config =
//                new GoogleAuthenticatorConfigBuilder()
//                        .setTimeStepSizeInMillis(TIME_STEP_SECONDS * 1000L)
//                        .setWindowSize(3)
//                        .setCodeDigits(CODE_DIGITS)
//                        .setKeyRepresentation(KeyRepresentation.BASE32)
//                        .build();
//
//        this.authenticator = new GoogleAuthenticator(config);
//    }
//
//    @Override
//    public String generateSecret() {
//        return authenticator.createCredentials().getKey();
//    }
//
//    @Override
//    public boolean verifyCode(String secret, String code) {
//
//        if (secret == null || secret.isBlank()) {
//            return false;
//        }
//
//        if (code == null) {
//            return false;
//        }
//
//        String normalizedCode = code.trim();
//
//        if (!normalizedCode.matches("\\d{" + CODE_DIGITS + "}")) {
//            return false;
//        }
//
//        try {
//            return authenticator.authorize(
//                    secret,
//                    Integer.parseInt(normalizedCode)
//            );
//        } catch (RuntimeException exception) {
//            return false;
//        }
//    }
//
//    @Override
//    public String generateOtpAuthUri(
//            String email,
//            String secret) {
//
//        String safeEmail =
//                email == null ? "" : email.trim();
//
//        String encodedLabel =
//                URLEncoder.encode(
//                        "BiddingApplication:" + safeEmail,
//                        StandardCharsets.UTF_8
//                ).replace("+", "%20");
//
//        String encodedIssuer =
//                URLEncoder.encode(
//                        "BiddingApplication",
//                        StandardCharsets.UTF_8
//                ).replace("+", "%20");
//
//        return "otpauth://totp/"
//                + encodedLabel
//                + "?secret="
//                + secret
//                + "&issuer="
//                + encodedIssuer
//                + "&algorithm=SHA1"
//                + "&digits="
//                + CODE_DIGITS
//                + "&period="
//                + TIME_STEP_SECONDS;
//    }
//}
package com.example.BiddingApplication.service.impl;

import com.example.BiddingApplication.service.TwoFactorService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorServiceImpl implements TwoFactorService {

    private final GoogleAuthenticator authenticator =
            new GoogleAuthenticator();

    @Override
    public String generateSecret() {

        return authenticator
                .createCredentials()
                .getKey();
    }

    @Override
    public boolean verifyCode(
            String secret,
            String code) {

        if (secret == null ||
                secret.isBlank() ||
                code == null ||
                !code.matches("\\d{6}")) {

            return false;
        }

        try {

            int otp = Integer.parseInt(code);

            return authenticator.authorize(
                    secret,
                    otp
            );

        } catch (NumberFormatException e) {

            return false;

        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public String generateOtpAuthUri(
            String email,
            String secret) {

        return "otpauth://totp/"
                + "BiddingApplication:"
                + email
                + "?secret="
                + secret
                + "&issuer=BiddingApplication"
                + "&algorithm=SHA1"
                + "&digits=6"
                + "&period=30";
    }
}