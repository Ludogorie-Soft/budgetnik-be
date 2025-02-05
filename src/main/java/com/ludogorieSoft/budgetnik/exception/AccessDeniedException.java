package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
  public AccessDeniedException() {
    super("Достъпът е забранен! Моля влезте във вашият акаунт!", HttpStatus.FORBIDDEN);
  }
}
