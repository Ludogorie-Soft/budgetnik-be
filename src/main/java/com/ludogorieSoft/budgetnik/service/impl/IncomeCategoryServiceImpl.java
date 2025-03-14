package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SubcategoryRequest;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SubcategoryResponse;
import com.ludogorieSoft.budgetnik.exception.CategoryException;
import com.ludogorieSoft.budgetnik.exception.CategoryExistsException;
import com.ludogorieSoft.budgetnik.exception.CategoryNotFoundException;
import com.ludogorieSoft.budgetnik.model.Income;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.Subcategory;
import com.ludogorieSoft.budgetnik.repository.IncomeCategoryRepository;
import com.ludogorieSoft.budgetnik.repository.IncomeRepository;
import com.ludogorieSoft.budgetnik.repository.SubcategoryRepository;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeCategoryServiceImpl implements IncomeCategoryService {

  private static final Logger logger = LoggerFactory.getLogger(IncomeCategoryServiceImpl.class);

  private final IncomeCategoryRepository incomeCategoryRepository;
  private final ModelMapper modelMapper;
  private final IncomeRepository incomeRepository;
  private final SubcategoryRepository subcategoryRepository;
  private final MessageSource messageSource;

  @Override
  public CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto) {
    if (incomeCategoryRepository.existsByName(categoryRequestDto.getName())) {
      throw new CategoryExistsException(messageSource);
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
    return incomeCategoryRepository
        .findByName(name)
        .orElseThrow(() -> new CategoryNotFoundException(messageSource));
  }

  @Override
  public List<CategoryResponseDto> getAllCategories() {
    List<CategoryResponseDto> categories =
        incomeCategoryRepository.findAll().stream()
            .map(category -> modelMapper.map(category, CategoryResponseDto.class))
            .toList();
    return categories.stream()
        .map(
            category -> {
              CategoryResponseDto response = modelMapper.map(category, CategoryResponseDto.class);
              List<SubcategoryResponse> subcategoryResponses =
                  category.getSubcategories().stream()
                      .map(sub -> modelMapper.map(sub, SubcategoryResponse.class))
                      .toList();
              response.setSubcategories(subcategoryResponses);
              return response;
            })
        .toList();
  }

  @Override
  public void deleteCategory(String name) {
    if (name.equals("other")) {
      throw new CategoryException(messageSource);
    }
    IncomeCategory incomeCategory = getCategory(name);
    IncomeCategory other = getCategory("other");

    List<Income> incomes = incomeRepository.findByCategory(incomeCategory);

    if (!incomes.isEmpty()) {
      for (Income current : incomes) {
        current.setCategory(other);
      }
      incomeRepository.saveAll(incomes);
    }
    incomeCategoryRepository.delete(incomeCategory);
  }

  @Override
  public SubcategoryResponse attachIncomeSubcategory(SubcategoryRequest subcategoryRequest) {
    IncomeCategory category = getCategory(subcategoryRequest.getCategoryName());

    Subcategory subcategory = new Subcategory();
    subcategory.setName(subcategoryRequest.getName());
    subcategory.setBgName(subcategoryRequest.getBgName());
    subcategory.setIncomeCategory(category);
    subcategoryRepository.save(subcategory);

    List<Subcategory> categorySubcategories = category.getSubcategories();
    categorySubcategories.add(subcategory);

    return modelMapper.map(subcategory, SubcategoryResponse.class);
  }
}
