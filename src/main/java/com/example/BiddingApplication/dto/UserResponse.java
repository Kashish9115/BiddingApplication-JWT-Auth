package com.example.BiddingApplication.dto;
import lombok.Data;
@Data public class UserResponse {
 private Integer userId; private String fullName; private String email; private String role; private boolean twoFactorEnabled;
}
