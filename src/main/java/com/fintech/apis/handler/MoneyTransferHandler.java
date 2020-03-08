package com.fintech.apis.handler;

import com.fintech.apis.dao.IMoneyTransferDAO;
import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.exception.UserInputException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.BaseResponse;
import com.fintech.apis.model.MoneyTransferRequest;
import com.fintech.apis.model.User;
import com.fintech.apis.validation.ValidationResults;
import com.fintech.apis.validation.Validator;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.fintech.apis.util.AppConstants.TRANSFER_SUCCESS;

public class MoneyTransferHandler implements IHandler<MoneyTransferRequest, BaseResponse> {
    private final Validator<MoneyTransferRequest> moneyTransferRequestRequestValidator;
    private final IMoneyTransferDAO moneyTransferDAO;

    public MoneyTransferHandler(Validator<MoneyTransferRequest> moneyTransferRequestRequestValidator, IMoneyTransferDAO moneyTransferDAO) {
        this.moneyTransferRequestRequestValidator = moneyTransferRequestRequestValidator;
        this.moneyTransferDAO = moneyTransferDAO;
    }

    @Override
    public BaseResponse handle(MoneyTransferRequest userRequest) throws UserInputException, DataValidationException {
        ValidationResults validationResults = this.moneyTransferRequestRequestValidator.validate(userRequest);
        if (!validationResults.isValid())
            throw new UserInputException(validationResults.getValidationResults().stream().collect(Collectors.joining(",")));
        this.moneyTransferDAO.transfer(User.newBuilder().setUserID(userRequest.getUserID()).build()
                , Account.newBuilder().setAccountID(userRequest.getFromAccount()).setUserID(userRequest.getUserID()).build()
                , Account.newBuilder().setAccountID(userRequest.getToAccount()).build()
                , userRequest.getAmountToTransfer());
        return BaseResponse.successResponse(Arrays.asList(TRANSFER_SUCCESS));
    }


}
