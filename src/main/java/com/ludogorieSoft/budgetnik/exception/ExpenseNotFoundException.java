package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class ExpenseNotFoundException extends ApiException{
    public ExpenseNotFoundException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.NOT_FOUND);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.expense_not_found", null, LocaleContextHolder.getLocale());
    }
}
