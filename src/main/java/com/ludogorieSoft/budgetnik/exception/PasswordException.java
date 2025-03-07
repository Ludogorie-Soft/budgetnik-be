package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class PasswordException extends ApiException{
    public PasswordException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.BAD_REQUEST);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.password_not_match", null, LocaleContextHolder.getLocale());
    }
}
