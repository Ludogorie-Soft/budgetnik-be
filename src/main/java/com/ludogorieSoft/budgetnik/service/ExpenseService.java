package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.ExpenseRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ExpenseService {
  ExpenseResponseDto createExpense(ExpenseRequestDto expenseRequestDto);

  ExpenseResponseDto getExpense(UUID id);

  List<ExpenseResponseDto> getAllExpensesOfUser(UUID userId);

  ExpenseResponseDto deleteExpense(UUID id);

  BigDecimal calculateSumOfAllExpensesOfUser(UUID userId);

  BigDecimal calculateSumOfAllExpensesOfUserByCategory(UUID userId, String category);

  ExpenseResponseDto editExpense(UUID expenseId, ExpenseRequestDto expenseRequestDto);

  BigDecimal calculateSumOfUserExpensesByType(UUID userId, Type type);

  List<ExpenseResponseDto> getAllExpensesOfUserByType(UUID userId, Type type);

  List<ExpenseResponseDto> getAllExpensesOfUserForPeriod(
      UUID userId, LocalDate firstDate, LocalDate lastDate);

  BigDecimal getSumOfAllExpensesOfUserForPeriodByCategory(
      UUID id, String category, LocalDate firstDate, LocalDate lastDate);
    }
