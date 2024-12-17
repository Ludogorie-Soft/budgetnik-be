package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.exception.ExpenseNotFoundException;
import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
import com.ludogorieSoft.budgetnik.service.ExpenseCategoryService;
import com.ludogorieSoft.budgetnik.service.ExpenseService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final ExpenseCategoryService expenseCategoryService;

  @Override
  public ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto) {
    Expense expense = new Expense();
    User user = userService.findById(expenseRequestDto.getOwnerId());
    expense.setOwner(user);
    expense.setType(expenseRequestDto.getType());
    if (expenseRequestDto.getType().equals(Type.FIXED)) {
      expense.setRegularity(expenseRequestDto.getRegularity());
    } else {
      expense.setOneTimeExpense(expenseRequestDto.getOneTimeExpense());
    }

    expense.setDate(expenseRequestDto.getDate());
    expense.setSum(expenseRequestDto.getSum());
    ExpenseCategory expenseCategory =
        expenseCategoryService.getCategory(expenseRequestDto.getCategory());
    expense.setCategory(expenseCategory);

    expenseRepository.save(expense);
    return modelMapper.map(expense, ExpenseResponseDto.class);
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
  public ExpenseResponseDto deleteExpense(UUID id) {
    Expense expense = findById(id);
    ExpenseResponseDto response = modelMapper.map(expense, ExpenseResponseDto.class);
    expenseRepository.delete(expense);
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

    if (expenseRequestDto.getType().equals(Type.FIXED)) {
      expense.setRegularity(expenseRequestDto.getRegularity());
    } else {
      expense.setOneTimeExpense(expenseRequestDto.getOneTimeExpense());
    }
    ExpenseCategory expenseCategory =
        expenseCategoryService.getCategory(expenseRequestDto.getCategory());
    expense.setCategory(expenseCategory);
    expense.setDate(expenseRequestDto.getDate());
    expenseRepository.save(expense);
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

  private Expense findById(UUID id) {
    return expenseRepository.findById(id).orElseThrow(ExpenseNotFoundException::new);
  }
}
