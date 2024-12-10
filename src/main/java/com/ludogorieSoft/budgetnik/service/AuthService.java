package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

public interface AuthService {
  AuthResponse register(RegisterRequest registerRequest);

  AuthResponse login(LoginRequest loginRequest);

  AuthResponse getUserByJwt(String jwtToken);

  ResponseEntity<String> resetPassword(String token);

  ResponseEntity<String> confirmRegistration(String token);

  ResponseEntity<String> confirmNewPassword(String email, String newPassword, String confirmNewPassword);
}
