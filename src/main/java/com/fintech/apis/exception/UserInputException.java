package com.fintech.apis.exception;

public class UserInputException extends Exception {
    public UserInputException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public UserInputException(String message) {
        super(message);
    }
}
