package com.fintech.apis.validation;

public interface Validator<I> {
    ValidationResults validate(I request);
}
