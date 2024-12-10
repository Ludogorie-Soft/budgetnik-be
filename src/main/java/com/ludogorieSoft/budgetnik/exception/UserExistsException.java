package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class UserExistsException extends ApiException {
  public UserExistsException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
  }
}
