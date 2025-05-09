package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponse;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponse;
import com.ludogorieSoft.budgetnik.dto.response.TransactionCountResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.service.AdminService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final UserRepository userRepository;
  private final IncomeRepository incomeRepository;
  private final ExpenseRepository expenseRepository;
  private final ModelMapper modelMapper;

  @Override
  public long getCountOfRegisteredUsers() {
    return userRepository.count();
  }

  @Override
  public Page<UserResponse> searchUsers(String searchTerm, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);

    if (searchTerm == null || searchTerm.trim().isEmpty()) {
      Page<User> userPage = userRepository.findAll(pageable);
      return userPage.map(user -> modelMapper.map(user, UserResponse.class));
    }
    return userRepository
        .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(searchTerm, searchTerm, pageable)
        .map(u -> modelMapper.map(u, UserResponse.class));
  }

  @Override
  public List<UserResponse> getLastTenRegisteredUsers() {
    return userRepository.findTop10RegisteredUsersDesc().stream()
        .map(u -> modelMapper.map(u, UserResponse.class))
        .toList();
  }

  @Override
  public List<UserResponse> getLastTenLoggedInUsers() {
    return userRepository.findTop10LastLoginUsersDesc().stream()
        .map(u -> modelMapper.map(u, UserResponse.class))
        .toList();
  }

  @Override
  public TransactionCountResponseDto getCountOfEnteredIncomes() {
    LocalDate time24HoursAgo = LocalDate.now().minusDays(1);
    LocalDate timeWeekAgo = LocalDate.now().minusWeeks(1);
    LocalDate timeMonthAgo = LocalDate.now().minusMonths(1);
    LocalDate timeYearAgo = LocalDate.now().minusYears(1);
    return TransactionCountResponseDto.builder()
        .daily(getIncomesCount(time24HoursAgo))
        .weekly(getIncomesCount(timeWeekAgo))
        .monthly(getIncomesCount(timeMonthAgo))
        .annual(getIncomesCount(timeYearAgo))
        .build();
  }

  @Override
  public TransactionCountResponseDto getCountOfEnteredExpenses() {
    LocalDate time24HoursAgo = LocalDate.now().minusDays(1);
    LocalDate timeWeekAgo = LocalDate.now().minusWeeks(1);
    LocalDate timeMonthAgo = LocalDate.now().minusMonths(1);
    LocalDate timeYearAgo = LocalDate.now().minusYears(1);
    return TransactionCountResponseDto.builder()
        .daily(getExpensesCount(time24HoursAgo))
        .weekly(getExpensesCount(timeWeekAgo))
        .monthly(getExpensesCount(timeMonthAgo))
        .annual(getExpensesCount(timeYearAgo))
        .build();
  }

  @Override
  public List<IncomeResponse> getAllIncomes() {
    return incomeRepository.findAll().stream()
        .map(i -> modelMapper.map(i, IncomeResponse.class))
        .toList();
  }

  @Override
  public List<ExpenseResponse> getAllExpenses() {
    return expenseRepository.findAll().stream()
        .map(e -> modelMapper.map(e, ExpenseResponse.class))
        .toList();
  }

  private long getIncomesCount(LocalDate periodStart) {
    return incomeRepository.countByDateAfter(periodStart);
  }

  private long getExpensesCount(LocalDate periodStart) {
    return expenseRepository.countByDateAfter(periodStart);
  }
}
