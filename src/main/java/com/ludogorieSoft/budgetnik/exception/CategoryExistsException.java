package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class CategoryExistsException extends ApiException {
  public CategoryExistsException() {
    super("Тази категория вече съществува!", HttpStatus.BAD_REQUEST);
  }
}
