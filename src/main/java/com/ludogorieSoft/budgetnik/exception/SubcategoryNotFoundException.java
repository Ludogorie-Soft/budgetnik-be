package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class SubcategoryNotFoundException extends ApiException {
    public SubcategoryNotFoundException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.NOT_FOUND);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.subcategory_not_found", null, LocaleContextHolder.getLocale());
    }
}
