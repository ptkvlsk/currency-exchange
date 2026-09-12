package com.boo4er.currencyexchange.exception;

public class DatabaseException extends AppException {
    public DatabaseException(String message) {
        super(message, 500);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, 500, cause);
    }
}
