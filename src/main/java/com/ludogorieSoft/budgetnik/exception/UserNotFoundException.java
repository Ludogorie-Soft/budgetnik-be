package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
  public UserNotFoundException() {
    super("Потребителят не е намерен!", HttpStatus.NOT_FOUND);
  }
}
