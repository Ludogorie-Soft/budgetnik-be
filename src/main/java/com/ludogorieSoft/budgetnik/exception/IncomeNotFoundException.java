package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class IncomeNotFoundException extends ApiException{
    public IncomeNotFoundException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.NOT_FOUND);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.income_not_found", null, LocaleContextHolder.getLocale());
    }
}
