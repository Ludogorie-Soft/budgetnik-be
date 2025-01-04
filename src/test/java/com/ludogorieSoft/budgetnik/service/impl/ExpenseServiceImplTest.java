package com.ludogorieSoft.budgetnik.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
import com.ludogorieSoft.budgetnik.service.ExpenseCategoryService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {
  @Mock private UserService userService;

  @Mock private ModelMapper modelMapper;

  @Mock private ExpenseRepository expenseRepository;

  @Mock private ExpenseCategoryService expenseCategoryService;

  @InjectMocks private ExpenseServiceImpl expenseService;

  private ExpenseCategory expenseCategory;

  @BeforeEach
  void setup() {
    expenseCategory = getTestExpenseCategory();
  }

  @Test
  void createFixedExpense_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    ExpenseRequestDto requestDto = createFixedExpenseRequestDto(ownerId);

    User user = new User();
    user.setId(ownerId);

    createFixedExpense(user);

    ExpenseResponseDto responseDto = new ExpenseResponseDto();
    responseDto.setSum(BigDecimal.ONE);

    when(userService.findById(ownerId)).thenReturn(user);
    when(modelMapper.map(any(Expense.class), eq(ExpenseResponseDto.class))).thenReturn(responseDto);

    // WHEN
    ExpenseResponseDto result = expenseService.createExpense(requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(BigDecimal.ONE, result.getSum());

    verify(userService, times(1)).findById(ownerId);
    verify(expenseRepository, times(1)).save(any(Expense.class));
    verify(modelMapper, times(1)).map(any(Expense.class), eq(ExpenseResponseDto.class));
  }

  @Test
  void testCreateVariableExpense_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    ExpenseRequestDto requestDto = createVariableExpenseRequestDto(ownerId);

    User user = new User();
    user.setId(ownerId);

    createVariableExpense(user);

    ExpenseResponseDto responseDto = new ExpenseResponseDto();
    responseDto.setSum(BigDecimal.ONE);

    when(userService.findById(ownerId)).thenReturn(user);
    when(modelMapper.map(any(Expense.class), eq(ExpenseResponseDto.class))).thenReturn(responseDto);

    // WHEN
    ExpenseResponseDto result = expenseService.createExpense(requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(BigDecimal.ONE, result.getSum());

    verify(userService, times(1)).findById(ownerId);
    verify(expenseRepository, times(1)).save(any(Expense.class));
    verify(modelMapper, times(1)).map(any(Expense.class), eq(ExpenseResponseDto.class));
  }

  @Test
  void getExpense_Success() {
    // GIVEN
    User user = new User();
    Expense expense = createFixedExpense(user);

    when(expenseRepository.findById(expense.getId())).thenReturn(Optional.of(expense));

    // WHEN
    expenseService.getExpense(expense.getId());

    // THEN
    verify(expenseRepository, times(1)).findById(expense.getId());
  }

  @Test
  void getAllExpenses_Success() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);

    Expense expense1 = new Expense();
    expense1.setId(UUID.randomUUID());
    Expense expense2 = new Expense();
    expense2.setId(UUID.randomUUID());

    List<Expense> expenses = List.of(expense1, expense2);

    ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto();
    expenseResponseDto1.setId(expense1.getId());
    ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto();
    expenseResponseDto2.setId(expense2.getId());

    when(userService.findById(userId)).thenReturn(user);
    when(expenseRepository.findAllByOwner(user)).thenReturn(expenses);
    when(modelMapper.map(expense1, ExpenseResponseDto.class)).thenReturn(expenseResponseDto1);
    when(modelMapper.map(expense2, ExpenseResponseDto.class)).thenReturn(expenseResponseDto2);

    // WHEN
    List<ExpenseResponseDto> result = expenseService.getAllExpensesOfUser(userId);

    // THEN
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expenseResponseDto1.getId(), result.get(0).getId());
    assertEquals(expenseResponseDto2.getId(), result.get(1).getId());

    verify(userService, times(1)).findById(userId);
    verify(expenseRepository, times(1)).findAllByOwner(user);
    verify(modelMapper, times(1)).map(expense1, ExpenseResponseDto.class);
    verify(modelMapper, times(1)).map(expense2, ExpenseResponseDto.class);
  }

  @Test
  void testDeleteExpense_Success() {
    // GIVEN
    UUID expenseId = UUID.randomUUID();

    Expense expense = new Expense();
    expense.setId(expenseId);

    ExpenseResponseDto expenseResponseDto = new ExpenseResponseDto();
    expenseResponseDto.setId(expenseId);

    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
    when(modelMapper.map(expense, ExpenseResponseDto.class)).thenReturn(expenseResponseDto);

    // WHEN
    ExpenseResponseDto result = expenseService.deleteExpense(expenseId);

    // THEN
    assertNotNull(result);
    assertEquals(expenseId, result.getId());

    verify(expenseRepository, times(1)).findById(expenseId);
    verify(modelMapper, times(1)).map(expense, ExpenseResponseDto.class);
    verify(expenseRepository, times(1)).delete(expense);
  }

  @Test
  void testCalculateSumOfAllUserExpenses() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(expenseRepository.calculateTotalSumByUserId(userId)).thenReturn(expectedSum);

    // WHEN
    BigDecimal result = expenseService.calculateSumOfAllExpensesOfUser(userId);

    // THEN
    assertNotNull(result);
    assertEquals(expectedSum, result);

    verify(expenseRepository, times(1)).calculateTotalSumByUserId(userId);
  }

  @Test
  void calculateSumOnUserExpensesByCategory() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(expenseCategoryService.getCategory("Phone")).thenReturn(expenseCategory);
    when(expenseRepository.calculateTotalSumByUserIdAndCategory(userId, expenseCategory))
        .thenReturn(expectedSum);

    // WHEN
    BigDecimal result =
        expenseService.calculateSumOfAllExpensesOfUserByCategory(userId, expenseCategory.getName());

    // THEN
    assertNotNull(result);
    assertEquals(expectedSum, result);

    verify(expenseRepository, times(1))
        .calculateTotalSumByUserIdAndCategory(userId, expenseCategory);
  }

  @Test
  void calculateSumOfUserExpensesByType() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(expenseRepository.calculateSumOfUserExpensesByType(userId, Type.FIXED))
        .thenReturn(expectedSum);
    when(expenseRepository.calculateSumOfUserExpensesByType(userId, Type.VARIABLE))
        .thenReturn(expectedSum);

    // WHEN
    BigDecimal actual = expenseService.calculateSumOfUserExpensesByType(userId, Type.FIXED);
    BigDecimal actual2 = expenseService.calculateSumOfUserExpensesByType(userId, Type.VARIABLE);

    // THEN
    assertNotNull(actual);
    assertNotNull(actual2);
    assertEquals(expectedSum, actual);
    assertEquals(expectedSum, actual2);

    verify(expenseRepository, times(1)).calculateSumOfUserExpensesByType(userId, Type.FIXED);
    verify(expenseRepository, times(1)).calculateSumOfUserExpensesByType(userId, Type.VARIABLE);
  }

  @Test
  void testFindAllUserExpensesByType() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Type type = Type.FIXED;

    User user = new User();
    user.setId(userId);

    Expense expense1 = new Expense();
    expense1.setId(UUID.randomUUID());
    expense1.setType(type);

    Expense expense2 = new Expense();
    expense2.setId(UUID.randomUUID());
    expense2.setType(type);

    List<Expense> expenses = List.of(expense1, expense2);

    ExpenseResponseDto expenseResponseDto1 = new ExpenseResponseDto();
    expenseResponseDto1.setId(expense1.getId());

    ExpenseResponseDto expenseResponseDto2 = new ExpenseResponseDto();
    expenseResponseDto2.setId(expense2.getId());

    when(userService.findById(userId)).thenReturn(user);
    when(expenseRepository.findAllByOwnerAndType(user, type)).thenReturn(expenses);
    when(modelMapper.map(expense1, ExpenseResponseDto.class)).thenReturn(expenseResponseDto1);
    when(modelMapper.map(expense2, ExpenseResponseDto.class)).thenReturn(expenseResponseDto2);

    // WHEN
    List<ExpenseResponseDto> result = expenseService.getAllExpensesOfUserByType(userId, type);

    // THEN
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expenseResponseDto1.getId(), result.get(0).getId());
    assertEquals(expenseResponseDto2.getId(), result.get(1).getId());

    verify(userService, times(1)).findById(userId);
    verify(expenseRepository, times(1)).findAllByOwnerAndType(user, type);
    verify(modelMapper, times(1)).map(expense1, ExpenseResponseDto.class);
    verify(modelMapper, times(1)).map(expense2, ExpenseResponseDto.class);
  }

  @Test
  void testEditFixedIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    UUID expenseId = UUID.randomUUID();

    Expense expense = new Expense();
    expense.setId(expenseId);

    ExpenseRequestDto requestDto = createFixedExpenseRequestDto(ownerId);

    ExpenseResponseDto responseDto = new ExpenseResponseDto();
    responseDto.setId(expenseId);
    responseDto.setSum(requestDto.getSum());

    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
    when(modelMapper.map(expense, ExpenseResponseDto.class)).thenReturn(responseDto);

    // WHEN
    ExpenseResponseDto result = expenseService.editExpense(expenseId, requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(requestDto.getSum(), result.getSum());
    assertEquals(expenseId, result.getId());

    verify(expenseRepository, times(1)).findById(expenseId);
    verify(expenseRepository, times(1)).save(expense);
    verify(modelMapper, times(1)).map(expense, ExpenseResponseDto.class);

    assertEquals(requestDto.getSum(), expense.getSum());
    assertEquals(Type.FIXED, expense.getType());
    assertEquals(requestDto.getRegularity(), expense.getRegularity());
  }

  @Test
  void testEditVariableIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    UUID expenseId = UUID.randomUUID();

    Expense expense = new Expense();
    expense.setId(expenseId);

    ExpenseRequestDto requestDto = createVariableExpenseRequestDto(ownerId);

    ExpenseResponseDto responseDto = new ExpenseResponseDto();
    responseDto.setId(expenseId);
    responseDto.setSum(requestDto.getSum());

    when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
    when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
    when(modelMapper.map(expense, ExpenseResponseDto.class)).thenReturn(responseDto);

    // WHEN
    ExpenseResponseDto result = expenseService.editExpense(expenseId, requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(requestDto.getSum(), result.getSum());
    assertEquals(expenseId, result.getId());

    verify(expenseRepository, times(1)).findById(expenseId);
    verify(expenseRepository, times(1)).save(expense);
    verify(modelMapper, times(1)).map(expense, ExpenseResponseDto.class);

    assertEquals(requestDto.getSum(), expense.getSum());
    assertEquals(Type.VARIABLE, expense.getType());
    assertEquals(requestDto.getOneTimeExpense(), expense.getOneTimeExpense());
  }

  private static ExpenseCategory getTestExpenseCategory() {
    ExpenseCategory expenseCategory = new ExpenseCategory();
    expenseCategory.setId(UUID.randomUUID());
    expenseCategory.setName("Phone");
    expenseCategory.setBgName("Телефон");
    return expenseCategory;
  }

  private Expense createFixedExpense(User user) {
    Expense income = new Expense();
    income.setOwner(user);
    income.setType(Type.FIXED);
    income.setRegularity(Regularity.MONTHLY);
    income.setCategory(expenseCategory);
    income.setDate(LocalDate.now());
    income.setSum(BigDecimal.ONE);
    return income;
  }

  private static ExpenseRequestDto createFixedExpenseRequestDto(UUID ownerId) {
    ExpenseRequestDto requestDto = new ExpenseRequestDto();
    requestDto.setOwnerId(ownerId);
    requestDto.setType(Type.FIXED);
    requestDto.setRegularity(Regularity.MONTHLY);
    requestDto.setCategory("Salary");
    requestDto.setDate(LocalDate.now());
    requestDto.setSum(BigDecimal.ONE);
    return requestDto;
  }

  private Expense createVariableExpense(User user) {
    Expense expense = new Expense();
    expense.setOwner(user);
    expense.setType(Type.VARIABLE);
    expense.setOneTimeExpense("Something");
    expense.setCategory(expenseCategory);
    expense.setDate(LocalDate.now());
    expense.setSum(BigDecimal.ONE);
    return expense;
  }

  private static ExpenseRequestDto createVariableExpenseRequestDto(UUID ownerId) {
    ExpenseRequestDto requestDto = new ExpenseRequestDto();
    requestDto.setOwnerId(ownerId);
    requestDto.setType(Type.VARIABLE);
    requestDto.setOneTimeExpense("Something");
    requestDto.setCategory("Extra");
    requestDto.setDate(LocalDate.now());
    requestDto.setSum(BigDecimal.ONE);
    return requestDto;
  }
}
