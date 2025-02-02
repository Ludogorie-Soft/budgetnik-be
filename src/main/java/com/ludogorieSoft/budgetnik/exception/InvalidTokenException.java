package com.ludogorieSoft.budgetnik.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException{
    public InvalidTokenException() {
        super("Изтекла сесия! Моля влезте отново във вашият акаунт!", HttpStatus.UNAUTHORIZED);
    }
}
