package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends ApiException {
  public CategoryNotFoundException() {
    super("Категорията не е намерена!", HttpStatus.NOT_FOUND);
  }
}
