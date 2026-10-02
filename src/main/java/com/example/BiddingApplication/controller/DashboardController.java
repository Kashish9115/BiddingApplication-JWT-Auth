


package com.example.BiddingApplication.controller;

import com.example.BiddingApplication.dto.UserResponse;
import com.example.BiddingApplication.entity.User;
import com.example.BiddingApplication.service.AuthService;
import com.example.BiddingApplication.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ModelMapper mapper;


    private final AuthService auth;
    private final JwtService jwt;


    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam String token,
            Model model) {

        if (token == null || token.isBlank()) {
            return "redirect:/login";
        }

        User user = auth.findById(jwt.extractUserId(token));

        if (user == null ||
                !auth.validateAccessToken(user, token)) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("accessToken", token);

        return "dashboard";
    }
}