package com.ludogorieSoft.budgetnik.service.impl.security;

import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import jakarta.servlet.http.Cookie;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
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
    saveToken(user, jwtToken, TokenType.ACCESS);
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

    tokenRepository.save(token);
  }

  @Override
  public void revokeToken(Token token) {
    tokenRepository.delete(token);
  }

  @Override
  public void revokeAllUserTokens(User user) {
    tokenRepository.deleteAll(tokenRepository.findAllByUser(user));
  }

  @Override
  @Transactional
  public void logoutToken(String jwt) {
    Token storedToken = tokenRepository.findByToken(jwt).orElse(null);

    if (storedToken == null) {
      return;
    }

    revokeAllUserTokens(storedToken.getUser());
    SecurityContextHolder.clearContext();
  }
}
