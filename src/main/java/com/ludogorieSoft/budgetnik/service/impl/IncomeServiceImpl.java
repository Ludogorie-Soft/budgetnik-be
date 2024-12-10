package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.exception.IncomeNotFoundException;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.service.IncomeService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

  private final UserService userService;
  private final ModelMapper modelMapper;
  private final IncomeRepository incomeRepository;

  @Override
  public IncomeResponseDto createIncome(IncomeRequestDto incomeRequestDto) {
    Income income = new Income();

    User user = userService.findById(incomeRequestDto.getOwnerId());
    income.setOwner(user);
    income.setType(incomeRequestDto.getType());

    if (incomeRequestDto.getType().equals(Type.FIXED)) {
      income.setRegularity(incomeRequestDto.getRegularity());
    } else {
      income.setOneTimeIncome(incomeRequestDto.getOneTimeIncome());
    }

    income.setCategory(incomeRequestDto.getCategory());
    income.setDate(incomeRequestDto.getDate());
    income.setSum(incomeRequestDto.getSum());

    incomeRepository.save(income);
    return modelMapper.map(income, IncomeResponseDto.class);
  }

  @Override
  public IncomeResponseDto getIncome(UUID id) {
    Income income = findById(id);
    return modelMapper.map(income, IncomeResponseDto.class);
  }

  @Override
  public List<IncomeResponseDto> getAllIncomesOfUser(UUID userId) {
    User user = userService.findById(userId);
    return incomeRepository.findAllByOwner(user).stream()
        .map(income -> modelMapper.map(income, IncomeResponseDto.class))
        .toList();
  }

  @Override
  public IncomeResponseDto deleteIncome(UUID id) {
    Income income = findById(id);
    IncomeResponseDto response = modelMapper.map(income, IncomeResponseDto.class);
    incomeRepository.delete(income);
    return response;
  }

  @Override
  public BigDecimal calculateSumOfAllIncomesOfUser(UUID userId) {
    return incomeRepository.calculateTotalSumByUserId(userId);
  }

  @Override
  public BigDecimal calculateSumOfAllIncomesOfUserByCategory(UUID userId, String category) {
    return incomeRepository.calculateTotalSumByUserIdAndCategory(userId, category);
  }

  @Override
  public IncomeResponseDto editIncome(UUID incomeId, IncomeRequestDto incomeRequestDto) {
    Income income = findById(incomeId);
    income.setSum(incomeRequestDto.getSum());
    income.setType(incomeRequestDto.getType());

    if (incomeRequestDto.getType().equals(Type.FIXED)) {
      income.setRegularity(incomeRequestDto.getRegularity());
    } else {
      income.setOneTimeIncome(incomeRequestDto.getOneTimeIncome());
    }

    income.setDate(incomeRequestDto.getDate());

    incomeRepository.save(income);
    return modelMapper.map(income, IncomeResponseDto.class);
  }

  @Override
  public BigDecimal calculateSumOfUserIncomesByType(UUID userId, Type type) {
    return incomeRepository.calculateSumOfUserIncomesByType(userId, type);
  }

  @Override
  public List<IncomeResponseDto> findAllByUserAndType(UUID userId, Type type) {
    User user = userService.findById(userId);
    return incomeRepository.findAllByOwnerAndType(user, type).stream()
        .map(income -> modelMapper.map(income, IncomeResponseDto.class))
        .toList();
  }

  private Income findById(UUID id) {
    return incomeRepository.findById(id).orElseThrow(IncomeNotFoundException::new);
  }
}
