package com.fintech.apis.exception;

public class DataValidationException extends Exception {
    public DataValidationException(String exceptionMessage, Throwable throwable) {
        super(exceptionMessage, throwable);
    }

    public DataValidationException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
