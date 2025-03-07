package com.ludogorieSoft.budgetnik.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

public class CategoryException extends ApiException {
    public CategoryException(MessageSource messageSource) {
        super(getLocalizedMessage(messageSource), HttpStatus.FORBIDDEN);
    }

    private static String getLocalizedMessage(MessageSource messageSource) {
        return messageSource.getMessage("error.category_not_delete", null, LocaleContextHolder.getLocale());
    }
}
