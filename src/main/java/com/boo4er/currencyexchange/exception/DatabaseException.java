package com.boo4er.currencyexchange.exception;

public class DatabaseException extends AppException {
    private static final int STATUS_CODE = 500;

    public DatabaseException(String message) {
        super(message, STATUS_CODE);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, STATUS_CODE, cause);
    }
}
