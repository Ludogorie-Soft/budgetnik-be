package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

  public InternalServerErrorException() {
    super("Възникна проблем! Моля влезте в акаунта си отновоѝ1", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
