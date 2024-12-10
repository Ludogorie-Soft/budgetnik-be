package com.ludogorieSoft.budgetnik.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
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

  private static final String LOGIN_URL = "/api/auth/login";

  private static final String REGISTER_URL = "/api/auth/register";

  @Autowired protected TestRestTemplate testRestTemplate;

  @Autowired private UserRepository userRepository;

  @Autowired private TokenRepository tokenRepository;

  @Autowired private TokenServiceImpl tokenService;

  @Autowired private VerificationTokenRepository verificationTokenRepository;

//  @Mock
//  private ApplicationEventPublisher applicationEventPublisher;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    registerRequest = createRegisterDto();
    loginRequest = createLoginDto();
  }

  @AfterEach
  void cleanUp() {
    verificationTokenRepository.deleteAll();
    tokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void testRegisterUserSuccessfully() {
    // WHEN
    ResponseEntity<AuthResponse> response = createUserInDb(registerRequest);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    AuthResponse authResponse = response.getBody();
    assertNotNull(authResponse);
    assertNotNull(authResponse.getUser());
    assertEquals(TEST_EMAIL, authResponse.getUser().getEmail());
  }

  @Test
  void testRegisterUserShouldThrowWhenUserExists() {
    // GIVEN
    createUserInDb(registerRequest);

    // WHEN
    ResponseEntity<AuthResponse> response = createUserInDb(registerRequest);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testRegisterUserShouldThrowWhenPasswordNotMatch() {
    // GIVEN
    registerRequest.setConfirmPassword("wrong-password");

    // WHEN
    ResponseEntity<AuthResponse> response = createUserInDb(registerRequest);

    // THEN
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  void testLoginSuccessfully() {
    // GIVEN
    createUserInDb(registerRequest);
    verificationTokenRepository.deleteAll();

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    AuthResponse authResponse = response.getBody();
    assertNotNull(authResponse);
    assertNotNull(authResponse.getUser());
    assertEquals(TEST_EMAIL, authResponse.getUser().getEmail());
    assertNotNull(authResponse.getToken());
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

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getUser().getId()).get();
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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getUser().getId()).get();

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
  void testGetMyProfileSuccessfully() {
    // GIVEN
    createUserInDb(registerRequest);
    verificationTokenRepository.deleteAll();

    ResponseEntity<AuthResponse> loginResponse =
        testRestTemplate.postForEntity(LOGIN_URL, loginRequest, AuthResponse.class);

    AuthResponse authResponse = loginResponse.getBody();
    assertNotNull(authResponse);
    assertNotNull(authResponse.getToken());

    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", authResponse.getToken());

    // WHEN
    ResponseEntity<AuthResponse> response =
        testRestTemplate.exchange(
            "/api/auth/my-profile",
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            AuthResponse.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());

    AuthResponse currentUser = response.getBody();
    assertEquals(registerRequest.getEmail(), currentUser.getUser().getEmail());
  }

  @Test
  void testGetMyProfileShouldThrowWhenInvalidJwt() {
    // GIVEN
    HttpHeaders headers = new HttpHeaders();
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
    HttpHeaders headers = new HttpHeaders();
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
    assertNotNull(authResponse.getToken());

    HttpHeaders headers = new HttpHeaders();
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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getUser().getId()).get();
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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);

    assertNotNull(userResponse.getBody());
    User user = userRepository.findById(userResponse.getBody().getUser().getId()).get();

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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);
    AuthResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    // WHEN
    ResponseEntity<String> response =
        testRestTemplate.exchange(
            "/api/auth/confirm-new-password?email="
                + authResponse.getUser().getEmail()
                + "&newPassword=newPassword&confirmNewPassword=newPassword",
            HttpMethod.PUT,
            null,
            String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
    User newPasswordUser = userRepository.findById(authResponse.getUser().getId()).get();
    assertNotNull(newPasswordUser);
    assertNotNull(newPasswordUser.getPassword());
    assertTrue(newPasswordUser.isActivated());
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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);
    AuthResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    // WHEN
    ResponseEntity<String> response =
            testRestTemplate.exchange(
                    "/api/auth/confirm-new-password?email="
                            + authResponse.getUser().getEmail()
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
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);
    AuthResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    // WHEN
    ResponseEntity<String> response = testRestTemplate
            .exchange("/api/auth/forgot-password?email=" + authResponse.getUser().getEmail(), HttpMethod.POST, null, String.class);

    // THEN
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void testSendVerificationEmail() {
    // GIVEN
    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);
    AuthResponse authResponse = userResponse.getBody();
    assertNotNull(authResponse);

    // WHEN
    ResponseEntity<String> response = testRestTemplate
            .exchange("/api/auth/send-verification-email?email=" + authResponse.getUser().getEmail(), HttpMethod.POST, null, String.class);

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

  private ResponseEntity<AuthResponse> createUserInDb(RegisterRequest request) {
    return testRestTemplate.postForEntity(REGISTER_URL, request, AuthResponse.class);
  }
}
