package com.boo4er.currencyexchange.exception;

public class ConflictException extends AppException {
    public ConflictException(String message) {
        super(message, 409);
    }
}
