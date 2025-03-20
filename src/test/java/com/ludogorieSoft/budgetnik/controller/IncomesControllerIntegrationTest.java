package com.ludogorieSoft.budgetnik.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.IncomeCategoryRepository;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.repository.TokenRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.repository.VerificationTokenRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
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
class IncomesControllerIntegrationTest {
  private static final String TEST_NAME = "Test Name";
  private static final String TEST_EMAIL = "test@email.com";
  private static final String TEST_PASSWORD = "password";

  private static final String REGISTER_URL = "/api/auth/register";
  private static final String LOGIN_URL = "/api/auth/login";
  private static final String INCOMES_URL = "/api/incomes";
  private static final String CATEGORY_URL = "/api/categories/incomes";

  @Autowired private UserRepository userRepository;
  @Autowired private IncomeRepository incomeRepository;
  @Autowired private TestRestTemplate testRestTemplate;
  @Autowired private VerificationTokenRepository verificationTokenRepository;
  @Autowired private TokenRepository tokenRepository;
  @Autowired private IncomeCategoryRepository incomeCategoryRepository;
  @Autowired private MessageSource messageSource;

  private HttpHeaders headers;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private AuthResponse registerResponse;
  private AuthResponse authResponse;
  private IncomeRequestDto incomeRequestDto;
  private CategoryRequestDto categoryRequestDto;
  private CategoryResponseDto categoryResponseDto;

//  @BeforeEach
//  void setup() {
//    registerRequest = createRegisterDto();
//    ResponseEntity<AuthResponse> userResponse = createUserInDb(registerRequest);
//    registerResponse = userResponse.getBody();
//    assertNotNull(registerResponse);
//    verificationTokenRepository.deleteAll();
//    loginRequest = createLoginDto();
//    headers = new HttpHeaders();
//    headers.set("DeviceId", "DeviceId");
//    HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);
//    ResponseEntity<AuthResponse> loginResponse =
//        testRestTemplate.exchange(LOGIN_URL, HttpMethod.POST, entity, AuthResponse.class);
//    authResponse = loginResponse.getBody();
//    assertNotNull(authResponse);
//    headers.set("Authorization", "Bearer " + authResponse.getToken());
//    categoryRequestDto = createCategoryRequestDto();
//    categoryResponseDto = createCategoryInDb();
//    incomeRequestDto = createIncomeRequest(authResponse.getUser().getId());
//  }
//
//  @AfterEach
//  void cleanUp() {
//    tokenRepository.deleteAll();
//    verificationTokenRepository.deleteAll();
//    incomeRepository.deleteAll();
//    userRepository.deleteAll();
//    incomeCategoryRepository.deleteAll();
//  }
//
//  @Test
//  void createIncomeSuccessfully() {
//    ResponseEntity<IncomeResponseDto> response = createIncomeInDb();
//
//    assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    IncomeResponseDto incomeResponseDto = response.getBody();
//    assertNotNull(incomeResponseDto);
//    assertEquals(incomeResponseDto.getSum(), incomeResponseDto.getSum());
//  }
//
//  @Test
//  void getIncomeSuccessfully() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> incomeResponse = createIncomeInDb();
//    assertNotNull(incomeResponse.getBody());
//
//    // WHEN
//    ResponseEntity<IncomeResponseDto> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "?id=" + incomeResponse.getBody().getId(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            IncomeResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(incomeRequestDto.getCategory(), response.getBody().getIncomeCategory().getName());
//    assertEquals(incomeRequestDto.getType(), response.getBody().getType());
//  }
//
//  @Test
//  void testGetIncomeShouldThrowWhenIncomeNotFound() {
//    // WHEN
//    ResponseEntity<IncomeResponseDto> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "?id=" + UUID.randomUUID(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            IncomeResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//  }
//
//  @Test
//  void testGetAllIncomesOfUserSuccessfully() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> income1 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income2 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income3 = createIncomeInDb();
//
//    assertNotNull(income1.getBody());
//    assertNotNull(income2.getBody());
//    assertNotNull(income3.getBody());
//
//    // WHEN
//    ResponseEntity<String> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "/users?id=" + authResponse.getUser().getId(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            String.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//  }
//
//  @Test
//  void testEditIncomeSuccessfully() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> createdIncome = createIncomeInDb();
//    assertNotNull(createdIncome.getBody());
//
//    IncomeRequestDto editRequest = incomeRequestDto;
//    editRequest.setSum(BigDecimal.TWO);
//    editRequest.setRegularity(Regularity.DAILY);
//
//    // WHEN
//    ResponseEntity<IncomeResponseDto> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "?id=" + createdIncome.getBody().getId(),
//            HttpMethod.PUT,
//            new HttpEntity<>(editRequest, headers),
//            IncomeResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(editRequest.getRegularity(), response.getBody().getRegularity());
//  }
//
//  @Test
//  void testDeleteIncomeSuccessfully() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> createdIncome = createIncomeInDb();
//    assertNotNull(createdIncome.getBody());
//
//    // WHEN
//    ResponseEntity<IncomeResponseDto> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "?id=" + createdIncome.getBody().getId(),
//            HttpMethod.DELETE,
//            new HttpEntity<>(null, headers),
//            IncomeResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(
//        createdIncome.getBody().getIncomeCategory().getName(),
//        response.getBody().getIncomeCategory().getName());
//  }
//
//  @Test
//  void testDeleteIncomeShouldThrowWhenIncomeNotFound() {
//    // WHEN
//    ResponseEntity<IncomeResponseDto> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "?id=" + UUID.randomUUID(),
//            HttpMethod.DELETE,
//            new HttpEntity<>(null, headers),
//            IncomeResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//  }
//
//  @Test
//  void testGetSumOfAllIncomesOfUserByCategory() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> income1 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income2 = createIncomeInDb();
//    assertNotNull(income1.getBody());
//    assertNotNull(income2.getBody());
//
//    BigDecimal expectedSum = income1.getBody().getSum().add(income2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            INCOMES_URL
//                + "/users/category/sum?id="
//                + incomeRequestDto.getOwnerId()
//                + "&category="
//                + income1.getBody().getIncomeCategory().getName(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            BigDecimal.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(expectedSum.setScale(2, RoundingMode.HALF_UP), response.getBody());
//  }
//
//  @Test
//  void testGetSumOfAllIncomesOfUser() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> income1 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income2 = createIncomeInDb();
//    assertNotNull(income1.getBody());
//    assertNotNull(income2.getBody());
//
//    BigDecimal expectedSum = income1.getBody().getSum().add(income2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            INCOMES_URL + "/users/sum?id=" + incomeRequestDto.getOwnerId(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            BigDecimal.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(
//        expectedSum.setScale(2, RoundingMode.HALF_UP),
//        response.getBody().setScale(2, RoundingMode.HALF_UP));
//  }
//
//  @Test
//  void testGetSumOfAllUserIncomesByType() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> income1 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income2 = createIncomeInDb();
//    assertNotNull(income1.getBody());
//    assertNotNull(income2.getBody());
//
//    BigDecimal expectedSum = income1.getBody().getSum().add(income2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            INCOMES_URL
//                + "/users/type/sum?id="
//                + incomeRequestDto.getOwnerId()
//                + "&type="
//                + income1.getBody().getType(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            BigDecimal.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(
//        expectedSum.setScale(2, RoundingMode.HALF_UP),
//        response.getBody().setScale(2, RoundingMode.HALF_UP));
//  }
//
//  @Test
//  void testGetAllIncomesOfUserByType() {
//    // GIVEN
//    ResponseEntity<IncomeResponseDto> income2 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income3 = createIncomeInDb();
//    ResponseEntity<IncomeResponseDto> income1 = createIncomeInDb();
//
//    assertNotNull(income1.getBody());
//    assertNotNull(income2.getBody());
//    assertNotNull(income3.getBody());
//
//    // WHEN
//    ResponseEntity<String> response =
//        testRestTemplate.exchange(
//            INCOMES_URL
//                + "/users/type?id="
//                + authResponse.getUser().getId()
//                + "&type="
//                + income1.getBody().getType(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            String.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//  }
//
//  private IncomeRequestDto createIncomeRequest(UUID userId) {
//    IncomeRequestDto requestDto = new IncomeRequestDto();
//    requestDto.setType(Type.FIXED);
//    requestDto.setCategory("Salary");
//    requestDto.setCreationDate(LocalDate.now());
//    requestDto.setRegularity(Regularity.MONTHLY);
//    requestDto.setSum(BigDecimal.TEN);
//    requestDto.setOwnerId(userId);
//    requestDto.setSubcategory("");
//    return requestDto;
//  }
//
//  private static RegisterRequest createRegisterDto() {
//    RegisterRequest registerDto = new RegisterRequest();
//    registerDto.setName(TEST_NAME);
//    registerDto.setEmail(TEST_EMAIL);
//    registerDto.setPassword(TEST_PASSWORD);
//    registerDto.setConfirmPassword(TEST_PASSWORD);
//    return registerDto;
//  }
//
//  private static LoginRequest createLoginDto() {
//    LoginRequest loginDto = new LoginRequest();
//    loginDto.setEmail(TEST_EMAIL);
//    loginDto.setPassword(TEST_PASSWORD);
//    return loginDto;
//  }
//
//  private ResponseEntity<AuthResponse> createUserInDb(RegisterRequest request) {
//    return testRestTemplate.postForEntity(REGISTER_URL, request, AuthResponse.class);
//  }
//
//  private ResponseEntity<IncomeResponseDto> createIncomeInDb() {
//    return testRestTemplate.exchange(
//        INCOMES_URL,
//        HttpMethod.POST,
//        new HttpEntity<>(incomeRequestDto, headers),
//        IncomeResponseDto.class);
//  }
//
//  private CategoryRequestDto createCategoryRequestDto() {
//    CategoryRequestDto requestDto = new CategoryRequestDto();
//    requestDto.setName("Salary");
//    requestDto.setBgName("Работна заплата");
//    return requestDto;
//  }
//
//  private CategoryResponseDto createCategoryInDb() {
//    ResponseEntity<CategoryResponseDto> response =
//        testRestTemplate.exchange(
//            CATEGORY_URL,
//            HttpMethod.POST,
//            new HttpEntity<>(categoryRequestDto, headers),
//            CategoryResponseDto.class);
//    assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    assertNotNull(response.getBody());
//    return response.getBody();
//  }
}
