package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.CategoryRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SubcategoryRequest;
import com.ludogorieSoft.budgetnik.dto.response.CategoryResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SubcategoryResponse;
import com.ludogorieSoft.budgetnik.service.ExpenseCategoryService;
import com.ludogorieSoft.budgetnik.service.IncomeCategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

  private final IncomeCategoryService incomeCategoryService;
  private final ExpenseCategoryService expenseCategoryService;

  @PostMapping("/incomes")
  public ResponseEntity<CategoryResponseDto> createIncomeCategory(
      @RequestBody CategoryRequestDto request) {
    CategoryResponseDto response = incomeCategoryService.createCategory(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PostMapping("/expenses")
  public ResponseEntity<CategoryResponseDto> createExpenseCategory(
      @RequestBody CategoryRequestDto request) {
    CategoryResponseDto response = expenseCategoryService.createCategory(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/incomes")
  public ResponseEntity<List<CategoryResponseDto>> getAllIncomeCategories() {
    List<CategoryResponseDto> response = incomeCategoryService.getAllCategories();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/expenses")
  public ResponseEntity<List<CategoryResponseDto>> getAllExpenseCategories() {
    List<CategoryResponseDto> response = expenseCategoryService.getAllCategories();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/expenses")
  public ResponseEntity<String> deleteExpenseCategory(@RequestParam("name") String name) {
    expenseCategoryService.deleteCategory(name);
    return new ResponseEntity<>("Категорията е изтрита успешно!", HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/incomes")
  public ResponseEntity<String> deleteIncomeCategory(@RequestParam("name") String name) {
    incomeCategoryService.deleteCategory(name);
    return new ResponseEntity<>("Категорията е изтрита успешно!", HttpStatus.NO_CONTENT);
  }

  @PostMapping("/incomes/subcategories")
  public ResponseEntity<SubcategoryResponse> attachIncomeSubcategory(
      @RequestBody SubcategoryRequest request) {
    SubcategoryResponse response = incomeCategoryService.attachIncomeSubcategory(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/expenses/subcategories")
  public ResponseEntity<SubcategoryResponse> attachExpenseSubcategory(
      @RequestBody SubcategoryRequest request) {
    SubcategoryResponse response = expenseCategoryService.attachExpenseSubcategory(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
