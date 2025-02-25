package com.ludogorieSoft.budgetnik.service.impl.security;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.ActivateUserException;
import com.ludogorieSoft.budgetnik.exception.InvalidTokenException;
import com.ludogorieSoft.budgetnik.exception.PasswordException;
import com.ludogorieSoft.budgetnik.exception.UserLoginException;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.model.enums.TokenType;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.AuthService;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  private static final String TOKEN_EXPIRED =
      "Изтекла сесия! Моля влезте отново във вашият акаунт!";

  private final UserService userService;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final ModelMapper modelMapper;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResponse register(RegisterRequest registerRequest) {
    User user = userService.createUser(registerRequest);
    return modelMapper.map(user, UserResponse.class);
  }

  @Override
  public AuthResponse login(LoginRequest loginRequest) {
    User user = userService.findByEmail(loginRequest.getEmail());

    List<VerificationToken> verificationTokens =
        verificationTokenRepository.findByUserId(user.getId());
    if (!verificationTokens.isEmpty() && !user.isActivated()) {
      throw new ActivateUserException();
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (UserLoginException exception) {
      throw new UserLoginException("Грешен имейл или парола!");
    }

    logger.info("User with id " + user.getId() + " logged in!");
    return tokenService.generateAuthResponse(user);
  }

  @Override
  public AuthResponse getUserByJwt(String token) {

    if (token == null || token.isEmpty()) {
      throw new InvalidTokenException();
    }

    // Премахваме префикса "Bearer " ако има такъв
    String jwtToken = token.startsWith("Bearer ") ? token.substring("Bearer ".length()) : token;

    // Търсим токена в репото
    Token accessToken = tokenService.findByToken(jwtToken);

    if (accessToken == null) {
      throw new InvalidTokenException();
    }

    // Проверяваме дали токенът е валиден
    User user = accessToken.getUser();
    UserDetails userDetails = accessToken.getUser();
    boolean isTokenValid;

    try {
      isTokenValid = jwtService.isTokenValid(accessToken.getToken(), userDetails);
    } catch (Exception e) {
      isTokenValid = false;
    }

    // Мапваме отговорния потребител
    UserResponse userResponse = modelMapper.map(accessToken.getUser(), UserResponse.class);

    if (!isTokenValid) {
      accessToken.setExpired(true);
      accessToken.setRevoked(true);
      tokenService.saveToken(accessToken);
      // Вземаме всички токени за потребителя
      List<Token> tokens = tokenService.findByUser(user);
      Token refreshToken =
          tokens.stream()
              .filter(x -> x.getTokenType() == TokenType.REFRESH)
              .findFirst()
              .orElse(null);

      String refreshTokenString;

      if (refreshToken == null || !jwtService.isTokenValid(refreshToken.getToken(), user)) {
        if (refreshToken != null) {
          refreshToken.setExpired(true);
          refreshToken.setRevoked(true);
          tokenService.saveToken(refreshToken);
        }
        // Ако няма refresh токен или е невалиден, генерираме нов
        refreshTokenString = jwtService.generateRefreshToken(user);
        tokenService.saveToken(user, refreshTokenString, TokenType.REFRESH);
      } else {
        // Връщаме стария refresh токен
        refreshTokenString = refreshToken.getToken();
      }
    }

    logger.info("Get user by token with id " + user.getId());

    String accessTokenString = jwtService.generateToken(user);
    tokenService.saveToken(user, accessTokenString, TokenType.ACCESS);
    return AuthResponse.builder().token(accessTokenString).user(userResponse).build();
  }

  @Override
  public ResponseEntity<String> resetPassword(String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TOKEN_EXPIRED);
    }
    verificationToken.setCreatedAt(LocalDateTime.now());

    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TOKEN_EXPIRED);
    }

    user.setActivated(false);
    user.setPassword(null);
    userRepository.save(user);
    revokeVerificationTokens(user);
    logger.info("Password reset! User with id " + user.getId());
    return ResponseEntity.ok("Паролата е изтрита!");
  }

  @Override
  public ResponseEntity<String> confirmRegistration(String token) {
    VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
    if (verificationToken == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TOKEN_EXPIRED);
    }
    verificationToken.setCreatedAt(LocalDateTime.now());

    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(TOKEN_EXPIRED);
    }

    user.setActivated(true);
    userRepository.save(user);
    revokeVerificationTokens(user);
    logger.info("Registration confirmed! User with id " + user.getId());
    return ResponseEntity.ok("Успешна регистрация!");
  }

  @Override
  public ResponseEntity<String> confirmNewPassword(
      String email, String newPassword, String confirmNewPassword) {
    User user = userService.findByEmail(email);
    String encodedPassword = passwordEncoder.encode(newPassword);

    if (!passwordEncoder.matches(confirmNewPassword, encodedPassword)) {
      throw new PasswordException("Паролата не съвпада!");
    }

    user.setActivated(true);
    user.setPassword(encodedPassword);
    userRepository.save(user);
    logger.info("New password confirmed! User with id " + user.getId());
    return ResponseEntity.ok("Паролата е променена успешно!");
  }

  private void revokeVerificationTokens(User user) {
    List<VerificationToken> userTokens = verificationTokenRepository.findByUserId(user.getId());
    userTokens.forEach(verificationTokenRepository::delete);
  }
}
