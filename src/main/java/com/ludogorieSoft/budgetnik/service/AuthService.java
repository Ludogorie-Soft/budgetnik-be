package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.function.Consumer;

public interface AuthService {
  UserResponse register(RegisterRequest registerRequest);

  AuthResponse login(LoginRequest loginRequest, String device);

  AuthResponse getUserByJwt(String jwtToken, String device);

  ResponseEntity<String> resetPassword(String token);

  ResponseEntity<String> confirmRegistration(String token);

  ResponseEntity<String> confirmNewPassword(String email, String newPassword, String confirmNewPassword);
}
