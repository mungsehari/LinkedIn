package com.hari.features.authentication.controller;

import com.hari.features.authentication.model.AuthUser;
import com.hari.features.authentication.request.UserRequest;
import com.hari.features.authentication.response.UserResponse;
import com.hari.features.authentication.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {


    private final AuthService authService;

    public AuthenticationController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/user")
    public AuthUser getUser(@RequestAttribute("authenticatedUser") AuthUser user) {
        return user;
    }
    @PostMapping("/login")
    public UserResponse loginPage(@Valid @RequestBody UserRequest request){

        return authService.login(request);
    }
    @PostMapping("/register")
    public UserResponse registerPage(@Valid  @RequestBody UserRequest request) throws MessagingException, UnsupportedEncodingException {
        return authService.register(request);
    }

    @PutMapping("/validate-email-verification-token")
    public String verifyEmail(@RequestParam String token, @RequestAttribute("authenticatedUser") AuthUser user) {
        authService.validateEmailVerificationToken(token, user.getEmail());
        return ("Email verified successfully.");
    }
    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthUser user) {
        authService.sendEmailVerificationToken(user.getEmail());
        return ("Email verification token sent successfully.");
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email) {
        authService.sendPasswordResetToken(email);
        return ("Password reset token sent successfully.");
    }
    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, @RequestParam String token, @RequestParam String email) {
        authService.resetPassword(email, newPassword, token);
        return ("Password reset successfully.");
    }

}
