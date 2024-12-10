package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class UserLoginException extends ApiException{
    public UserLoginException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
