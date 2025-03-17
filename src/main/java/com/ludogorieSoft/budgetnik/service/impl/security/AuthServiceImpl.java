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
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

  public static final String JWT_PREFIX = "Bearer ";
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
  private final MessageSource messageSource;

  @Override
  public UserResponse register(RegisterRequest registerRequest) {
    User user = userService.createUser(registerRequest);
    return modelMapper.map(user, UserResponse.class);
  }

  @Override
  public AuthResponse login(LoginRequest loginRequest, String device) {
    User user = userService.findByEmail(loginRequest.getEmail());

    List<VerificationToken> verificationTokens =
        verificationTokenRepository.findByUserId(user.getId());
    if (!verificationTokens.isEmpty() && !user.isActivated()) {
      throw new ActivateUserException(messageSource);
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (AuthenticationException exception) {
      throw new UserLoginException(messageSource);
    }

    logger.info("User with id " + user.getId() + " logged in!");
    return tokenService.generateAuthResponse(user, device);
  }

  @Override
  public AuthResponse getUserByJwt(String token, String device) {
    if (token == null || token.isEmpty()) {
      throw new InvalidTokenException(messageSource);
    }

    String jwtToken = token.startsWith(JWT_PREFIX) ? token.substring(JWT_PREFIX.length()) : token;
    Token accessToken = tokenService.findByToken(jwtToken);

    if (accessToken == null) {
      throw new InvalidTokenException(messageSource);
    }

    User user = accessToken.getUser();

    boolean isAccessTokenValid = tokenService.isTokenValid(accessToken.getToken(), user);

    String newAccessTokenString;
    if (isAccessTokenValid) {
      newAccessTokenString = accessToken.getToken();
    } else {
      tokenService.setTokenAsExpiredAndRevoked(accessToken);
      newAccessTokenString = jwtService.generateToken(user);
      tokenService.saveToken(user, newAccessTokenString, TokenType.ACCESS, device);
    }

    Token refreshToken = tokenService.getLastValidToken(user, TokenType.REFRESH, device);

    if (refreshToken == null) {
      throw new InvalidTokenException(messageSource);
    }

    boolean isValid = tokenService.isTokenValid(refreshToken.getToken(), user);

    if (!isValid) {
      tokenService.setTokenAsExpiredAndRevoked(refreshToken);
      String refreshTokenString = jwtService.generateRefreshToken(user);
      tokenService.saveToken(user, refreshTokenString, TokenType.REFRESH, device);
    }

    UserResponse userResponse = modelMapper.map(accessToken.getUser(), UserResponse.class);

    logger.info("Get user by token with id " + user.getId());
    return AuthResponse.builder().token(newAccessTokenString).user(userResponse).build();
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
      throw new PasswordException(messageSource);
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
