package com.fintech.apis.handler;

import com.fintech.apis.dao.IUserAccountDAO;
import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.exception.UserInputException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.UserAccountRequest;
import com.fintech.apis.validation.ValidationResults;
import com.fintech.apis.validation.Validator;

import java.util.stream.Collectors;

import static com.fintech.apis.util.AppConstants.INVALID_DEBIT_ACCOUNT;

public class UserAccountHandler implements IHandler<UserAccountRequest, Account> {
    private final Validator<UserAccountRequest> userAccountRequestValidator;
    private final IUserAccountDAO userAccountDAO;

    public UserAccountHandler(IUserAccountDAO userAccountDAO, Validator<UserAccountRequest> userAccountRequestValidator) {
        this.userAccountDAO = userAccountDAO;
        this.userAccountRequestValidator = userAccountRequestValidator;
    }

    @Override
    public com.fintech.apis.model.Account handle(UserAccountRequest userAccountRequest) throws UserInputException, DataValidationException {
        Account userAccount = Account.newBuilder()
                .setAccountID(userAccountRequest.getAccountID())
                .setUserID(userAccountRequest.getUserID())
                .build();
        ValidationResults validationResults = userAccountRequestValidator.validate(userAccountRequest);
        if (!validationResults.isValid()) {
            throw new UserInputException(validationResults.getValidationResults().stream().collect(Collectors.joining(",")));
        } else if (!this.userAccountDAO.isValidFromAccount(userAccount)) {
            throw new DataValidationException(INVALID_DEBIT_ACCOUNT);
        }
        return this.userAccountDAO.getUserAccount(userAccount);
    }
}

