package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class IncomeNotFoundException extends ApiException{
    public IncomeNotFoundException() {
        super("Приходът не е намерен!", HttpStatus.NOT_FOUND);
    }
}
