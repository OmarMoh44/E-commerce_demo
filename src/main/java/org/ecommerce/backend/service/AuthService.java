package org.ecommerce.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.ecommerce.backend.dto.LoginRequest;
import org.ecommerce.backend.dto.RegisterRequest;
import org.ecommerce.backend.dto.ResetPasswordRequest;
import org.ecommerce.backend.dto.VerifyEmailRequest;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.EmailDetails;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.UserRepository;
import org.ecommerce.backend.util.CookieUtil;
import org.ecommerce.backend.util.EmailUtil;
import org.ecommerce.backend.util.RedisUtil;
import org.ecommerce.backend.util.UserUtil;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final CookieUtil cookieUtil;
    private final EmailUtil emailUtil;
    private final UserUtil userUtil;
    private final RedisUtil redisUtil;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public String login(HttpServletResponse response, LoginRequest loginRequest) {
        // runs UserDetailsService bean to find user from DB and compares passwords
        Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        String jwtToken = null;
        if (authentication.isAuthenticated()) {
            jwtToken = jwtService.generateToken(loginRequest.getEmail());
            Cookie cookie = cookieUtil.createCookie(jwtToken);
            response.addCookie(cookie);
        }
        return jwtToken;
    }

    public void register(RegisterRequest registerRequest) {
        userUtil.verifyUserNotExist(registerRequest.getEmail());
        String generatedCode = RandomStringUtils.randomAlphanumeric(10);
        String redisCodeKey = String.format("verifyEmail:%s", registerRequest.getEmail());
        String redisUseInfoKey = String.format("userInfo:%s:%s", registerRequest.getEmail(), generatedCode);
        redisUtil.setValue(redisCodeKey, generatedCode, Duration.ofHours(1));
        redisUtil.setValue(redisUseInfoKey, registerRequest, Duration.ofHours(1));
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(registerRequest.getEmail())
                .subject("Email Verification Code")
                .msgBody(String.format("Your verification code is: %s", generatedCode))
                .build();
        emailUtil.sendSimpleMail(emailDetails);
    }

    @Transactional
    @Caching(evict = @CacheEvict(value = "users", allEntries = true))
    public void verifyEmail(VerifyEmailRequest verifyEmailRequest) {
        String redisCodeKey = String.format("verifyEmail:%s", verifyEmailRequest.getEmail());
        String redisUseInfoKey = String.format("userInfo:%s:%s", verifyEmailRequest.getEmail(), verifyEmailRequest.getVerificationCode());
        String storedCode = (String) redisUtil.getValue(redisCodeKey);
        if (storedCode == null || !storedCode.equals(verifyEmailRequest.getVerificationCode())) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_REQUEST.getMessage());
        }
        RegisterRequest registerRequest = objectMapper.convertValue(redisUtil.getValue(redisUseInfoKey), RegisterRequest.class);
        if (registerRequest == null) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_REQUEST.getMessage());
        }
        User newUser = modelMapper.map(registerRequest, User.class);
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(newUser);
        redisUtil.deleteValue(redisCodeKey);
        redisUtil.deleteValue(redisUseInfoKey);
    }

    public void forgetPassword(String email) {
        userUtil.verifyUserExist(email);
        String generatedCode = RandomStringUtils.randomAlphanumeric(10);
        String redisCodeKey = String.format("resetPassword:%s", email);
        redisUtil.setValue(redisCodeKey, generatedCode, Duration.ofHours(1));
        EmailDetails emailDetails = EmailDetails.builder()
                .recipient(email)
                .subject("Password Reset Code")
                .msgBody(String.format("Your password reset code is: %s", generatedCode))
                .build();
        emailUtil.sendSimpleMail(emailDetails);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String redisCodeKey = String.format("resetPassword:%s", resetPasswordRequest.getEmail());
        String storedCode = (String) redisUtil.getValue(redisCodeKey);
        if (storedCode == null || !storedCode.equals(resetPasswordRequest.getVerificationCode())) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_REQUEST.getMessage());
        }
        User user = userUtil.verifyUserExist(resetPasswordRequest.getEmail());
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        userRepository.save(user);
        redisUtil.deleteValue(redisCodeKey);
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = cookieUtil.clearCookie();
        response.addCookie(cookie);
    }
}
