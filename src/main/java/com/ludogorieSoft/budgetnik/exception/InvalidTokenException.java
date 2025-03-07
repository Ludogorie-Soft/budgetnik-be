package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException{
    public InvalidTokenException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.UNAUTHORIZED);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.invalid_token", null, LocaleContextHolder.getLocale());
    }
}
