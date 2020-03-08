package com.fintech.apis.handler;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.exception.UserInputException;

public interface IHandler<I, O> {
    O handle(I userRequest) throws UserInputException, DataValidationException;
}
