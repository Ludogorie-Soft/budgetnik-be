package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
  public AccessDeniedException(MessageSource messageSource) {
    super(getLocalizedMessage(messageSource), HttpStatus.FORBIDDEN);
  }


  private static String getLocalizedMessage(MessageSource messageSource) {
    return messageSource.getMessage("error.access_denied", null, LocaleContextHolder.getLocale());
  }
}
