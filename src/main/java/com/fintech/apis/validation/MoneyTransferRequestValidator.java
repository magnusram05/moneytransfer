package com.fintech.apis.validation;

import com.fintech.apis.model.MoneyTransferRequest;

import java.util.Objects;

import static com.fintech.apis.util.AppConstants.*;

public class MoneyTransferRequestValidator implements Validator<MoneyTransferRequest> {
    @Override
    public ValidationResults validate(MoneyTransferRequest request) {
        ValidationResults.Builder builder = ValidationResults.newBuilder();
        builder.setValid(true);
        if (request.getUserID() <= 0) {
            builder.addValidationResults(INVALID_USER);
            builder.setValid(false);
        }
        if (Objects.isNull(request.getAmountToTransfer()) || request.getAmountToTransfer().doubleValue() <= 0) {
            builder.addValidationResults(INVALID_AMOUNT);
            builder.setValid(false);
        }
        if (request.getFromAccount() <= 0) {
            builder.addValidationResults(INVALID_DEBIT_ACCOUNT);
            builder.setValid(false);
        }
        if (request.getToAccount() <= 0) {
            builder.addValidationResults(INVALID_CREDIT_ACCOUNT);
            builder.setValid(false);
        }
        if (request.getToAccount() == request.getFromAccount()) {
            builder.addValidationResults(SAME_DEBIT_CREDIT_ACCOUNTS);
            builder.setValid(false);
        }
        return builder.build();
    }
}
