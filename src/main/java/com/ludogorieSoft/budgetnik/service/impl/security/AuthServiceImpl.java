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
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.AuthService;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import com.ludogorieSoft.budgetnik.service.UserService;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

  private static final String TOKEN_EXPIRED = "Изтекла сесия!";

  private final UserService userService;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final ModelMapper modelMapper;
  private final VerificationTokenRepository verificationTokenRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public AuthResponse register(RegisterRequest registerRequest) {
    User user = userService.createUser(registerRequest);
    return tokenService.generateAuthResponse(user);
  }

  @Override
  public AuthResponse login(LoginRequest loginRequest) {
    User user = userService.findByEmail(loginRequest.getEmail());

    List<VerificationToken> verificationTokens =
        verificationTokenRepository.findByUserId(user.getId());
    if (!verificationTokens.isEmpty()) {
      throw new ActivateUserException();
    }

    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(), loginRequest.getPassword()));
    } catch (UserLoginException exception) {
      throw new UserLoginException("Грешен имейл или парола!");
    }

    tokenService.revokeAllUserTokens(user);

    return tokenService.generateAuthResponse(user);
  }

  @Override
  public AuthResponse getUserByJwt(String jwtToken) {
    if (jwtToken == null || jwtToken.isEmpty()) {
      throw new InvalidTokenException();
    }

    Token accessToken = tokenService.findByToken(jwtToken);

    if (accessToken == null) {
      throw new InvalidTokenException();
    }

    User user = accessToken.getUser();

    boolean isTokenValid;

    try {
      isTokenValid = jwtService.isTokenValid(accessToken.getToken(), user);
    } catch (JwtException jwtException) {
      isTokenValid = false;
    }

    if (!isTokenValid) {
      tokenService.revokeAllUserTokens(user);
      throw new InvalidTokenException();
    }

    UserResponse userResponse = modelMapper.map(accessToken.getUser(), UserResponse.class);

    return AuthResponse.builder().token(accessToken.getToken()).user(userResponse).build();
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
    return ResponseEntity.ok("Паролата е променена успешно!");
  }

  private void revokeVerificationTokens(User user) {
    List<VerificationToken> userTokens = verificationTokenRepository.findByUserId(user.getId());
    userTokens.forEach(verificationTokenRepository::delete);
  }
}
