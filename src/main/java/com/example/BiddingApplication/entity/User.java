////package com.example.BiddingApplication.entity;
////import jakarta.persistence.*;
////import lombok.*;
////import java.time.LocalDateTime;
////
////@Entity @Table(name="Users")
////@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
////public class User {
//// @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="UserId")
//// private Integer userId;
//// @Column(name="FullName",nullable=false,columnDefinition="LONGTEXT") private String fullName;
//// @Column(name="Email",nullable=false,unique=true,length=255) private String email;
//// @Column(name="PasswordHash",nullable=false,columnDefinition="LONGTEXT") private String passwordHash;
//// @Column(name="Role",nullable=false,length=50) private String role;
//// @Column(name="IsFirstTimeLogin",nullable=false) private boolean firstTimeLogin=true;
//// @Column(name="SecretKey",columnDefinition="LONGTEXT") private String secretKey;
//// @Column(name="IsTwoFactorEnabled",nullable=false) private boolean twoFactorEnabled=false;
//// @Column(name="RefreshTokenHash") private String refreshTokenHash;
//// @Column(name="RefreshTokenExpiry") private LocalDateTime refreshTokenExpiry;
//// @Column(name="EmailOtpHash") private String emailOtpHash;
//// @Column(name="EmailOtpExpiry") private LocalDateTime emailOtpExpiry;
//// @Column(name="IsDeleted",nullable=false) private boolean deleted=false;
//// @Column(name="DeletedAt") private LocalDateTime deletedAt;
////
////
////
////
////
////    @Column(name = "access_token_hash")
////    private String accessTokenHash;
////
////    @Column(name = "access_token_expiry")
////    private LocalDateTime accessTokenExpiry;
////
////
////
////    @Column(name = "is_token_revoked", nullable = false)
////    private boolean tokenRevoked;
////
////
////}
//package com.example.BiddingApplication.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "users")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private Integer userId;
//
//    @Column(name = "full_name", nullable = false, columnDefinition = "LONGTEXT")
//    private String fullName;
//
//    @Column(name = "email", nullable = false, unique = true, length = 255)
//    private String email;
//
//    @Column(name = "password_hash", nullable = false, columnDefinition = "LONGTEXT")
//    private String passwordHash;
//
//    @Column(name = "role", nullable = false, length = 50)
//    private String role;
//
//    @Column(name = "is_first_time_login", nullable = false)
//    private boolean firstTimeLogin = true;
//
//    @Column(name = "secret_key", columnDefinition = "LONGTEXT")
//    private String secretKey;
//
//    @Column(name = "is_two_factor_enabled", nullable = false)
//    private boolean twoFactorEnabled = false;
//
//    /*
//     * ACTUAL JWT IS STORED HERE.
//     * Despite the column name "hash", this contains plaintext JWT.
//     */
//    @Column(name = "access_token_hash", length = 2000)
//    private String accessTokenHash;
//
//    @Column(name = "access_token_expiry")
//    private LocalDateTime accessTokenExpiry;
//
//    /*
//     * ACTUAL JWT IS STORED HERE.
//     * Despite the column name "hash", this contains plaintext JWT.
//     */
//    @Column(name = "refresh_token_hash", length = 2000)
//    private String refreshTokenHash;
//
//    @Column(name = "refresh_token_expiry")
//    private LocalDateTime refreshTokenExpiry;
//
//    /*
//     * Email OTP remains hashed.
//     */
//    @Column(name = "email_otp_hash")
//    private String emailOtpHash;
//
//    @Column(name = "email_otp_expiry")
//    private LocalDateTime emailOtpExpiry;
//
//    @Column(name = "is_deleted", nullable = false)
//    private boolean deleted = false;
//
//    @Column(name = "deleted_at")
//    private LocalDateTime deletedAt;
//
//    /*
//     * Global token/session revocation switch.
//     *
//     * false = current token pair can work
//     * true  = all current tokens are rejected
//     */
//    @Column(name = "is_token_revoked", nullable = false)
//    private boolean tokenRevoked = false;
//}

package com.example.BiddingApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "full_name", nullable = false, columnDefinition = "LONGTEXT")
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, columnDefinition = "LONGTEXT")
    private String passwordHash;

    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Column(name = "is_first_time_login", nullable = false)
    private boolean firstTimeLogin = true;

    @Column(name = "secret_key", columnDefinition = "LONGTEXT")
    private String secretKey;

    @Column(name = "is_two_factor_enabled", nullable = false)
    private boolean twoFactorEnabled = false;

    /*
     * IMPORTANT:
     * These columns contain the ACTUAL JWT.
     * They are NOT hashes.
     *
     * Column names remain *_hash because they already exist
     * in the database.
     */

    @Column(name = "access_token_hash", length = 2000)
    private String accessTokenHash;

    @Column(name = "access_token_expiry")
    private LocalDateTime accessTokenExpiry;

    @Column(name = "refresh_token_hash", length = 2000)
    private String refreshTokenHash;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    /*
     * Email OTP is different.
     * Email OTP remains BCrypt hashed.
     */
    @Column(name = "email_otp_hash")
    private String emailOtpHash;

    @Column(name = "email_otp_expiry")
    private LocalDateTime emailOtpExpiry;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /*
     * Global token revocation.
     *
     * false = current token pair can authenticate
     * true  = current token pair is revoked
     */
    @Column(name = "is_token_revoked", nullable = false)
    private boolean tokenRevoked = false;
}