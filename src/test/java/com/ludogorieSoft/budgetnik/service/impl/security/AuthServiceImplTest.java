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
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.exception.ActivateUserException;
import com.ludogorieSoft.budgetnik.exception.InvalidTokenException;
import com.ludogorieSoft.budgetnik.exception.PasswordException;
import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.AuthService;
import com.ludogorieSoft.budgetnik.service.JwtService;
import com.ludogorieSoft.budgetnik.service.TokenService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
  @Mock private MessageSource messageSource;

  private LoginRequest loginRequest;
  private Token token;
  private User user;
  private UserResponse userResponse;
  private VerificationToken mockVerificationToken;
  private static final String DEVICE_ID = "DeviceId";

  private static final String TOKEN_EXPIRED =
      "Изтекла сесия! Моля влезте отново във вашият акаунт!";

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
            passwordEncoder,
            messageSource);

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
  void testGetUserByJwt_NullToken() {
    // WHEN & THEN
    assertThrows(
        InvalidTokenException.class, () -> authenticationService.getUserByJwt(null, DEVICE_ID));
    verifyNoInteractions(tokenService, jwtService, modelMapper);
  }

  @Test
  void testGetUserByJwt_EmptyToken() {
    // GIVEN
    String emptyToken = "";

    // WHEN & THEN
    assertThrows(
        InvalidTokenException.class,
        () -> authenticationService.getUserByJwt(emptyToken, DEVICE_ID));
    verifyNoInteractions(tokenService, jwtService, modelMapper);
  }

  @Test
  void testGetUserByJwt_TokenNotFound() {
    // GIVEN
    String invalidToken = "Bearer invalid-jwt-token";
    when(tokenService.findByToken("invalid-jwt-token")).thenReturn(null);

    // WHEN & THEN
    assertThrows(
        InvalidTokenException.class,
        () -> authenticationService.getUserByJwt(invalidToken, DEVICE_ID));
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

    // WHEN & THEN
    assertThrows(
        InvalidTokenException.class,
        () -> authenticationService.getUserByJwt(validToken, DEVICE_ID));

    verify(tokenService).findByToken("mock-jwt-token");
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

    // WHEN & THEN
    assertThrows(
        InvalidTokenException.class,
        () -> authenticationService.getUserByJwt(validToken, DEVICE_ID));

    verify(tokenService).findByToken("mock-jwt-token");
  }

  @Test
  void testRegisterUser_Success() {
    // GIVEN
    RegisterRequest registerRequest = new RegisterRequest();
    User testUser = new User();

    when(userService.createUser(registerRequest)).thenReturn(testUser);

    // WHEN
    authenticationService.register(registerRequest);

    // THEN
    verify(userService, times(1)).createUser(registerRequest);
    verify(modelMapper, times(1)).map(testUser, UserResponse.class);
  }

  @Test
  void testLogin_Success() {
    // GIVEN
    when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
    user.setActivated(true);

    // WHEN
    authenticationService.login(loginRequest, DEVICE_ID);

    // THEN
    verify(tokenService, times(1)).generateAuthResponse(user, DEVICE_ID);
  }

  @Test
  void testLogin_ActivatedUser_ThrowsActivateUserException() {
    // GIVEN
    when(userService.findByEmail(loginRequest.getEmail())).thenReturn(user);
    user.setActivated(false);
    when(verificationTokenRepository.findByUserId(user.getId()))
        .thenReturn(List.of(new VerificationToken()));

    // WHEN & THEN
    ActivateUserException exception =
        assertThrows(
            ActivateUserException.class,
            () -> authenticationService.login(loginRequest, DEVICE_ID));

    assertNotNull(exception);
    verify(authenticationManager, never()).authenticate(any());
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
        assertThrows(
            PasswordException.class,
            () -> authenticationService.confirmNewPassword(email, newPassword, confirmNewPassword));

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
