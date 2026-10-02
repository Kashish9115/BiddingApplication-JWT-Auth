package com.example.BiddingApplication.service;
public interface TwoFactorService {
 String generateSecret(); boolean verifyCode(String secret,String code); String generateOtpAuthUri(String email,String secret);
}
