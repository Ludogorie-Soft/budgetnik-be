package com.ludogorieSoft.budgetnik.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.LoginRequest;
import com.ludogorieSoft.budgetnik.dto.request.RegisterRequest;
import com.ludogorieSoft.budgetnik.dto.response.AuthResponse;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.ExpenseCategoryRepository;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
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
class ExpensesControllerIntegrationTest {

  private static final String TEST_NAME = "Test Name";
  private static final String TEST_EMAIL = "test@email.com";
  private static final String TEST_PASSWORD = "password";

  private static final String REGISTER_URL = "/api/auth/register";
  private static final String LOGIN_URL = "/api/auth/login";
  private static final String EXPENSE_URL = "/api/expenses";
  private static final String CATEGORY_URL = "/api/categories/expenses";

  @Autowired private UserRepository userRepository;
  @Autowired private ExpenseRepository expenseRepository;
  @Autowired private TestRestTemplate testRestTemplate;
  @Autowired private VerificationTokenRepository verificationTokenRepository;
  @Autowired private TokenRepository tokenRepository;
  @Autowired private ExpenseCategoryRepository expenseCategoryRepository;
  @Autowired private MessageSource messageSource;

  private HttpHeaders headers;
  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private AuthResponse registerResponse;
  private AuthResponse authResponse;
  private ExpenseRequestDto expenseRequestDto;
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
//    expenseRequestDto = createExpenseRequest(authResponse.getUser().getId());
//  }
//
//  @AfterEach
//  void cleanUp() {
//    tokenRepository.deleteAll();
//    verificationTokenRepository.deleteAll();
//    expenseRepository.deleteAll();
//    userRepository.deleteAll();
//    expenseCategoryRepository.deleteAll();
//  }
//
//  @Test
//  void createExpenseSuccessfully() {
//    ResponseEntity<ExpenseResponseDto> response = createExpenseInDb();
//
//    assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    ExpenseResponseDto expenseResponseDto = response.getBody();
//    assertNotNull(expenseResponseDto);
//    assertEquals(expenseRequestDto.getSum(), expenseResponseDto.getSum());
//  }
//
//  @Test
//  void getExpenseSuccessfully() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> expenseResponse = createExpenseInDb();
//    assertNotNull(expenseResponse.getBody());
//
//    // WHEN
//    ResponseEntity<ExpenseResponseDto> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "?id=" + expenseResponse.getBody().getId(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            ExpenseResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(expenseRequestDto.getType(), response.getBody().getType());
//  }
//
//  @Test
//  void testGetExpenseShouldThrowWhenExpenseNotFound() {
//    // WHEN
//    ResponseEntity<ExpenseResponseDto> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "?id=" + UUID.randomUUID(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            ExpenseResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//  }
//
//  @Test
//  void testGetAllExpensesOfUserSuccessfully() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> expense1 = createExpenseInDb();
//    ResponseEntity<ExpenseResponseDto> expense2 = createExpenseInDb();
//    ResponseEntity<ExpenseResponseDto> expense3 = createExpenseInDb();
//
//    assertNotNull(expense1.getBody());
//    assertNotNull(expense2.getBody());
//    assertNotNull(expense3.getBody());
//
//    // WHEN
//    ResponseEntity<String> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "/users?id=" + authResponse.getUser().getId(),
//            HttpMethod.GET,
//            new HttpEntity<>(null, headers),
//            String.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//  }
//
//  @Test
//  void testEditExpenseSuccessfully() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> createdExpense = createExpenseInDb();
//    assertNotNull(createdExpense.getBody());
//
//    ExpenseRequestDto editRequest = expenseRequestDto;
//    editRequest.setSum(BigDecimal.TWO);
//    editRequest.setRegularity(Regularity.DAILY);
//
//    // WHEN
//    ResponseEntity<ExpenseResponseDto> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "?id=" + createdExpense.getBody().getId(),
//            HttpMethod.PUT,
//            new HttpEntity<>(editRequest, headers),
//            ExpenseResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//    assertEquals(editRequest.getRegularity(), response.getBody().getRegularity());
//  }
//
//  @Test
//  void testDeleteExpenseSuccessfully() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> createdExpense = createExpenseInDb();
//    assertNotNull(createdExpense.getBody());
//
//    // WHEN
//    ResponseEntity<ExpenseResponseDto> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "?id=" + createdExpense.getBody().getId(),
//            HttpMethod.DELETE,
//            new HttpEntity<>(null, headers),
//            ExpenseResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.OK, response.getStatusCode());
//    assertNotNull(response.getBody());
//  }
//
//  @Test
//  void testDeleteExpenseShouldThrowWhenExpenseNotFound() {
//    // WHEN
//    ResponseEntity<ExpenseResponseDto> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "?id=" + UUID.randomUUID(),
//            HttpMethod.DELETE,
//            new HttpEntity<>(null, headers),
//            ExpenseResponseDto.class);
//
//    // THEN
//    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//  }
//
//  @Test
//  void testGetSumOfAllExpensesOfUserByCategory() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> expense1 = createExpenseInDb();
//    ResponseEntity<ExpenseResponseDto> expense2 = createExpenseInDb();
//    assertNotNull(expense1.getBody());
//    assertNotNull(expense2.getBody());
//
//    BigDecimal expectedSum = expense1.getBody().getSum().add(expense2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL
//                + "/users/category/sum?id="
//                + expenseRequestDto.getOwnerId()
//                + "&category="
//                + expense1.getBody().getExpenseCategory().getName(),
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
//  void testGetSumOfAllExpensesOfUser() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> expense1 = createExpenseInDb();
//    ResponseEntity<ExpenseResponseDto> expense2 = createExpenseInDb();
//    assertNotNull(expense1.getBody());
//    assertNotNull(expense2.getBody());
//
//    BigDecimal expectedSum = expense1.getBody().getSum().add(expense2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL + "/users/sum?id=" + expenseRequestDto.getOwnerId(),
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
//  void testGetSumOfAllUserExpensesByType() {
//    // GIVEN
//    ResponseEntity<ExpenseResponseDto> expense1 = createExpenseInDb();
//    ResponseEntity<ExpenseResponseDto> expense2 = createExpenseInDb();
//    assertNotNull(expense1.getBody());
//    assertNotNull(expense2.getBody());
//
//    BigDecimal expectedSum = expense1.getBody().getSum().add(expense2.getBody().getSum());
//
//    // WHEN
//    ResponseEntity<BigDecimal> response =
//        testRestTemplate.exchange(
//            EXPENSE_URL
//                + "/users/type/sum?id="
//                + expenseRequestDto.getOwnerId()
//                + "&type="
//                + expense1.getBody().getType(),
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
//  private ExpenseRequestDto createExpenseRequest(UUID userId) {
//    ExpenseRequestDto requestDto = new ExpenseRequestDto();
//    requestDto.setType(Type.FIXED);
//    requestDto.setCategory("internet");
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
//  private ResponseEntity<ExpenseResponseDto> createExpenseInDb() {
//    return testRestTemplate.exchange(
//        EXPENSE_URL,
//        HttpMethod.POST,
//        new HttpEntity<>(expenseRequestDto, headers),
//        ExpenseResponseDto.class);
//  }
//
//  private CategoryRequestDto createCategoryRequestDto() {
//    CategoryRequestDto requestDto = new CategoryRequestDto();
//    requestDto.setName("internet");
//    requestDto.setBgName("Интернет");
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
