package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface TokenService {
  AuthResponse generateAuthResponse(User user, String device);

  Token findByToken(String jwt);

  List<Token> findByUser(User user);

  void saveToken(User user, String jwtToken, TokenType tokenType, String device);

  void logoutToken(String jwt, String device);

  void saveToken(Token token);

  Token getLastValidToken(User user, TokenType tokenType, String device);

  void setTokenAsExpiredAndRevoked(Token token);

  boolean isTokenValid(String token, UserDetails user);
}
