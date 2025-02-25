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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

  private final JwtService jwtService;
  private final ModelMapper modelMapper;
  private final TokenRepository tokenRepository;

  @Value("${spring.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  @Value("${spring.security.jwt.expiration}")
  private long jwtExpiration;

  @Override
  public AuthResponse generateAuthResponse(User user) {
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);
    saveToken(user, jwtToken, TokenType.ACCESS);
    saveToken(user, refreshToken, TokenType.REFRESH);
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
  public void saveToken(User user, String jwtToken, TokenType tokenType) {
    Token token =
        Token.builder()
            .user(user)
            .token(jwtToken)
            .tokenType(tokenType)
            .expired(false)
            .revoked(false)
            .build();

    saveToken(token);
  }

  @Override
  @Transactional
  public void logoutToken(String jwt) {
    Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

    if (storedToken != null) {
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      saveToken(storedToken);
    }

    SecurityContextHolder.clearContext();
  }

  @Override
  public void saveToken(Token token) {
    tokenRepository.save(token);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  public void deleteOldExpiredTokens() {
    List<Token> expiredTokens = tokenRepository.findAllByExpiredTrue();
    tokenRepository.deleteAll(expiredTokens);
  }
}
