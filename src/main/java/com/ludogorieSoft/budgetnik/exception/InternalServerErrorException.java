package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

  public InternalServerErrorException() {
    super("Възникна проблем! Моля влезте отново във вашият акаунт!", HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
