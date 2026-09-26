package com.boo4er.currencyexchange.exception;

public class NotFoundException extends AppException {
    private static final int STATUS_CODE = 404;

    public NotFoundException(String message) {
        super(message, STATUS_CODE);
    }
}
