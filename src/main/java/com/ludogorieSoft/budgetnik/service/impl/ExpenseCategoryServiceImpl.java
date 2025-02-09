package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.exception.CategoryException;
import com.ludogorieSoft.budgetnik.exception.CategoryExistsException;
import com.ludogorieSoft.budgetnik.exception.CategoryNotFoundException;
import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.repository.ExpenseCategoryRepository;
import com.ludogorieSoft.budgetnik.repository.ExpenseRepository;
import com.ludogorieSoft.budgetnik.service.ExpenseCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

  private static final Logger logger = LoggerFactory.getLogger(ExpenseCategoryServiceImpl.class);

  private final ExpenseCategoryRepository expenseCategoryRepository;
  private final ModelMapper modelMapper;
  private final ExpenseRepository expenseRepository;

  @Override
  public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
    if (expenseCategoryRepository.existsByName(categoryRequestDto.getName())) {
      throw new CategoryExistsException();
    }
    ExpenseCategory expenseCategory = new ExpenseCategory();
    expenseCategory.setName(categoryRequestDto.getName());
    expenseCategory.setBgName(categoryRequestDto.getBgName());
    expenseCategoryRepository.save(expenseCategory);

    logger.info("Created category with name: " + categoryRequestDto.getName());
    return modelMapper.map(expenseCategory, CategoryResponseDto.class);
  }

  @Override
  public ExpenseCategory getCategory(String name) {
    return expenseCategoryRepository.findByName(name).orElseThrow(CategoryNotFoundException::new);
  }

  @Override
  public List<CategoryResponseDto> getAllCategories() {
    return expenseCategoryRepository.findAll().stream()
        .map(category -> modelMapper.map(category, CategoryResponseDto.class))
        .toList();
  }

  @Override
  public void deleteCategory(String name) {
    if (name.equals("other")) {
      throw new CategoryException();
    }
    ExpenseCategory expenseCategory = getCategory(name);
    ExpenseCategory other = getCategory("other");

    List<Expense> expenses = expenseRepository.findByCategory(expenseCategory);

    if (!expenses.isEmpty()) {
      for (Expense current : expenses) {
        current.setCategory(other);
      }
      expenseRepository.saveAll(expenses);
    }
    expenseCategoryRepository.delete(expenseCategory);
  }
}
