package com.fintech.apis.dao;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.User;

import java.math.BigDecimal;

public interface IUserAccountDAO {

    Account getUserAccount(User user) throws DataValidationException;

    Account getUserAccount(Account account) throws DataValidationException;

    boolean updateAccountBalance(Account updateAccount) throws DataValidationException;

    boolean isValidFromAccount(Account account);

    boolean isValidToAccount(Account account);

    boolean isValidUser(User user);

    BigDecimal currentBalance(Account account, boolean isDebit) throws DataValidationException;
}
