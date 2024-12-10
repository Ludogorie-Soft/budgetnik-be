package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException{
    public InvalidTokenException() {
        super("Invalid token!", HttpStatus.UNAUTHORIZED);
    }
}
