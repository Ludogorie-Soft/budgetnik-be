package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.exception.CategoryExistsException;
import com.ludogorieSoft.budgetnik.exception.CategoryNotFoundException;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.repository.IncomeCategoryRepository;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeCategoryServiceImpl implements IncomeCategoryService {

  private static final Logger logger = LoggerFactory.getLogger(IncomeCategoryServiceImpl.class);

  private final IncomeCategoryRepository incomeCategoryRepository;
  private final ModelMapper modelMapper;

  @Override
  public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
    if (incomeCategoryRepository.existsByName(categoryRequestDto.getName())) {
      throw new CategoryExistsException();
    }

    IncomeCategory incomeCategory = new IncomeCategory();
    incomeCategory.setName(categoryRequestDto.getName());
    incomeCategory.setBgName(categoryRequestDto.getBgName());
    incomeCategoryRepository.save(incomeCategory);

    logger.info("Created income category with name: " + categoryRequestDto.getName());
    return modelMapper.map(incomeCategory, CategoryResponseDto.class);
  }

  @Override
  public IncomeCategory getCategory(String name) {
    return incomeCategoryRepository.findByName(name).orElseThrow(CategoryNotFoundException::new);
  }

  @Override
  public List<CategoryResponseDto> getAllCategories() {
    return incomeCategoryRepository.findAll().stream()
        .map(category -> modelMapper.map(category, CategoryResponseDto.class))
        .toList();
  }
}
