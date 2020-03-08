package com.fintech.apis.dao;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.User;

import java.math.BigDecimal;

public interface IMoneyTransferDAO {
    boolean transfer(User user, Account fromAccount, Account toAccount, BigDecimal amount) throws DataValidationException;

    boolean credit(User user, Account account, BigDecimal amount) throws DataValidationException;

    boolean debit(User user, Account account, BigDecimal amount) throws DataValidationException;
}
