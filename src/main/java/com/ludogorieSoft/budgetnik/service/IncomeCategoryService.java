package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import java.util.List;

public interface IncomeCategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    IncomeCategory getCategory(String name);
    List<CategoryResponseDto> getAllCategories();
}
