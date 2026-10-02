package com.example.BiddingApplication.security;

import com.example.BiddingApplication.entity.User;
import com.example.BiddingApplication.service.AuthService;
import com.example.BiddingApplication.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractBearerToken(request);

        if (token != null) {

            try {

                if (jwtService.isValid(token)) {

                    Integer userId =
                            jwtService.extractUserId(token);

                    User user =
                            authService.findById(userId);

                    if (user != null &&
                            !user.isTokenRevoked()) {

                        boolean valid = false;

                        if (jwtService.isAccessToken(token)) {

                            valid =
                                    authService.validateAccessToken(
                                            user,
                                            token
                                    );

                        } else if (
                                jwtService.isRefreshToken(token)) {

                            valid =
                                    authService.validateRefreshToken(
                                            user,
                                            token
                                    );
                        }

                        if (valid) {

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            user,
                                            null,
                                            List.of(
                                                    new SimpleGrantedAuthority(
                                                            "ROLE_" +
                                                                    user.getRole()
                                                    )
                                            )
                                    );

                            SecurityContextHolder
                                    .getContext()
                                    .setAuthentication(
                                            authentication
                                    );
                        }
                    }
                }

            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(
            HttpServletRequest request) {

        String header =
                request.getHeader("Authorization");

        if (header == null ||
                !header.startsWith("Bearer ")) {

            return null;
        }

        String token =
                header.substring(7).trim();

        return token.isBlank() ? null : token;
    }
}
