package com.fintech.apis.factory;

import com.fintech.apis.dao.*;
import com.fintech.apis.handler.IHandler;
import com.fintech.apis.handler.MoneyTransferHandler;
import com.fintech.apis.handler.UserAccountHandler;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.BaseResponse;
import com.fintech.apis.model.MoneyTransferRequest;
import com.fintech.apis.model.UserAccountRequest;
import com.fintech.apis.validation.MoneyTransferRequestValidator;
import com.fintech.apis.validation.UserAccountRequestValidator;
import com.fintech.apis.validation.Validator;

public class RequestHandlerFactory {
    private static final Validator<MoneyTransferRequest> moneyTransferReqValidator
            = new MoneyTransferRequestValidator();
    private static final Validator<UserAccountRequest> userAccountReqValidator
            = new UserAccountRequestValidator();
    private static final AccountUpdateLockHolder accountHolder = new AccountUpdateLockHolder();
    private static final IUserAccountDAO userAccountDAO = new UserAccountDAO(accountHolder);
    public static final IHandler<UserAccountRequest, Account> userAccountRequestIHandler
            = new UserAccountHandler(userAccountDAO, userAccountReqValidator);
    private static final IMoneyTransferDAO moneyTransferDAO = new MoneyTransferDAO(userAccountDAO, accountHolder);
    private static final IHandler<MoneyTransferRequest, BaseResponse> moneyTransferHandler
            = new MoneyTransferHandler(moneyTransferReqValidator, moneyTransferDAO);

    public static IHandler<MoneyTransferRequest, BaseResponse> getMoneyTransferHandler() {
        return moneyTransferHandler;
    }

    public static IHandler<UserAccountRequest, Account> getUserAccountHandler() {
        return userAccountRequestIHandler;
    }
}
