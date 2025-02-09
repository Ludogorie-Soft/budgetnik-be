package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class CategoryException extends ApiException {
    public CategoryException() {
        super("Тази категория не може да бъде изтрита!", HttpStatus.FORBIDDEN);
    }
}
