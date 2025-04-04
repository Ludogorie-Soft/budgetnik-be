package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.response.ExpenseResponse;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponse;
import com.ludogorieSoft.budgetnik.dto.response.TransactionCountResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AdminService {
  long getCountOfRegisteredUsers();

  Page<UserResponse> searchUsers(String searchTerm, int page, int size);

  List<UserResponse> getLastTenRegisteredUsers();

  List<UserResponse> getLastTenLoggedInUsers();

  TransactionCountResponseDto getCountOfEnteredIncomes();

  TransactionCountResponseDto getCountOfEnteredExpenses();

  List<IncomeResponse> getAllIncomes();
  List<ExpenseResponse> getAllExpenses();
}
