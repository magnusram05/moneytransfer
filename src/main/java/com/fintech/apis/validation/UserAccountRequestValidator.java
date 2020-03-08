package com.fintech.apis.validation;

import com.fintech.apis.model.UserAccountRequest;

import static com.fintech.apis.util.AppConstants.INVALID_ACCOUNT;
import static com.fintech.apis.util.AppConstants.INVALID_USER;

public class UserAccountRequestValidator implements Validator<UserAccountRequest> {
    @Override
    public ValidationResults validate(UserAccountRequest request) {
        ValidationResults.Builder builder = ValidationResults.newBuilder();
        builder.setValid(true);
        if (request.getUserID() <= 0) {
            builder.addValidationResults(INVALID_USER);
            builder.setValid(false);
        }
        if (request.getAccountID() <= 0) {
            builder.addValidationResults(INVALID_ACCOUNT);
            builder.setValid(false);
        }
        return builder.build();
    }
}
