package com.example.BiddingApplication.service.impl;
import com.example.BiddingApplication.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor @Slf4j
public class EmailServiceImpl implements EmailService {
 private final JavaMailSender sender;
 public void sendOtp(String email,String otp){SimpleMailMessage m=new SimpleMailMessage();m.setTo(email);m.setSubject("Bidding Application - 2FA Recovery OTP");m.setText("Your 2FA recovery OTP is: "+otp+"\n\nThis OTP expires in 10 minutes.");sender.send(m);log.info("Recovery OTP sent to {}",mask(email));}
 private String mask(String e){int a=e.indexOf('@');return a>1?e.charAt(0)+"***"+e.substring(a):"***";}
}
