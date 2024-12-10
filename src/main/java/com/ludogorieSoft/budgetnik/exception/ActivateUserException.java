package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class ActivateUserException extends ApiException {
  public ActivateUserException() {
    super("Моля активирайте вашият профил!", HttpStatus.BAD_REQUEST);
  }
}
