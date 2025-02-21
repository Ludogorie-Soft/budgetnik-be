package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.IncomeRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.IncomeResponseDto;
import com.ludogorieSoft.budgetnik.exception.IncomeNotFoundException;
import com.ludogorieSoft.budgetnik.exception.SubcategoryNotFoundException;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.Subcategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.repository.SubcategoryRepository;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
import com.ludogorieSoft.budgetnik.service.IncomeService;
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
public class IncomeServiceImpl implements IncomeService {

  private static final Logger logger = LoggerFactory.getLogger(IncomeServiceImpl.class);

  private final UserService userService;
  private final ModelMapper modelMapper;
  private final IncomeRepository incomeRepository;
  private final IncomeCategoryService incomeCategoryService;
  private final SubcategoryRepository subcategoryRepository;

  @Override
  @Transactional
  public IncomeResponseDto createIncome(IncomeRequestDto incomeRequestDto) {
    Income income = new Income();

    User user = userService.findById(incomeRequestDto.getOwnerId());
    income.setOwner(user);
    income.setCreationDate(incomeRequestDto.getCreationDate());
    income.setType(incomeRequestDto.getType());
    income.setRegularity(incomeRequestDto.getRegularity());
    income.setDescription(incomeRequestDto.getDescription());
    income.setSum(incomeRequestDto.getSum());

    setIncomeCategory(incomeRequestDto, income);
    setSubcategory(incomeRequestDto, income);
    setIncomeDueDate(incomeRequestDto, income);
    Income createdIncome = incomeRepository.save(income);

    if (incomeRequestDto.getRelatedIncomeId() != null) {
      Income relatedIncome = findById(incomeRequestDto.getRelatedIncomeId());
      relatedIncome.getRelatedIncomes().add(createdIncome);
      setIncomeDueDate(incomeRequestDto, relatedIncome);
      createdIncome.setRelatedIncome(relatedIncome);
      incomeRepository.save(relatedIncome);
    }

    logger.info("Created income with id " + createdIncome.getId());
    return modelMapper.map(createdIncome, IncomeResponseDto.class);
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
  @Transactional
  public IncomeResponseDto deleteIncome(UUID id) {
    Income income = findById(id);

    if (income.getRelatedIncomes() != null) {
      for (Income related : income.getRelatedIncomes()) {
        related.setRelatedIncome(null);
        incomeRepository.save(related);
      }
    }

    if (income.getRelatedIncome() != null) {
      Income relatedIncome = findById(income.getRelatedIncome().getId());

      List<Income> relatedIncomes = relatedIncome.getRelatedIncomes();

      relatedIncomes.removeIf(current -> current.getId() == income.getId());

      relatedIncome.setRelatedIncomes(relatedIncomes);
      incomeRepository.save(relatedIncome);
    }

    IncomeResponseDto response = modelMapper.map(income, IncomeResponseDto.class);
    incomeRepository.delete(income);
    logger.info("Deleted income with id " + response.getId());
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
    income.setCreationDate(incomeRequestDto.getCreationDate());
    income.setRegularity(incomeRequestDto.getRegularity());
    income.setDescription(incomeRequestDto.getDescription());

    setIncomeCategory(incomeRequestDto, income);
    setSubcategory(incomeRequestDto, income);
    setIncomeDueDate(incomeRequestDto, income);

    incomeRepository.save(income);
    logger.info("Edited income with id " + income.getId());
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
  public List<IncomeResponseDto> findAllFixedIncomesBeforeThanEqualDueDate(
      LocalDate date, Type type, UUID userId) {
    return incomeRepository.findByDueDateLessThanEqualAndTypeAndOwnerId(date, type, userId).stream()
        .map(income -> modelMapper.map(income, IncomeResponseDto.class))
        .toList();
  }

  @Override
  public List<IncomeResponseDto> findRelatedIncomes(UUID incomeId, UUID userId) {
    return incomeRepository.findByRelatedIncomeIdAndOwnerId(incomeId, userId).stream()
        .map(income -> modelMapper.map(income, IncomeResponseDto.class))
        .toList();
  }

  private Income findById(UUID id) {
    return incomeRepository.findById(id).orElseThrow(IncomeNotFoundException::new);
  }

  private void setIncomeDueDate(IncomeRequestDto incomeRequestDto, Income income) {

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

  private void setIncomeCategory(IncomeRequestDto incomeRequestDto, Income income) {
    IncomeCategory incomeCategory =
        incomeCategoryService.getCategory(incomeRequestDto.getCategory());
    income.setCategory(incomeCategory);
  }

  private void setSubcategory(IncomeRequestDto incomeRequestDto, Income income) {
    if (!incomeRequestDto.getSubcategory().isEmpty()) {
      Subcategory subcategory =
          subcategoryRepository
              .findByName(incomeRequestDto.getSubcategory())
              .orElseThrow(SubcategoryNotFoundException::new);
      income.setSubcategory(subcategory);
    }
  }
}
