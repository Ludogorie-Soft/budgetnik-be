package com.ludogorieSoft.budgetnik.service.impl.security;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final JwtService jwtService;
  private final ModelMapper modelMapper;
  private final TokenRepository tokenRepository;

  @Override
  public AuthResponse generateAuthResponse(User user, String device) {
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    saveToken(user, jwtToken, TokenType.ACCESS, device);
    saveToken(user, refreshToken, TokenType.REFRESH, device);
    return AuthResponse.builder()
        .token(jwtToken)
        .user(modelMapper.map(user, UserResponse.class))
        .build();
  }

  @Override
  public Token findByToken(String jwt) {
    return tokenRepository.findByToken(jwt).orElse(null);
  }

  @Override
  public List<Token> findByUser(User user) {
    return tokenRepository.findAllByUser(user);
  }

  @Override
  public void saveToken(User user, String jwtToken, TokenType tokenType, String device) {
    Token token =
        Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(tokenType)
            .expired(false)
            .revoked(false)
            .device(user.getEmail() + device)
            .build();

    saveToken(token);
  }

  @Override
  @Transactional
  public void logoutToken(String jwt, String device) {
    Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

    if (storedToken != null) {
      User user = storedToken.getUser();
      setTokenAsExpiredAndRevoked(storedToken);

      Token refreshToken = getLastValidToken(user, TokenType.REFRESH, device);
      setTokenAsExpiredAndRevoked(refreshToken);
    }

    SecurityContextHolder.clearContext();
  }

  @Override
  public void saveToken(Token token) {
    tokenRepository.save(token);
  }

  @Override
  public Token getLastValidToken(User user, TokenType tokenType, String device) {
    String deviceId = user.getEmail() + device;
    return tokenRepository
        .findByUserAndTokenTypeAndDeviceAndExpiredFalseAndRevokedFalse(user, tokenType, deviceId)
        .stream()
        .findFirst()
        .orElse(null);
  }

  @Override
  public void setTokenAsExpiredAndRevoked(Token token) {
    token.setExpired(true);
    token.setRevoked(true);
    saveToken(token);
  }

  @Override
  public boolean isTokenValid(String token, UserDetails user) {
    return jwtService.isTokenValid(token, user);
  }

  @Scheduled(cron = "0 0 0,12 * * ?")
  public void deleteOldExpiredTokens() {
    List<Token> expiredTokens = tokenRepository.findAllByExpiredTrue();
    tokenRepository.deleteAll(expiredTokens);
  }
}
