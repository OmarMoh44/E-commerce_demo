package org.ecommerce.backend.controller;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.LoginRequest;
import org.ecommerce.backend.dto.RegisterRequest;
import org.ecommerce.backend.dto.ResetPasswordRequest;
import org.ecommerce.backend.dto.VerifyEmailRequest;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Map<String, String> login(HttpServletResponse response, @RequestBody @Valid LoginRequest loginRequest) {
        String jwtToken = authService.login(response, loginRequest);
        if (jwtToken == null) throw new JwtException(ErrorMessage.INVALID_CREDENTIALS.getMessage());
        return Map.of("token", jwtToken);
    }

    @PostMapping("/register")
    public void register(@RequestBody @Valid RegisterRequest registrationRequest) {
        authService.register(registrationRequest);
    }

    @PostMapping("/verify-email")
    public void verifyEmail(@RequestBody @Valid VerifyEmailRequest verifyEmailRequest) {
        authService.verifyEmail(verifyEmailRequest);
    }

    @PostMapping("/forget-password")
    public void forgetPassword(@RequestBody Map<String, String> emailMap) {
        String email = emailMap.get("email");
        authService.forgetPassword(email);
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
    }


    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        authService.logout(response);
    }
}
