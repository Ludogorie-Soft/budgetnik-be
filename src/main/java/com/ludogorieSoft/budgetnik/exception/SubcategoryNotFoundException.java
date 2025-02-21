package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class SubcategoryNotFoundException extends ApiException {
    public SubcategoryNotFoundException() {
        super("Субкатегорията не е намерена!", HttpStatus.NOT_FOUND);
    }
}
