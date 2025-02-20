package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SubcategoryRequest;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SubcategoryResponse;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import java.util.List;
import java.util.UUID;

public interface IncomeCategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto);
    IncomeCategory getCategory(String name);
    List<CategoryResponseDto> getAllCategories();
    void deleteCategory(String name);
    SubcategoryResponse attachIncomeSubcategory(SubcategoryRequest subcategoryRequest);
}
