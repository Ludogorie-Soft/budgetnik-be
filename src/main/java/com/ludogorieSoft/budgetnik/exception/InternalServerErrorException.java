package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class InternalServerErrorException extends ApiException {

  public InternalServerErrorException(MessageSource messageSource) {
    super(getLocalizedMessage(messageSource), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private static String getLocalizedMessage(MessageSource messageSource) {
    return messageSource.getMessage("error.internal_server_error", null, LocaleContextHolder.getLocale());
  }
}
