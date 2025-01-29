package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.exception.IncomeNotFoundException;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
import com.ludogorieSoft.budgetnik.service.IncomeService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

  private final UserService userService;
  private final ModelMapper modelMapper;
  private final IncomeRepository incomeRepository;
  private final IncomeCategoryService incomeCategoryService;

  @Override
  @Transactional
  public IncomeResponseDto createIncome(IncomeRequestDto incomeRequestDto) {
    Income income = new Income();

    User user = userService.findById(incomeRequestDto.getOwnerId());
    income.setOwner(user);
    income.setType(incomeRequestDto.getType());

    if (incomeRequestDto.getType().equals(Type.FIXED)) {
      income.setRegularity(incomeRequestDto.getRegularity());
      fillFixedIncomeRelation(incomeRequestDto, income);
      income.setAutoCreate(incomeRequestDto.isAutoCreate());
    } else {
      income.setOneTimeIncome(incomeRequestDto.getOneTimeIncome());
    }

    IncomeCategory incomeCategory =
        incomeCategoryService.getCategory(incomeRequestDto.getCategory());
    income.setCategory(incomeCategory);
    income.setCreationDate(incomeRequestDto.getCreationDate());
    income.setSum(incomeRequestDto.getSum());

    Income createdIncome = incomeRepository.save(income);

    if (incomeRequestDto.getRelatedIncomeId() != null) {
      Income relatedIncome = findById(incomeRequestDto.getRelatedIncomeId());
      relatedIncome.setRelatedIncome(createdIncome);
      incomeRepository.save(relatedIncome);
    }

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

    Income relatedIncome = incomeRepository.findByRelatedIncomeId(income.getId()).orElse(null);

    if (relatedIncome != null) {
      relatedIncome.setRelatedIncome(null);
    }

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
    IncomeCategory incomeCategory = incomeCategoryService.getCategory(category);
    return incomeRepository.calculateTotalSumByUserIdAndCategory(userId, incomeCategory);
  }

  @Override
  public IncomeResponseDto editIncome(UUID incomeId, IncomeRequestDto incomeRequestDto) {
    Income income = findById(incomeId);
    income.setSum(incomeRequestDto.getSum());
    income.setType(incomeRequestDto.getType());

    if (incomeRequestDto.getType().equals(Type.FIXED)) {
      income.setRegularity(incomeRequestDto.getRegularity());
      fillFixedIncomeRelation(incomeRequestDto, income);
      income.setAutoCreate(incomeRequestDto.isAutoCreate());
    } else {
      income.setOneTimeIncome(incomeRequestDto.getOneTimeIncome());
    }

    IncomeCategory incomeCategory =
        incomeCategoryService.getCategory(incomeRequestDto.getCategory());
    income.setCategory(incomeCategory);
    income.setCreationDate(incomeRequestDto.getCreationDate());

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

  @Override
  public List<IncomeResponseDto> getAllIncomesOfUserForPeriod(
      UUID userId, LocalDate firstDate, LocalDate lastDate) {
    User user = userService.findById(userId);
    return incomeRepository.findIncomesForPeriod(user, firstDate, lastDate).stream()
        .map(income -> modelMapper.map(income, IncomeResponseDto.class))
        .toList();
  }

  @Override
  public BigDecimal getSumOfAllIncomesOfUserForPeriodByCategory(
      UUID id, String category, LocalDate firstDate, LocalDate lastDate) {
    User user = userService.findById(id);
    IncomeCategory incomeCategory = incomeCategoryService.getCategory(category);
    return incomeRepository.calculateSumOfIncomesByCategory(
        user, incomeCategory, firstDate, lastDate);
  }

  @Override
  public BigDecimal calculateSumOfUserIncomesByTypeAndPeriod(
      UUID userId, Type type, LocalDate startDate, LocalDate endDate) {
    return incomeRepository.calculateSumOfUserIncomesByTypeAndPeriod(
        userId, type, startDate, endDate);
  }

  @Override
  public List<IncomeResponseDto> findAllFixedIncomesByDueDate(LocalDate date, Type type) {
    return incomeRepository.findByDueDateAndRelatedIncomeIsNullAndType(date, type).stream()
            .map(income -> modelMapper.map(income, IncomeResponseDto.class))
            .toList();
  }

  private Income findById(UUID id) {
    return incomeRepository.findById(id).orElseThrow(IncomeNotFoundException::new);
  }

  private void fillFixedIncomeRelation(IncomeRequestDto incomeRequestDto, Income income) {

    LocalDate today = incomeRequestDto.getCreationDate();
    LocalDate tomorrow = today.plusDays(1);
    LocalDate oneWeekLater = today.plusWeeks(1);
    LocalDate oneMonthLater = today.plusMonths(1);
    LocalDate threeMonthsLater = today.plusMonths(3);
    LocalDate sixMonthsLater = today.plusMonths(6);
    LocalDate oneYearLater = today.plusYears(1);

    if (incomeRequestDto.getRegularity().equals(Regularity.DAILY)) {
      income.setDueDate(tomorrow);
    } else if (incomeRequestDto.getRegularity().equals(Regularity.WEEKLY)) {
      income.setDueDate(oneWeekLater);
    } else if (incomeRequestDto.getRegularity().equals(Regularity.MONTHLY)) {
      income.setDueDate(oneMonthLater);
    } else if (incomeRequestDto.getRegularity().equals(Regularity.QUARTERLY)) {
      income.setDueDate(threeMonthsLater);
    } else if (incomeRequestDto.getRegularity().equals(Regularity.SIX_MONTHS)) {
      income.setDueDate(sixMonthsLater);
    } else if (incomeRequestDto.getRegularity().equals(Regularity.ANNUAL)) {
      income.setDueDate(oneYearLater);
    }
  }
}
