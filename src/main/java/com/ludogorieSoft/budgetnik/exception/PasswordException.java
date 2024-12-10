package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class PasswordException extends ApiException{
    public PasswordException(String message ) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
