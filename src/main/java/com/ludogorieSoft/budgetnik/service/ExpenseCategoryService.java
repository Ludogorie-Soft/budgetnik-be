package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SubcategoryRequest;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SubcategoryResponse;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import java.util.List;

public interface ExpenseCategoryService {
    CategoryResponseDto createCategory(CategoryRequestDto categoryRequestDto1);
    ExpenseCategory getCategory(String name);
    List<CategoryResponseDto> getAllCategories();
    void deleteCategory(String name);
    SubcategoryResponse attachExpenseSubcategory(SubcategoryRequest subcategoryRequest);
}
