//package com.example.BiddingApplication.service;
//import com.example.BiddingApplication.entity.User;
//public interface AuthService {
// User findByEmail(String email); User findById(Integer id); boolean verifyPassword(User user,String password);
// String createAccessToken(User user); String createRefreshToken(User user); boolean validateRefreshToken(User user,String token); void revokeRefreshToken(User user);
// String createPendingToken(User user); String createRecoveryToken(User user);
// String createNewSecret(User user); boolean verify2FA(User user,String code); void enable2FA(User user); void reset2FA(User user);
// void generateEmailOtp(User user); boolean verifyEmailOtp(User user,String otp);
//
//
//
//
//    boolean validateAccessToken(User user, String token);
//
//
//    void revokeTokens(User user);
//}
package com.example.BiddingApplication.service;

import com.example.BiddingApplication.entity.User;

public interface AuthService {

    // USER
    User findByEmail(String email);

    User findById(Integer id);


    // PASSWORD
    boolean verifyPassword(User user, String password);


    // JWT ACCESS TOKEN
    String createAccessToken(User user);

    boolean validateAccessToken(User user, String token);


    // JWT REFRESH TOKEN
    String createRefreshToken(User user);

    boolean validateRefreshToken(User user, String token);

    void revokeRefreshToken(User user);


    // NEW LOGIN / NEW SESSION
    void startNewSession(User user);


    // 2FA PENDING TOKEN
    String createPendingToken(User user);


    // RECOVERY TOKEN
    String createRecoveryToken(User user);


    // GOOGLE AUTHENTICATOR
    String createNewSecret(User user);

    boolean verify2FA(User user, String code);

    void enable2FA(User user);

    void reset2FA(User user);


    // EMAIL OTP
    void generateEmailOtp(User user);

    boolean verifyEmailOtp(User user, String otp);
}