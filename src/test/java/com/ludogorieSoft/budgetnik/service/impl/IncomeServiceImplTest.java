package com.ludogorieSoft.budgetnik.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
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
class IncomeServiceImplTest {

  @Mock private UserService userService;

  @Mock private ModelMapper modelMapper;

  @Mock private IncomeRepository incomeRepository;

  @Mock private IncomeCategoryService incomeCategoryService;

  @InjectMocks private IncomeServiceImpl incomeService;

  private IncomeCategory incomeCategory;

  @BeforeEach
  void setup() {
      incomeCategory = getTestIncomeCategory();
  }

  @Test
  void createFixedIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    IncomeRequestDto requestDto = createFixedIncomeRequestDto(ownerId);

    User user = new User();
    user.setId(ownerId);

    createFixedIncome(user);

    IncomeResponseDto responseDto = new IncomeResponseDto();
    responseDto.setSum(BigDecimal.ONE);

    when(userService.findById(ownerId)).thenReturn(user);
    when(modelMapper.map(any(Income.class), eq(IncomeResponseDto.class))).thenReturn(responseDto);

    // WHEN
    IncomeResponseDto result = incomeService.createIncome(requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(BigDecimal.ONE, result.getSum());

    verify(userService, times(1)).findById(ownerId);
    verify(incomeRepository, times(1)).save(any(Income.class));
    verify(modelMapper, times(1)).map(any(Income.class), eq(IncomeResponseDto.class));
  }

  @Test
  void testCreateVariableIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    IncomeRequestDto requestDto = createVariableIncomeRequestDto(ownerId);

    User user = new User();
    user.setId(ownerId);

    createVariableIncome(user);

    IncomeResponseDto responseDto = new IncomeResponseDto();
    responseDto.setSum(BigDecimal.ONE);

    when(userService.findById(ownerId)).thenReturn(user);
    when(modelMapper.map(any(Income.class), eq(IncomeResponseDto.class))).thenReturn(responseDto);

    // WHEN
    IncomeResponseDto result = incomeService.createIncome(requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(BigDecimal.ONE, result.getSum());

    verify(userService, times(1)).findById(ownerId);
    verify(incomeRepository, times(1)).save(any(Income.class));
    verify(modelMapper, times(1)).map(any(Income.class), eq(IncomeResponseDto.class));
  }

  @Test
  void getIncome_Success() {
    // GIVEN
    User user = new User();
    Income income = createFixedIncome(user);

    when(incomeRepository.findById(income.getId())).thenReturn(Optional.of(income));

    // WHEN
    incomeService.getIncome(income.getId());

    // THEN
    verify(incomeRepository, times(1)).findById(income.getId());
  }

  @Test
  void getAllIncomes_Success() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    User user = new User();
    user.setId(userId);

    Income income1 = new Income();
    income1.setId(UUID.randomUUID());
    Income income2 = new Income();
    income2.setId(UUID.randomUUID());

    List<Income> incomes = List.of(income1, income2);

    IncomeResponseDto incomeDto1 = new IncomeResponseDto();
    incomeDto1.setId(income1.getId());
    IncomeResponseDto incomeDto2 = new IncomeResponseDto();
    incomeDto2.setId(income2.getId());

    when(userService.findById(userId)).thenReturn(user);
    when(incomeRepository.findAllByOwner(user)).thenReturn(incomes);
    when(modelMapper.map(income1, IncomeResponseDto.class)).thenReturn(incomeDto1);
    when(modelMapper.map(income2, IncomeResponseDto.class)).thenReturn(incomeDto2);

    // WHEN
    List<IncomeResponseDto> result = incomeService.getAllIncomesOfUser(userId);

    // THEN
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(incomeDto1.getId(), result.get(0).getId());
    assertEquals(incomeDto2.getId(), result.get(1).getId());

    verify(userService, times(1)).findById(userId);
    verify(incomeRepository, times(1)).findAllByOwner(user);
    verify(modelMapper, times(1)).map(income1, IncomeResponseDto.class);
    verify(modelMapper, times(1)).map(income2, IncomeResponseDto.class);
  }

  @Test
  void testDeleteIncome_Success() {
    // GIVEN
    UUID incomeId = UUID.randomUUID();

    Income income = new Income();
    income.setId(incomeId);

    IncomeResponseDto incomeResponseDto = new IncomeResponseDto();
    incomeResponseDto.setId(incomeId);

    when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(income));
    when(modelMapper.map(income, IncomeResponseDto.class)).thenReturn(incomeResponseDto);

    // WHEN
    IncomeResponseDto result = incomeService.deleteIncome(incomeId);

    // THEN
    assertNotNull(result);
    assertEquals(incomeId, result.getId());

    verify(incomeRepository, times(1)).findById(incomeId);
    verify(modelMapper, times(1)).map(income, IncomeResponseDto.class);
    verify(incomeRepository, times(1)).delete(income);
  }

  @Test
  void testCalculateSumOfAllUserIncomes() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(incomeRepository.calculateTotalSumByUserId(userId)).thenReturn(expectedSum);

    // WHEN
    BigDecimal result = incomeService.calculateSumOfAllIncomesOfUser(userId);

    // THEN
    assertNotNull(result);
    assertEquals(expectedSum, result);

    verify(incomeRepository, times(1)).calculateTotalSumByUserId(userId);
  }

  @Test
  void calculateSumOnUserIncomesByCategory() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(incomeCategoryService.getCategory("Salary")).thenReturn(incomeCategory);
    when(incomeRepository.calculateTotalSumByUserIdAndCategory(userId, incomeCategory))
        .thenReturn(expectedSum);

    // WHEN
    BigDecimal result = incomeService.calculateSumOfAllIncomesOfUserByCategory(userId, "Salary");

    // THEN
    assertNotNull(result);
    assertEquals(expectedSum, result);

    verify(incomeRepository, times(1)).calculateTotalSumByUserIdAndCategory(userId, incomeCategory);
  }

  @Test
  void calculateSumOfUserIncomesByType() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    BigDecimal expectedSum = new BigDecimal("12345.67");

    when(incomeRepository.calculateSumOfUserIncomesByType(userId, Type.FIXED))
        .thenReturn(expectedSum);
    when(incomeRepository.calculateSumOfUserIncomesByType(userId, Type.VARIABLE))
        .thenReturn(expectedSum);

    // WHEN
    BigDecimal actual = incomeService.calculateSumOfUserIncomesByType(userId, Type.FIXED);
    BigDecimal actual2 = incomeService.calculateSumOfUserIncomesByType(userId, Type.VARIABLE);

    // THEN
    assertNotNull(actual);
    assertNotNull(actual2);
    assertEquals(expectedSum, actual);
    assertEquals(expectedSum, actual2);

    verify(incomeRepository, times(1)).calculateSumOfUserIncomesByType(userId, Type.FIXED);
    verify(incomeRepository, times(1)).calculateSumOfUserIncomesByType(userId, Type.VARIABLE);
  }

  @Test
  void testFindAllUserIncomesByType() {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Type type = Type.FIXED;

    User user = new User();
    user.setId(userId);

    Income income1 = new Income();
    income1.setId(UUID.randomUUID());
    income1.setType(type);

    Income income2 = new Income();
    income2.setId(UUID.randomUUID());
    income2.setType(type);

    List<Income> incomes = List.of(income1, income2);

    IncomeResponseDto incomeDto1 = new IncomeResponseDto();
    incomeDto1.setId(income1.getId());

    IncomeResponseDto incomeDto2 = new IncomeResponseDto();
    incomeDto2.setId(income2.getId());

    when(userService.findById(userId)).thenReturn(user);
    when(incomeRepository.findAllByOwnerAndType(user, type)).thenReturn(incomes);
    when(modelMapper.map(income1, IncomeResponseDto.class)).thenReturn(incomeDto1);
    when(modelMapper.map(income2, IncomeResponseDto.class)).thenReturn(incomeDto2);

    // WHEN
    List<IncomeResponseDto> result = incomeService.findAllByUserAndType(userId, type);

    // THEN
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(incomeDto1.getId(), result.get(0).getId());
    assertEquals(incomeDto2.getId(), result.get(1).getId());

    verify(userService, times(1)).findById(userId);
    verify(incomeRepository, times(1)).findAllByOwnerAndType(user, type);
    verify(modelMapper, times(1)).map(income1, IncomeResponseDto.class);
    verify(modelMapper, times(1)).map(income2, IncomeResponseDto.class);
  }

  @Test
  void testEditFixedIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    UUID incomeId = UUID.randomUUID();

    Income income = new Income();
    income.setId(incomeId);

    IncomeRequestDto requestDto = createFixedIncomeRequestDto(ownerId);

    IncomeResponseDto responseDto = new IncomeResponseDto();
    responseDto.setId(incomeId);
    responseDto.setSum(requestDto.getSum());

    when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(income));
    when(incomeRepository.save(any(Income.class))).thenReturn(income);
    when(modelMapper.map(income, IncomeResponseDto.class)).thenReturn(responseDto);

    // WHEN
    IncomeResponseDto result = incomeService.editIncome(incomeId, requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(requestDto.getSum(), result.getSum());
    assertEquals(incomeId, result.getId());

    verify(incomeRepository, times(1)).findById(incomeId);
    verify(incomeRepository, times(1)).save(income);
    verify(modelMapper, times(1)).map(income, IncomeResponseDto.class);

    assertEquals(requestDto.getSum(), income.getSum());
    assertEquals(Type.FIXED, income.getType());
    assertEquals(requestDto.getRegularity(), income.getRegularity());
  }

  @Test
  void testEditVariableIncome_Success() {
    // GIVEN
    UUID ownerId = UUID.randomUUID();
    UUID incomeId = UUID.randomUUID();

    Income income = new Income();
    income.setId(incomeId);

    IncomeRequestDto requestDto = createVariableIncomeRequestDto(ownerId);

    IncomeResponseDto responseDto = new IncomeResponseDto();
    responseDto.setId(incomeId);
    responseDto.setSum(requestDto.getSum());

    when(incomeRepository.findById(incomeId)).thenReturn(Optional.of(income));
    when(incomeRepository.save(any(Income.class))).thenReturn(income);
    when(modelMapper.map(income, IncomeResponseDto.class)).thenReturn(responseDto);

    // WHEN
    IncomeResponseDto result = incomeService.editIncome(incomeId, requestDto);

    // THEN
    assertNotNull(result);
    assertEquals(requestDto.getSum(), result.getSum());
    assertEquals(incomeId, result.getId());

    verify(incomeRepository, times(1)).findById(incomeId);
    verify(incomeRepository, times(1)).save(income);
    verify(modelMapper, times(1)).map(income, IncomeResponseDto.class);

    assertEquals(requestDto.getSum(), income.getSum());
    assertEquals(Type.VARIABLE, income.getType());
    assertEquals(requestDto.getOneTimeIncome(), income.getOneTimeIncome());
  }

    private static IncomeCategory getTestIncomeCategory() {
        IncomeCategory incomeCategory = new IncomeCategory();
        incomeCategory.setId(UUID.randomUUID());
        incomeCategory.setName("Salary");
        incomeCategory.setBgName("Заплата");
        return incomeCategory;
    }

  private Income createFixedIncome(User user) {
    Income income = new Income();
    income.setOwner(user);
    income.setType(Type.FIXED);
    income.setRegularity(Regularity.MONTHLY);
    income.setCategory(incomeCategory);
    income.setCreationDate(LocalDate.now());
    income.setSum(BigDecimal.ONE);
    return income;
  }

  private static IncomeRequestDto createFixedIncomeRequestDto(UUID ownerId) {
    IncomeRequestDto requestDto = new IncomeRequestDto();
    requestDto.setOwnerId(ownerId);
    requestDto.setType(Type.FIXED);
    requestDto.setRegularity(Regularity.MONTHLY);
    requestDto.setCategory("Salary");
    requestDto.setCreationDate(LocalDate.now());
    requestDto.setSum(BigDecimal.ONE);
    return requestDto;
  }

  private Income createVariableIncome(User user) {
    Income income = new Income();
    income.setOwner(user);
    income.setType(Type.VARIABLE);
    income.setOneTimeIncome("Bonus");
    income.setCategory(incomeCategory);
    income.setCreationDate(LocalDate.now());
    income.setSum(BigDecimal.ONE);
    return income;
  }

  private static IncomeRequestDto createVariableIncomeRequestDto(UUID ownerId) {
    IncomeRequestDto requestDto = new IncomeRequestDto();
    requestDto.setOwnerId(ownerId);
    requestDto.setType(Type.VARIABLE);
    requestDto.setOneTimeIncome("Bonus");
    requestDto.setCategory("Extra");
    requestDto.setCreationDate(LocalDate.now());
    requestDto.setSum(BigDecimal.ONE);
    return requestDto;
  }
}
