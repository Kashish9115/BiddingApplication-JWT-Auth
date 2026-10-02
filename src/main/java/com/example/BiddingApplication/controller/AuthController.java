////package com.example.BiddingApplication.controller;
////import com.example.BiddingApplication.dto.LoginRequest;
////import com.example.BiddingApplication.entity.User;
////import com.example.BiddingApplication.service.*;
////import jakarta.servlet.http.*;
////import jakarta.validation.Valid;
////import lombok.RequiredArgsConstructor;
////import org.springframework.stereotype.Controller;
////import org.springframework.ui.Model;
////import org.springframework.web.bind.annotation.*;
////
////@Controller @RequiredArgsConstructor
////public class AuthController {
//// private final AuthService auth; private final JwtService jwt;
//// @GetMapping({"/","/login"}) public String login(Model m){m.addAttribute("loginRequest",new LoginRequest());return "login";}
//// @PostMapping("/login") public String login(@Valid @ModelAttribute("loginRequest")LoginRequest r,HttpServletResponse s,Model m){
////  User u=auth.findByEmail(r.getEmail());if(u==null||!auth.verifyPassword(u,r.getPassword())){m.addAttribute("error","Invalid email or password.");return "login";}
////  add(s,"2FA_PENDING_TOKEN",auth.createPendingToken(u),300);return (u.isFirstTimeLogin()||!u.isTwoFactorEnabled())?"redirect:/2fa/setup":"redirect:/2fa/verify";
//// }
//// @GetMapping("/lost-2fa") public String lost(){return "lost-2fa";}
//// @PostMapping("/lost-2fa") public String send(@RequestParam String email,HttpServletResponse s,Model m){
////  User u=auth.findByEmail(email);if(u==null){m.addAttribute("error","Unable to process this request.");return "lost-2fa";}
////  auth.generateEmailOtp(u);add(s,"RECOVERY_TOKEN",auth.createRecoveryToken(u),600);return "redirect:/verify-email-otp";
//// }
//// @GetMapping("/verify-email-otp") public String otp(){return "verify-email-otp";}
//// @PostMapping("/verify-email-otp") public String otp(@RequestParam String otp,HttpServletRequest r,HttpServletResponse s,Model m){
////  String t=cookie(r,"RECOVERY_TOKEN");if(t==null||!jwt.isValid(t)||!jwt.isRecoveryToken(t))return "redirect:/lost-2fa";
////  User u=auth.findById(jwt.extractUserId(t));if(u==null||!auth.verifyEmailOtp(u,otp)){m.addAttribute("error","Invalid or expired OTP.");return "verify-email-otp";}
////  auth.reset2FA(u);delete(s,"RECOVERY_TOKEN");add(s,"2FA_PENDING_TOKEN",auth.createPendingToken(u),300);return "redirect:/2fa/setup";
//// }
//// @GetMapping("/logout") public String logout(HttpServletRequest r,HttpServletResponse s){
////  String t=cookie(r,"REFRESH_TOKEN");if(t!=null&&jwt.isValid(t))try{User u=auth.findById(jwt.extractUserId(t));if(u!=null)auth.revokeRefreshToken(u);}catch(Exception ignored){}
////  delete(s,"ACCESS_TOKEN");delete(s,"REFRESH_TOKEN");delete(s,"2FA_PENDING_TOKEN");delete(s,"RECOVERY_TOKEN");return "redirect:/login";
//// }
//// private void add(HttpServletResponse s,String n,String v,int a){Cookie c=new Cookie(n,v);c.setHttpOnly(true);c.setSecure(false);c.setPath("/");c.setMaxAge(a);s.addCookie(c);}
//// private void delete(HttpServletResponse s,String n){add(s,n,"",0);}
//// private String cookie(HttpServletRequest r,String n){if(r.getCookies()==null)return null;for(Cookie c:r.getCookies())if(n.equals(c.getName()))return c.getValue();return null;}
////}
//
//
//package com.example.BiddingApplication.controller;
//
//import com.example.BiddingApplication.dto.LoginRequest;
//import com.example.BiddingApplication.entity.User;
//import com.example.BiddingApplication.service.AuthService;
//import com.example.BiddingApplication.service.JwtService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService auth;
//    private final JwtService jwt;
//
//
//    // ============================================================
//    // LOGIN PAGE
//    // ============================================================
//
//    @GetMapping({"/", "/login"})
//    public String login(Model model) {
//
//        model.addAttribute(
//                "loginRequest",
//                new LoginRequest()
//        );
//
//        return "login";
//    }
//
//
//    // ============================================================
//    // LOGIN
//    // ============================================================
//
//    @PostMapping("/login")
//    public String login(
//            @Valid @ModelAttribute("loginRequest") LoginRequest request,
//            Model model) {
//
//        User user =
//                auth.findByEmail(request.getEmail());
//
//
//        // Invalid email/password
//        if (user == null ||
//                !auth.verifyPassword(
//                        user,
//                        request.getPassword())) {
//
//            model.addAttribute(
//                    "error",
//                    "Invalid email or password."
//            );
//
//            return "login";
//        }
//
//
//        /*
//         * --------------------------------------------------------
//         * PASSWORD IS CORRECT
//         * --------------------------------------------------------
//         *
//         * We DO NOT create access/refresh tokens yet.
//         *
//         * User must complete 2FA first.
//         *
//         * Create short-lived 2FA pending JWT.
//         */
//
//        String pendingToken =
//                auth.createPendingToken(user);
//
//
//        model.addAttribute(
//                "pendingToken",
//                pendingToken
//        );
//
//
//        /*
//         * First login:
//         *
//         * Login
//         *   ↓
//         * QR setup
//         *   ↓
//         * Google Authenticator OTP
//         *
//         * Existing user:
//         *
//         * Login
//         *   ↓
//         * Google Authenticator OTP
//         */
//
//        if (user.isFirstTimeLogin() ||
//                !user.isTwoFactorEnabled()) {
//
//            return "redirect:/2fa/setup?token=" + pendingToken;
//        }
//
//
//        return "redirect:/2fa/verify?token=" + pendingToken;
//    }
//
//
//    // ============================================================
//    // LOST GOOGLE AUTHENTICATOR
//    // ============================================================
//
//    @GetMapping("/lost-2fa")
//    public String lost2FA() {
//
//        return "lost-2fa";
//    }
//
//
//    // ============================================================
//    // SEND EMAIL OTP
//    // ============================================================
//
//    @PostMapping("/lost-2fa")
//    public String sendRecoveryOtp(
//            @RequestParam String email,
//            Model model) {
//
//        User user =
//                auth.findByEmail(email);
//
//
//        if (user == null) {
//
//            model.addAttribute(
//                    "error",
//                    "Unable to process this request."
//            );
//
//            return "lost-2fa";
//        }
//
//
//        /*
//         * Generate and send email OTP.
//         */
//
//        auth.generateEmailOtp(user);
//
//
//        /*
//         * Create short-lived recovery JWT.
//         *
//         * No cookie.
//         */
//
//        String recoveryToken =
//                auth.createRecoveryToken(user);
//
//
//        return "redirect:/verify-email-otp?token="
//                + recoveryToken;
//    }
//
//
//    // ============================================================
//    // EMAIL OTP PAGE
//    // ============================================================
//
//    @GetMapping("/verify-email-otp")
//    public String verifyEmailOtpPage(
//            @RequestParam(required = false) String token,
//            Model model) {
//
//        if (token == null ||
//                token.isBlank()) {
//
//            return "redirect:/lost-2fa";
//        }
//
//
//        /*
//         * Send token to Thymeleaf page so that the form
//         * can submit it again.
//         */
//
//        model.addAttribute(
//                "recoveryToken",
//                token
//        );
//
//        return "verify-email-otp";
//    }
//
//
//    // ============================================================
//    // VERIFY EMAIL OTP
//    // ============================================================
//
//    @PostMapping("/verify-email-otp")
//    public String verifyEmailOtp(
//            @RequestParam String otp,
//            @RequestParam String token,
//            Model model) {
//
//
//        /*
//         * Validate recovery token.
//         */
//
//        if (token == null ||
//                token.isBlank() ||
//                !jwt.isValid(token) ||
//                !jwt.isRecoveryToken(token)) {
//
//            return "redirect:/lost-2fa";
//        }
//
//
//        /*
//         * Get user from recovery JWT.
//         */
//
//        Integer userId =
//                jwt.extractUserId(token);
//
//        User user =
//                auth.findById(userId);
//
//
//        if (user == null) {
//
//            return "redirect:/lost-2fa";
//        }
//
//
//        /*
//         * Verify email OTP.
//         */
//
//        if (!auth.verifyEmailOtp(user, otp)) {
//
//            model.addAttribute(
//                    "error",
//                    "Invalid or expired OTP."
//            );
//
//            model.addAttribute(
//                    "recoveryToken",
//                    token
//            );
//
//            return "verify-email-otp";
//        }
//
//
//        /*
//         * Email OTP verified.
//         *
//         * Reset old Google Authenticator configuration.
//         */
//
//        auth.reset2FA(user);
//
//
//        /*
//         * Create NEW short-lived 2FA pending token.
//         *
//         * This is NOT an access token.
//         */
//
//        String pendingToken =
//                auth.createPendingToken(user);
//
//
//        return "redirect:/2fa/setup?token="
//                + pendingToken;
//    }
//
//
//    // ============================================================
//    // LOGOUT
//    // ============================================================
//
////    @GetMapping("/logout")
////    public String logout(
////            @RequestParam(required = false) String token) {
////
////
////        /*
////         * --------------------------------------------------------
////         * LOGOUT
////         * --------------------------------------------------------
////         *
////         * The client should send the current refresh token:
////         *
////         * /logout?token=R1
////         *
////         * No cookie is used.
////         *
////         * We DO NOT delete token values from DB.
////         *
////         * We only set:
////         *
////         * is_token_revoked = true
////         */
////
////        if (token != null &&
////                !token.isBlank()) {
////
////            try {
////
////                if (jwt.isValid(token) &&
////                        jwt.isRefreshToken(token)) {
////
////                    Integer userId =
////                            jwt.extractUserId(token);
////
////                    User user =
////                            auth.findById(userId);
////
////
////                    if (user != null &&
////                            auth.validateRefreshToken(
////                                    user,
////                                    token)) {
////
////                        auth.revokeRefreshToken(user);
////                    }
////                }
////
////            } catch (Exception ignored) {
////                // Do not expose token validation details.
////            }
////        }
////
////
////        return "redirect:/login";
////    }
//
//
//    @PostMapping("/logout")
//    public String logout(
//            @RequestParam(required = false) String token) {
//
//        /*
//         * ============================================================
//         * LOGOUT USING REFRESH TOKEN
//         * ============================================================
//         */
//
//        if (token != null &&
//                !token.isBlank()) {
//
//            try {
//
//                /*
//                 * Check JWT signature + expiry.
//                 */
//
//                if (jwt.isValid(token) &&
//                        jwt.isRefreshToken(token)) {
//
//                    /*
//                     * Get user ID from R1.
//                     */
//
//                    Integer userId =
//                            jwt.extractUserId(token);
//
//
//                    User user =
//                            auth.findById(userId);
//
//
//                    /*
//                     * Validate R1 against DB hash.
//                     */
//
//                    if (user != null &&
//                            auth.validateRefreshToken(
//                                    user,
//                                    token)) {
//
//                        /*
//                         * IMPORTANT:
//                         *
//                         * We DO NOT delete:
//                         *
//                         * refresh_token_hash
//                         * refresh_token_expiry
//                         * access_token_hash
//                         * access_token_expiry
//                         *
//                         * We only set:
//                         *
//                         * is_token_revoked = true
//                         */
//
//                        auth.revokeRefreshToken(user);
//                    }
//                }
//
//            } catch (Exception ignored) {
//                // Do not expose token details.
//            }
//        }
//
//
//        return "redirect:/login";
//    }
//
//}

package com.example.BiddingApplication.controller;

import com.example.BiddingApplication.dto.LoginRequest;
import com.example.BiddingApplication.entity.User;
import com.example.BiddingApplication.service.AuthService;
import com.example.BiddingApplication.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;
    private final JwtService jwt;


    @GetMapping({"/", "/login"})
    public String login(Model model) {

        model.addAttribute(
                "loginRequest",
                new LoginRequest()
        );

        return "login";
    }


    @PostMapping("/login")
    public String login(
            @Valid
            @ModelAttribute("loginRequest")
            LoginRequest request,
            Model model) {

        User user =
                auth.findByEmail(
                        request.getEmail()
                );


        if (user == null ||
                !auth.verifyPassword(
                        user,
                        request.getPassword()
                )) {

            model.addAttribute(
                    "error",
                    "Invalid email or password."
            );

            return "login";
        }


        /*
         * Temporary JWT only for 2FA process.
         */
        String pendingToken =
                auth.createPendingToken(user);


        if (user.isFirstTimeLogin() ||
                !user.isTwoFactorEnabled()) {

            return "redirect:/2fa/setup?token="
                    + pendingToken;
        }


        return "redirect:/2fa/verify?token="
                + pendingToken;
    }


 //   @PostMapping("/logout")
//    public String logout(
//            HttpServletRequest request) {
//
//        String authorization =
//                request.getHeader(
//                        "Authorization"
//                );
//
//
//        if (authorization != null &&
//                authorization.startsWith(
//                        "Bearer "
//                )) {
//
//            String token =
//                    authorization
//                            .substring(7)
//                            .trim();
//
//
//            try {
//
//                if (jwt.isValid(token)) {
//
//                    Integer userId =
//                            jwt.extractUserId(token);
//
//                    User user =
//                            auth.findById(userId);
//
//
//                    if (user != null) {
//
//                        /*
//                         * DB:
//                         *
//                         * is_token_revoked = true
//                         */
//                        auth.revokeRefreshToken(user);
//                    }
//                }
//
//            } catch (Exception ignored) {
//            }

//    if (jwt.isValid(token)
//            && (jwt.isAccessToken(token)
//            || jwt.isRefreshToken(token))) {
//
//        Integer userId = jwt.extractUserId(token);
//
//        User user = auth.findById(userId);
//
//        if (user != null) {
//            auth.revokeRefreshToken(user);
//        }
//    }
//        }
//
//
//        return "redirect:/login";
  //  }





    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        String authorization =
                request.getHeader("Authorization");

        if (authorization != null
                && authorization.startsWith("Bearer ")) {

            String token =
                    authorization.substring(7).trim();

            try {

                if (jwt.isValid(token)
                        && (jwt.isAccessToken(token)
                        || jwt.isRefreshToken(token))) {

                    Integer userId =
                            jwt.extractUserId(token);

                    User user =
                            auth.findById(userId);

                    if (user != null) {

                        auth.revokeRefreshToken(user);
                    }
                }

            } catch (Exception ignored) {
                // Do not expose token validation details
            }
        }

        return "redirect:/login";
    }


    @GetMapping("/lost-2fa")
    public String lost2fa() {
        return "lost-2fa";
    }


    @PostMapping("/lost-2fa")
    public String sendRecoveryOtp(
            @RequestParam String email,
            Model model) {

        User user =
                auth.findByEmail(email);


        if (user == null) {

            model.addAttribute(
                    "error",
                    "Unable to process this request."
            );

            return "lost-2fa";
        }


        auth.generateEmailOtp(user);


        String recoveryToken =
                auth.createRecoveryToken(user);


        return "redirect:/verify-email-otp?token="
                + recoveryToken;
    }


    @GetMapping("/verify-email-otp")
    public String verifyEmailOtpPage(
            @RequestParam(required = false)
            String token,
            Model model) {

        if (token == null ||
                token.isBlank()) {

            return "redirect:/lost-2fa";
        }


        model.addAttribute(
                "recoveryToken",
                token
        );


        return "verify-email-otp";
    }


    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(
            @RequestParam String otp,
            @RequestParam String token,
            Model model) {

        if (!jwt.isValid(token) ||
                !jwt.isRecoveryToken(token)) {

            return "redirect:/lost-2fa";
        }


        User user =
                auth.findById(
                        jwt.extractUserId(token)
                );


        if (user == null) {
            return "redirect:/lost-2fa";
        }


        if (!auth.verifyEmailOtp(user, otp)) {

            model.addAttribute(
                    "error",
                    "Invalid or expired OTP."
            );

            model.addAttribute(
                    "recoveryToken",
                    token
            );

            return "verify-email-otp";
        }


        /*
         * Old authentication is revoked.
         */
        auth.reset2FA(user);


        String pendingToken =
                auth.createPendingToken(user);


        return "redirect:/2fa/setup?token="
                + pendingToken;
    }
}