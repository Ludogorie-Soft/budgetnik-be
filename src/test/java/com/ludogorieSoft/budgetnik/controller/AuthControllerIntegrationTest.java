package com.ludogorieSoft.budgetnik.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.event.OnConfirmRegistrationEvent;
import com.ludogorieSoft.budgetnik.event.OnPasswordResetRequestEvent;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.VerificationToken;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import com.ludogorieSoft.budgetnik.service.impl.security.TokenServiceImpl;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

  private static final String TEST_NAME = "Test Name";
  private static final String TEST_EMAIL = "test@email.com";
  private static final String TEST_PASSWORD = "password";
  private static final String TEST_EXPIRED_JWT =
      "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpZ25hdG92X3JzQGFidi5iZyIsImlhdCI6MTczMzAzODMxOCwiZXhwIjoxNzMzMTI0NzE4fQ.bKuxCmFUznESGjYTYJzdGg4Wz9PRPD4PYhx9Lr8Ou2g";
  private static final String TOKEN = "123456";
  private static final String LOGIN_URL = "/api/auth/login";

  private static final String REGISTER_URL = "/api/auth/register";

  @Autowired protected TestRestTemplate testRestTemplate;

  @Autowired private UserRepository userRepository;

  @Autowired private TokenRepository tokenRepository;

  @Autowired private TokenServiceImpl tokenService;

  @Autowired private VerificationTokenRepository verificationTokenRepository;

  @Autowired private ApplicationEventPublisher applicationEventPublisher;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private HttpHeaders headers;

  @BeforeEach
  void setUp() {
    registerRequest = createRegisterDto();
    loginRequest = createLoginDto();
    headers = new HttpHeaders();
    headers.set("DeviceId", "DeviceId");
  }

  @AfterEach
  void cleanUp() {
    verificationTokenRepository.deleteAll();
    tokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void testLoginSuccessfully() {
    createUserInDb(registerRequest);

    User user = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertFalse(user.isActivated());

    verificationTokenRepository.deleteAll();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(TOKEN);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(1)));
    verificationTokenRepository.save(verificationToken);

    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-registration?token=" + TOKEN, HttpMethod.PUT, null, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    User savedUser = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertNotNull(savedUser);
    assertTrue(savedUser.isActivated());

    // WHEN
    ResponseEntity<AuthResponse> loginResponse =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    AuthResponse authResponse = loginResponse.getBody();
    assertNotNull(authResponse);
    assertNotNull(authResponse.getUser());
    assertEquals(registerRequest.getEmail(), authResponse.getUser().getEmail());
    assertNotNull(authResponse.getToken());
  }

  @Test
  void testGetMyProfileSuccessfully() {
    createUserInDb(registerRequest);

    User user = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertFalse(user.isActivated());

    verificationTokenRepository.deleteAll();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(TOKEN);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(1)));
    verificationTokenRepository.save(verificationToken);

    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-registration?token=" + TOKEN, HttpMethod.PUT, null, String.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());

    User savedUser = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertNotNull(savedUser);
    assertTrue(savedUser.isActivated());

    ResponseEntity<AuthResponse> loginResponse =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    AuthResponse authResponse = loginResponse.getBody();
    assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    assertNotNull(authResponse);

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + authResponse.getToken());

    // WHEN
    ResponseEntity<AuthResponse> userResponse =
        testRestTemplate.exchange(
            "/api/auth/my-profile",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.OK, userResponse.getStatusCode());
    assertNotNull(userResponse.getBody());

    AuthResponse currentUser = userResponse.getBody();
    assertEquals(registerRequest.getEmail(), currentUser.getUser().getEmail());
  }

  @Test
  void testRegisterUserSuccessfully() {
    // WHEN
    ResponseEntity<UserResponse> response = createUserInDb(registerRequest);
    applicationEventPublisher.publishEvent(
        new OnConfirmRegistrationEvent(registerRequest.getEmail()));

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    UserResponse userResponse = response.getBody();
    assertNotNull(userResponse);
  }

  @Test
  void testLoginShouldThrowWhenUserIsNotActivated() {
    // GIVEN
    createUserInDb(registerRequest);

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testLoginShouldThrowWhenWrongEmail() {
    // GIVEN
    createUserInDb(registerRequest);
    loginRequest.setEmail("wrong@email.com");

    HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange(LOGIN_URL, HttpMethod.POST, entity, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testLoginShouldThrowWhenWrongPassword() {
    // GIVEN
    createUserInDb(registerRequest);
    loginRequest.setPassword("wrong-password");

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testConfirmRegistration_Success() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    createUserInDb(registerRequest);

    User user = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertFalse(user.isActivated());

    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(1)));
    verificationTokenRepository.save(verificationToken);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-registration?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());

    User savedUser = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertNotNull(savedUser);
    assertTrue(savedUser.isActivated());
  }

  @Test
  void testConfirmRegistrationShouldThrowWhenVerificationTokenExpired() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    createUserInDb(registerRequest);
    applicationEventPublisher.publishEvent(
        new OnConfirmRegistrationEvent(registerRequest.getEmail()));

    User user = userRepository.findByEmail(registerRequest.getEmail()).get();

    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().minusDays(1)));
    verificationTokenRepository.save(verificationToken);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-registration?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testConfirmRegistrationShouldThrowWhenTokenIsNull() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    createUserInDb(registerRequest);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-registration?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetMyProfileShouldThrowWhenInvalidJwt() {
    // GIVEN
    headers.set("Authorization", "invalid-token");

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange(
            "/api/auth/my-profile",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testGetMyProfileShouldThrowWhenNoAuthorizationHeader() {
    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange("/api/auth/my-profile", HttpMethod.GET, null, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testGetMyProfileShouldThrowWhenJwtIsNull() {
    // GIVEN
    headers.set("Authorization", null);

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange(
            "/api/auth/my-profile",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testGetMyProfileShouldThrowWhenJwtExpired() {
    // GIVEN
    createUserInDb(registerRequest);
    verificationTokenRepository.deleteAll();

    ResponseEntity<AuthResponse> loginResponse =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    AuthResponse authResponse = loginResponse.getBody();
    assertNotNull(authResponse);

    headers.set("Authorization", TEST_EXPIRED_JWT);

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange(
            "/api/auth/my-profile",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @Test
  void testResetPasswordSuccessfully() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    ResponseEntity<UserResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getId()).get();
    assertFalse(user.isActivated());

    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().plusDays(1)));
    verificationTokenRepository.save(verificationToken);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/reset-password?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());

    User savedUser = userRepository.findByEmail(registerRequest.getEmail()).get();
    assertNotNull(savedUser);
    assertNull(savedUser.getPassword());
    assertFalse(savedUser.isActivated());
  }

  @Test
  void testResetPasswordShouldThrowWhenVerificationTokenExpired() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    ResponseEntity<UserResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getId()).get();

    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setCreatedAt(LocalDateTime.now());
    verificationToken.setExpiryDate(Date.valueOf(LocalDate.now().minusDays(1)));
    verificationTokenRepository.save(verificationToken);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/reset-password?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testResetPasswordShouldThrowWhenTokenIsNull() {
    // GIVEN
    String token = UUID.randomUUID().toString();
    createUserInDb(registerRequest);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/reset-password?token=" + token, HttpMethod.PUT, null, String.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testConfirmNewPasswordSuccessfully() {
    // GIVEN
    createUserInDb(registerRequest);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-new-password?email="
                + registerRequest.getEmail()
                + "&newPassword=newPassword&confirmNewPassword=newPassword",
            HttpMethod.PUT,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testConfirmNewPasswordShouldThrowWhenUserNotFound() {
    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-new-password?email=unknown@email.com"
                + "&newPassword=newPassword&confirmNewPassword=newPassword",
            HttpMethod.PUT,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testConfirmNewPasswordShouldThrowWhenPasswordNotMatch() {
    // GIVEN
    ResponseEntity<UserResponse> userResponse = createUserInDb(registerRequest);
    UserResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-new-password?email="
                + registerRequest.getEmail()
                + "&newPassword=newPassword&confirmNewPassword=wrongPassword",
            HttpMethod.PUT,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testSendForgotPasswordEmail() {
    // GIVEN
    ResponseEntity<UserResponse> userResponse = createUserInDb(registerRequest);
    UserResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    applicationEventPublisher.publishEvent(
        new OnPasswordResetRequestEvent(registerRequest.getEmail()));

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/forgot-password?email=" + registerRequest.getEmail(),
            HttpMethod.POST,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testSendVerificationEmail() {
    // GIVEN
    ResponseEntity<UserResponse> userResponse = createUserInDb(registerRequest);
    UserResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    applicationEventPublisher.publishEvent(
        new OnConfirmRegistrationEvent(registerRequest.getEmail()));

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/send-verification-email?email=" + registerRequest.getEmail(),
            HttpMethod.POST,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  private static RegisterRequest createRegisterDto() {
    RegisterRequest registerDto = new RegisterRequest();
    registerDto.setName(TEST_NAME);
    registerDto.setEmail(TEST_EMAIL);
    registerDto.setPassword(TEST_PASSWORD);
    registerDto.setConfirmPassword(TEST_PASSWORD);
    return registerDto;
  }

  private static LoginRequest createLoginDto() {
    LoginRequest loginDto = new LoginRequest();
    loginDto.setEmail(TEST_EMAIL);
    loginDto.setPassword(TEST_PASSWORD);
    return loginDto;
  }

  private ResponseEntity<UserResponse> createUserInDb(RegisterRequest request) {
    return testRestTemplate.postForEntity(REGISTER_URL, request, UserResponse.class);
  }
}
