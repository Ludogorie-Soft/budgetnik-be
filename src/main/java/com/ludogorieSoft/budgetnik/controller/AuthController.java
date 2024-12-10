package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.event.OnConfirmRegistrationEvent;
import com.ludogorieSoft.budgetnik.event.OnPasswordResetRequestEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.AuthService;
import com.ludogorieSoft.budgetnik.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    AuthResponse authResponse = authService.register(request);
    sendVerificationEmail(request.getEmail());
    return ResponseEntity.ok(authResponse);
  }

  @PutMapping("/confirm-registration")
  public ResponseEntity<String> confirmRegistration(@RequestParam("token") String token) {
    return authService.confirmRegistration(token);
  }

  @PostMapping("/send-verification-email")
  public ResponseEntity<String> sendVerificationEmailAgain(@RequestParam("email") String email) {
    User user = userService.findByEmail(email);
    sendVerificationEmail(user.getEmail());
    return ResponseEntity.ok("Email send successfully!");
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> authenticate(
      @RequestBody LoginRequest request, HttpServletResponse servletResponse) {
    AuthResponse authResponse = authService.login(request);
    return ResponseEntity.ok(authResponse);
  }

  @GetMapping("/my-profile")
  public ResponseEntity<AuthResponse> getCurrentUser(@RequestHeader("Authorization") String token) {
    AuthResponse authResponse = authService.getUserByJwt(token);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
    User user = userService.findByEmail(email);
    sendForgotPasswordEmail(user.getEmail());
    return ResponseEntity.ok("Password reset link sent to your email!");
  }

  @PutMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestParam("token") String token) {
    return authService.resetPassword(token);
  }

  @PutMapping("/confirm-new-password")
  public ResponseEntity<String> confirmNewPassword(
      @RequestParam("email") String email,
      @RequestParam("newPassword") String newPassword,
      @RequestParam("confirmNewPassword") String confirmNewPassword) {
    return authService.confirmNewPassword(email, newPassword, confirmNewPassword);
  }

  private void sendVerificationEmail(String email) {
    applicationEventPublisher.publishEvent(new OnConfirmRegistrationEvent(email));
  }

  private void sendForgotPasswordEmail(String email) {
    applicationEventPublisher.publishEvent(new OnPasswordResetRequestEvent(email));
  }
}
