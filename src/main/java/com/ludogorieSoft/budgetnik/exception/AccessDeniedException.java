package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
  public AccessDeniedException() {
    super("Достъпът е забранен!", HttpStatus.FORBIDDEN);
  }
}
