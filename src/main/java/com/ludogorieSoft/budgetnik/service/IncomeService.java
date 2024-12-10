package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.enums.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface IncomeService {
    IncomeResponseDto createIncome(IncomeRequestDto incomeRequestDto);
    IncomeResponseDto getIncome(UUID id);
    List<IncomeResponseDto> getAllIncomesOfUser(UUID userId);
    IncomeResponseDto deleteIncome(UUID id);
    BigDecimal calculateSumOfAllIncomesOfUser(UUID userId);
    BigDecimal calculateSumOfAllIncomesOfUserByCategory(UUID userId, String category);
    IncomeResponseDto editIncome(UUID incomeId, IncomeRequestDto  incomeRequestDto);
    BigDecimal calculateSumOfUserIncomesByType(UUID userId, Type type);
    List<IncomeResponseDto> findAllByUserAndType(UUID userId, Type type);
}
