package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class ExpenseNotFoundException extends ApiException{
    public ExpenseNotFoundException() {
        super("Разходът не е намерен!", HttpStatus.NOT_FOUND);
    }
}
