package com.boo4er.currencyexchange.exception;

public class ValidationException extends AppException {
    private static final int STATUS_CODE = 400;

    public ValidationException(String message) {
        super(message, STATUS_CODE);
    }
}
