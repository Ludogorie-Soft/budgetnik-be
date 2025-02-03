package com.ludogorieSoft.budgetnik.service.impl.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
import io.jsonwebtoken.JwtException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Mock private UserService userService;

  @Mock private TokenService tokenService;
  @Mock private JwtService jwtService;
  @Mock private ModelMapper modelMapper;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private AuthService authenticationService;
  @Mock private VerificationTokenRepository verificationTokenRepository;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private LoginRequest loginRequest;
  private Token token;
  private User user;
  private UserResponse userResponse;
  private VerificationToken mockVerificationToken;

  private static final String TOKEN_EXPIRED = "Изтекла сесия!";

  @BeforeEach
  void setUp() {
    authenticationService =
        new AuthServiceImpl(
            userService,
            tokenService,
            authenticationManager,
            jwtService,
            modelMapper,
            verificationTokenRepository,
            userRepository,
            passwordEncoder);

    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");

    loginRequest = new LoginRequest();
    String email = "test@example.com";
    loginRequest.setEmail(email);
    loginRequest.setPassword("password");

    token = new Token();
    token.setToken("mock-jwt-token");
    token.setUser(user);

    userResponse = new UserResponse();
    userResponse.setId(user.getId());
    userResponse.setEmail(user.getEmail());

    mockVerificationToken = new VerificationToken();
    mockVerificationToken.setToken("mock-token");
    mockVerificationToken.setUser(user);
    mockVerificationToken.setExpiryDate(calculateExpiryDate());
    mockVerificationToken.setCreatedAt(LocalDateTime.now());
  }

  @Test
  void testResetPassword_ValidToken() {
    // GIVEN
    when(verificationTokenRepository.findByToken("valid-token")).thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.resetPassword("valid-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Паролата е изтрита!", response.getBody());
    assertNull(user.getPassword());
    verify(userRepository, times(1)).save(user);
    verify(verificationTokenRepository, times(1)).findByToken("valid-token");
  }

  @Test
  void testResetPassword_TokenNotFound() {
    // GIVEN
    when(verificationTokenRepository.findByToken("invalid-token")).thenReturn(null);

    // WHEN
    ResponseEntity<String> response = authenticationService.resetPassword("invalid-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(TOKEN_EXPIRED, response.getBody());
    verify(verificationTokenRepository, times(1)).findByToken("invalid-token");
    verifyNoInteractions(userRepository);
  }

  @Test
  void testResetPassword_TokenExpired() {
    // GIVEN
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -1);
    mockVerificationToken.setExpiryDate(new Date(cal.getTime().getTime()));

    when(verificationTokenRepository.findByToken("expired-token"))
        .thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.resetPassword("expired-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(TOKEN_EXPIRED, response.getBody());
    verify(verificationTokenRepository, times(1)).findByToken("expired-token");
    verifyNoInteractions(userRepository);
  }

  @Test
  void testResetPassword_TokenExpiryInFuture() {
    // GIVEN
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 1); // Set token expiry date in the future
    mockVerificationToken.setExpiryDate(new Date(cal.getTime().getTime()));

    when(verificationTokenRepository.findByToken("future-token")).thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.resetPassword("future-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Паролата е изтрита!", response.getBody());
    assertNull(user.getPassword());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void testGetUserByJwt_ValidToken() {
    // GIVEN
    String bearerToken = "Bearer mock-jwt-token";
    String strippedToken = "mock-jwt-token";
    Token accessToken = mock(Token.class);
    Token refreshToken = mock(Token.class);
    User currentUser = mock(User.class);
    UserResponse currentUserResponse = mock(UserResponse.class);
    List<Token> tokens = new ArrayList<>();

    when(refreshToken.getTokenType()).thenReturn(TokenType.REFRESH);
    when(refreshToken.getToken()).thenReturn("refresh-token");
    tokens.add(refreshToken);

    when(tokenService.findByToken(strippedToken)).thenReturn(accessToken);
    when(accessToken.getToken()).thenReturn(strippedToken);
    when(accessToken.getUser()).thenReturn(currentUser);
    when(jwtService.isTokenValid(strippedToken, currentUser)).thenReturn(true);
    when(modelMapper.map(currentUser, UserResponse.class)).thenReturn(currentUserResponse);
    when(tokenService.findByUser(currentUser)).thenReturn(tokens);

    // WHEN
    AuthResponse response = authenticationService.getUserByJwt(bearerToken);

    // THEN
    assertNotNull(response);
    assertEquals(strippedToken, response.getToken());
    assertEquals(currentUserResponse, response.getUser());

    verify(tokenService, times(1)).findByToken(strippedToken);
    verify(jwtService, times(1)).isTokenValid(strippedToken, currentUser);
    verify(modelMapper, times(1)).map(currentUser, UserResponse.class);
    verify(tokenService, times(1)).findByUser(currentUser);
  }

  @Test
  void testGetUserByJwt_NullToken() {
    // GIVEN
    String nullToken = null;

    // WHEN & THEN
    assertThrows(InvalidTokenException.class, () -> authenticationService.getUserByJwt(nullToken));
    verifyNoInteractions(tokenService, jwtService, modelMapper);
  }

  @Test
  void testGetUserByJwt_EmptyToken() {
    // GIVEN
    String emptyToken = "";

    // WHEN & THEN
    assertThrows(InvalidTokenException.class, () -> authenticationService.getUserByJwt(emptyToken));
    verifyNoInteractions(tokenService, jwtService, modelMapper);
  }

  @Test
  void testGetUserByJwt_TokenNotFound() {
    // GIVEN
    String invalidToken = "Bearer invalid-jwt-token";
    when(tokenService.findByToken("invalid-jwt-token")).thenReturn(null);

    // WHEN & THEN
    assertThrows(
            InvalidTokenException.class, () -> authenticationService.getUserByJwt(invalidToken));
    verify(tokenService, times(1)).findByToken("invalid-jwt-token");
    verifyNoInteractions(jwtService, modelMapper);
  }

  @Test
  void testGetUserByJwt_TokenInvalid() {
    // GIVEN
    String validToken = "Bearer mock-jwt-token";
    Token jwt = mock(Token.class);
    User currentUser = mock(User.class);

    when(tokenService.findByToken("mock-jwt-token")).thenReturn(jwt);
    when(jwt.getToken()).thenReturn("mock-jwt-token");
    when(jwt.getUser()).thenReturn(currentUser);
    when(jwtService.isTokenValid("mock-jwt-token", currentUser)).thenReturn(false);

    // WHEN & THEN
    assertThrows(InvalidTokenException.class, () -> authenticationService.getUserByJwt(validToken));

    verify(tokenService).findByToken("mock-jwt-token");
    verify(jwtService).isTokenValid("mock-jwt-token", currentUser);
//    verify(tokenService).revokeAllUserTokens(currentUser);
  }

  @Test
  void testGetUserByJwt_TokenValidationThrowsException() {
    // GIVEN
    String validToken = "Bearer mock-jwt-token";
    Token jwt = mock(Token.class);
    User currentUser = mock(User.class);

    when(tokenService.findByToken("mock-jwt-token")).thenReturn(jwt);
    when(jwt.getToken()).thenReturn("mock-jwt-token");
    when(jwt.getUser()).thenReturn(currentUser);
    when(jwtService.isTokenValid("mock-jwt-token", currentUser)).thenThrow(new JwtException("Invalid JWT"));

    // WHEN & THEN
    assertThrows(InvalidTokenException.class, () -> authenticationService.getUserByJwt(validToken));

    verify(tokenService).findByToken("mock-jwt-token");
    verify(jwtService).isTokenValid("mock-jwt-token", currentUser);
//    verify(tokenService).revokeAllUserTokens(currentUser);
  }

  @Test
  void testRegisterUser_Success() {
    // GIVEN
    RegisterRequest registerRequest = new RegisterRequest();
    User user = new User();

    when(userService.createUser(registerRequest)).thenReturn(user);

    // WHEN
    authenticationService.register(registerRequest);

    // THEN
    verify(userService, times(1)).createUser(registerRequest);
    verify(modelMapper, times(1)).map(user, UserResponse.class);
  }

  @Test
  void testLogin_Success() {
    // GIVEN
    when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);

    // WHEN
    authenticationService.login(loginRequest);

    // THEN
    verify(tokenService, times(1)).generateAuthResponse(user);
  }

  @Test
  void testLogin_ActivatedUser_ThrowsActivateUserException() {
    // GIVEN
    List<VerificationToken> tokens = Collections.singletonList(new VerificationToken());

    when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
    when(verificationTokenRepository.findByUserId(user.getId())).thenReturn(tokens);

    // WHEN & THEN
    ActivateUserException exception =
        assertThrows(ActivateUserException.class, () -> authenticationService.login(loginRequest));

    assertNotNull(exception);
    verify(authenticationManager, never()).authenticate(any());
  }

  @Test
  void testLogin_AuthenticationFailure_ThrowsUserLoginException() {
    // GIVEN
    when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
    when(verificationTokenRepository.findByUserId(user.getId()))
        .thenReturn(Collections.emptyList());

    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(), loginRequest.getPassword())))
        .thenThrow(new UserLoginException("Грешен имейл или парола!"));

    // WHEN & THEN
    UserLoginException exception =
        assertThrows(UserLoginException.class, () -> authenticationService.login(loginRequest));

    assertEquals("Грешен имейл или парола!", exception.getMessage());
  }

  @Test
  void testConfirmRegistration_ValidToken() {
    // GIVEN
    when(verificationTokenRepository.findByToken("valid-token")).thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.confirmRegistration("valid-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Успешна регистрация!", response.getBody());
    assertTrue(user.isActivated());
    verify(userRepository, times(1)).save(user);
    verify(verificationTokenRepository, times(1)).findByToken("valid-token");
  }

  @Test
  void testConfirmRegistration_TokenNotFound() {
    // GIVEN
    when(verificationTokenRepository.findByToken("invalid-token")).thenReturn(null);

    // WHEN
    ResponseEntity<String> response = authenticationService.confirmRegistration("invalid-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(TOKEN_EXPIRED, response.getBody());
    verify(verificationTokenRepository, times(1)).findByToken("invalid-token");
    verifyNoInteractions(userRepository);
  }

  @Test
  void testConfirmRegistration_ExpiredToken() {
    // GIVEN
    Calendar expiredCal = Calendar.getInstance();
    expiredCal.add(Calendar.DATE, -1); // Set expiry date to yesterday
    mockVerificationToken.setExpiryDate(new Date(expiredCal.getTime().getTime()));

    when(verificationTokenRepository.findByToken("expired-token"))
        .thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.confirmRegistration("expired-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(TOKEN_EXPIRED, response.getBody());
    verify(verificationTokenRepository, times(1)).findByToken("expired-token");
    verifyNoInteractions(userRepository);
  }

  @Test
  void testConfirmRegistration_TokenExpiryInFuture() {
    // GIVEN
    Calendar futureCal = Calendar.getInstance();
    futureCal.add(Calendar.DATE, 1); // Set expiry date to tomorrow
    mockVerificationToken.setExpiryDate(new Date(futureCal.getTime().getTime()));

    when(verificationTokenRepository.findByToken("valid-token")).thenReturn(mockVerificationToken);

    // WHEN
    ResponseEntity<String> response = authenticationService.confirmRegistration("valid-token");

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Успешна регистрация!", response.getBody());
    assertTrue(user.isActivated());
    verify(userRepository, times(1)).save(user);
  }

  @Test
  void testConfirmNewPassword_ValidCase() {
    // GIVEN
    String email = "test@example.com";
    String newPassword = "newPassword";
    String confirmNewPassword = "newPassword";

    when(userService.findByEmail(email)).thenReturn(user);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
    when(passwordEncoder.matches(confirmNewPassword, "encodedPassword")).thenReturn(true);

    // WHEN
    ResponseEntity<String> response =
        authenticationService.confirmNewPassword(email, newPassword, confirmNewPassword);

    // THEN
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals("Паролата е променена успешно!", response.getBody());

    verify(userRepository, times(1)).save(user);
    verify(passwordEncoder, times(1)).encode(newPassword);
    verify(passwordEncoder, times(1)).matches(confirmNewPassword, "encodedPassword");
  }

  @Test
  void testConfirmNewPassword_PasswordsDoNotMatch() {
    // GIVEN
    String email = "test@example.com";
    String newPassword = "newPassword";
    String confirmNewPassword = "differentPassword";

    when(userService.findByEmail(email)).thenReturn(user);
    when(passwordEncoder.encode(newPassword)).thenReturn("encodedPassword");
    when(passwordEncoder.matches(confirmNewPassword, "encodedPassword")).thenReturn(false);

    // WHEN & THEN
    PasswordException exception =
        assertThrows(
            PasswordException.class,
            () -> authenticationService.confirmNewPassword(email, newPassword, confirmNewPassword));

    assertEquals("Паролата не съвпада!", exception.getMessage());

    verify(userRepository, never()).save(user);
    verify(passwordEncoder, times(1)).encode(newPassword);
    verify(passwordEncoder, times(1)).matches(confirmNewPassword, "encodedPassword");
  }

  @Test
  void testConfirmNewPassword_UserNotFound() {
    // GIVEN
    String email = "notfound@example.com";
    String newPassword = "newPassword";
    String confirmNewPassword = "newPassword";

    when(userService.findByEmail(email)).thenReturn(null);

    // WHEN & THEN
    assertThrows(
        PasswordException.class,
        () -> authenticationService.confirmNewPassword(email, newPassword, confirmNewPassword));
  }

  private Date calculateExpiryDate() {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Timestamp(cal.getTime().getTime()));
    cal.add(Calendar.MINUTE, 60 * 24);
    return new Date(cal.getTime().getTime());
  }
}
