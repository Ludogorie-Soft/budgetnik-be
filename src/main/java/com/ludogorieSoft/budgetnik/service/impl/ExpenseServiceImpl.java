package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.exception.ExpenseNotFoundException;
import com.ludogorieSoft.budgetnik.exception.SubcategoryNotFoundException;
import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.Subcategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
import com.ludogorieSoft.budgetnik.repository.SubcategoryRepository;
import com.ludogorieSoft.budgetnik.service.ExpenseCategoryService;
import com.ludogorieSoft.budgetnik.service.ExpenseService;
import com.ludogorieSoft.budgetnik.service.UserService;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

  private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);

  private final ExpenseRepository expenseRepository;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final ExpenseCategoryService expenseCategoryService;
  private final SubcategoryRepository subcategoryRepository;

  @Override
  @Transactional
  public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
    Expense expense = new Expense();

    User user = userService.findById(expenseRequestDto.getOwnerId());
    expense.setOwner(user);
    expense.setCreationDate(expenseRequestDto.getCreationDate());
    expense.setType(expenseRequestDto.getType());
    expense.setRegularity(expenseRequestDto.getRegularity());
    expense.setDescription(expenseRequestDto.getDescription());
    expense.setSum(expenseRequestDto.getSum());

    setExpenseCategory(expenseRequestDto, expense);
    setSubcategory(expenseRequestDto, expense);
    setExpenseDueDate(expenseRequestDto, expense);

    Expense createdExpense = expenseRepository.save(expense);

    if (expenseRequestDto.getRelatedExpenseId() != null) {
      Expense relatedExpense = findById(expenseRequestDto.getRelatedExpenseId());
      relatedExpense.getRelatedExpenses().add(createdExpense);
      setExpenseDueDate(expenseRequestDto, relatedExpense);
      createdExpense.setRelatedExpense(relatedExpense);
      expenseRepository.save(relatedExpense);
    }

    logger.info("Created expense with id " + createdExpense.getId());
    return modelMapper.map(createdExpense, ExpenseResponseDto.class);
  }

  @Override
  public ExpenseResponseDto getExpense(UUID id) {
    Expense expense = findById(id);
    return modelMapper.map(expense, ExpenseResponseDto.class);
  }

  @Override
  public List<ExpenseResponseDto> getAllExpensesOfUser(UUID userId) {
    User user = userService.findById(userId);
    return expenseRepository.findAllByOwner(user).stream()
        .map(expense -> modelMapper.map(expense, ExpenseResponseDto.class))
        .toList();
  }

  @Override
  @Transactional
  public ExpenseResponseDto deleteExpense(UUID id) {
    Expense expense = findById(id);

    if (expense.getRelatedExpenses() != null) {
      for (Expense related : expense.getRelatedExpenses()) {
        related.setRelatedExpense(null);
        expenseRepository.save(related);
      }
    }

    if (expense.getRelatedExpense() != null) {
      Expense relatedExpense = findById(expense.getRelatedExpense().getId());

      List<Expense> relatedExpenses = relatedExpense.getRelatedExpenses();

      relatedExpenses.removeIf(current -> current.getId() == expense.getId());

      relatedExpense.setRelatedExpenses(relatedExpenses);
      expenseRepository.save(relatedExpense);
    }

    ExpenseResponseDto response = modelMapper.map(expense, ExpenseResponseDto.class);
    expenseRepository.delete(expense);
    logger.info("Deleted expense with id " + response.getId());
    return response;
  }

  @Override
  public BigDecimal calculateSumOfAllExpensesOfUser(UUID userId) {
    return expenseRepository.calculateTotalSumByUserId(userId);
  }

  @Override
  public BigDecimal calculateSumOfAllExpensesOfUserByCategory(UUID userId, String category) {
    ExpenseCategory expenseCategory = expenseCategoryService.getCategory(category);
    return expenseRepository.calculateTotalSumByUserIdAndCategory(userId, expenseCategory);
  }

  @Override
  public ExpenseResponseDto editExpense(UUID expenseId, ExpenseRequestDto expenseRequestDto) {
    Expense expense = findById(expenseId);

    expense.setSum(expenseRequestDto.getSum());
    expense.setType(expenseRequestDto.getType());
    expense.setRegularity(expenseRequestDto.getRegularity());
    expense.setDescription(expenseRequestDto.getDescription());
    expense.setCreationDate(expenseRequestDto.getCreationDate());

    setExpenseCategory(expenseRequestDto, expense);
    setSubcategory(expenseRequestDto, expense);
    setExpenseDueDate(expenseRequestDto, expense);

    expenseRepository.save(expense);
    logger.info("Edited expense with id " + expense.getId());
    return modelMapper.map(expense, ExpenseResponseDto.class);
  }

  @Override
  public BigDecimal calculateSumOfUserExpensesByType(UUID userId, Type type) {
    return expenseRepository.calculateSumOfUserExpensesByType(userId, type);
  }

  @Override
  public List<ExpenseResponseDto> getAllExpensesOfUserByType(UUID userId, Type type) {
    User user = userService.findById(userId);
    return expenseRepository.findAllByOwnerAndType(user, type).stream()
        .map(expense -> modelMapper.map(expense, ExpenseResponseDto.class))
        .toList();
  }

  @Override
  public List<ExpenseResponseDto> getAllExpensesOfUserForPeriod(
      UUID userId, LocalDate firstDate, LocalDate lastDate) {
    User user = userService.findById(userId);
    return expenseRepository.findExpensesForPeriod(user, firstDate, lastDate).stream()
        .map(expense -> modelMapper.map(expense, ExpenseResponseDto.class))
        .toList();
  }

  @Override
  public BigDecimal getSumOfAllExpensesOfUserForPeriodByCategory(
      UUID id, String category, LocalDate firstDate, LocalDate lastDate) {
    User user = userService.findById(id);
    ExpenseCategory expenseCategory = expenseCategoryService.getCategory(category);
    return expenseRepository.calculateSumOfExpensesByCategory(
        user, expenseCategory, firstDate, lastDate);
  }

  @Override
  public BigDecimal calculateSumOfUserExpensesByTypeAndPeriod(
      UUID userId, Type type, LocalDate startDate, LocalDate endDate) {
    return expenseRepository.calculateSumOfUserExpensesByTypeAndPeriod(
        userId, type, startDate, endDate);
  }

  @Override
  public List<ExpenseResponseDto> findAllFixedExpensesBeforeThanEqualDueDate(
      LocalDate date, Type type, UUID userId) {
    return expenseRepository
        .findByDueDateLessThanEqualAndTypeAndOwnerId(date, type, userId)
        .stream()
        .map(expense -> modelMapper.map(expense, ExpenseResponseDto.class))
        .toList();
  }

  @Override
  public List<ExpenseResponseDto> findRelatedExpenses(UUID expenseId, UUID userId) {
    return expenseRepository.findByRelatedExpenseIdAndOwnerId(expenseId, userId).stream()
        .map(expense -> modelMapper.map(expense, ExpenseResponseDto.class))
        .toList();
  }

  private Expense findById(UUID id) {
    return expenseRepository.findById(id).orElseThrow(ExpenseNotFoundException::new);
  }

  private void setExpenseDueDate(ExpenseRequestDto expenseRequestDto, Expense expense) {

    LocalDate today = expenseRequestDto.getCreationDate();
    LocalDate tomorrow = today.plusDays(1);
    LocalDate oneWeekLater = today.plusWeeks(1);
    LocalDate oneMonthLater = today.plusMonths(1);
    LocalDate threeMonthsLater = today.plusMonths(3);
    LocalDate sixMonthsLater = today.plusMonths(6);
    LocalDate oneYearLater = today.plusYears(1);

    if (expenseRequestDto.getRegularity().equals(Regularity.DAILY)) {
      expense.setDueDate(tomorrow);
    } else if (expenseRequestDto.getRegularity().equals(Regularity.WEEKLY)) {
      expense.setDueDate(oneWeekLater);
    } else if (expenseRequestDto.getRegularity().equals(Regularity.MONTHLY)) {
      expense.setDueDate(oneMonthLater);
    } else if (expenseRequestDto.getRegularity().equals(Regularity.QUARTERLY)) {
      expense.setDueDate(threeMonthsLater);
    } else if (expenseRequestDto.getRegularity().equals(Regularity.SIX_MONTHS)) {
      expense.setDueDate(sixMonthsLater);
    } else if (expenseRequestDto.getRegularity().equals(Regularity.ANNUAL)) {
      expense.setDueDate(oneYearLater);
    }
  }

  private void setExpenseCategory(ExpenseRequestDto expenseRequestDto, Expense expense) {
    ExpenseCategory expenseCategory =
        expenseCategoryService.getCategory(expenseRequestDto.getCategory());
    expense.setCategory(expenseCategory);
  }

  private void setSubcategory(ExpenseRequestDto expenseRequestDto, Expense expense) {
    Subcategory subcategory =
        subcategoryRepository
            .findByName(expenseRequestDto.getSubcategory())
            .orElseThrow(SubcategoryNotFoundException::new);
    expense.setSubcategory(subcategory);
  }
}
