package com.fintech.apis.dao;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.User;
import com.fintech.apis.validation.ValidationResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.fintech.apis.util.AppConstants.*;

public class MoneyTransferDAO implements IMoneyTransferDAO {
    private static final Logger LOGGER = LogManager.getLogger(MoneyTransferDAO.class);

    private final IUserAccountDAO userDAO;
    private final AccountUpdateLockHolder accountUpdateLockHolder;

    public MoneyTransferDAO(IUserAccountDAO userDAO, AccountUpdateLockHolder lockHolder) {
        this.userDAO = userDAO;
        this.accountUpdateLockHolder = lockHolder;
    }

    @Override
    public boolean transfer(User user, Account fromAccount, Account toAccount, BigDecimal amount) throws DataValidationException {
        ValidationResults results = this.validate(user, fromAccount, toAccount, amount);
        if (!results.isValid()) {
            LOGGER.error("Data access validation failed: {}", results.getValidationResults());
            throw new DataValidationException(results.getValidationResults().stream().collect(Collectors.joining(",")));
        }
        Lock debitLock = this.accountUpdateLockHolder.getLock(fromAccount);
        Lock creditLock = this.accountUpdateLockHolder.getLock(toAccount);
        BigDecimal debitAccountInitialBalance = null;
        BigDecimal creditAccountInitialBalance = null;
        try {
            if (debitLock.tryLock(4000, TimeUnit.MILLISECONDS)) {
                try {
                    debitAccountInitialBalance = this.userDAO.currentBalance(fromAccount, true);
                    if (creditLock.tryLock(2000, TimeUnit.MILLISECONDS)) {
                        creditAccountInitialBalance = this.userDAO.currentBalance(toAccount, false);
                        this.debit(user, fromAccount, amount);
                        this.credit(user, toAccount, amount);
                    } else {
                        LOGGER.error("Unable to acquire lock on credit account for {}", user);
                        throw new DataValidationException(TRANSFER_NOT_INIT + ", Unable to acquire lock on credit account");
                    }
                } catch (InterruptedException ex) {
                    LOGGER.error("InterruptedException thrown while trying to acquire lock on credit account for user {}", user, ex);
                } catch (Exception ex) {
                    LOGGER.error("Exception thrown while crediting the amount for user {}", user, ex);
                    reverseDebit(debitAccountInitialBalance, creditAccountInitialBalance, user, fromAccount, toAccount);
                } finally {
                    creditLock.unlock();
                }
            } else {
                LOGGER.error("Unable to acquire lock on debit account for {}", user);
                throw new DataValidationException(TRANSFER_NOT_INIT + ", Unable to acquire lock on debit account");
            }
        } catch (InterruptedException ex) {
            LOGGER.error("Failed to initiated money transfer initiated by the user {}", user, ex);
            throw new DataValidationException(TRANSFER_NOT_INIT + "Unable to acquire lock on debit account", ex);
        } catch (Exception ex) {
            LOGGER.error("Failed to initiated money transfer initiated by the user", user, ex);
            reverseDebit(debitAccountInitialBalance, creditAccountInitialBalance, user, fromAccount, toAccount);
            throw ex;
        } finally {
            debitLock.unlock();
        }
        return true;
    }

    @Override
    public boolean credit(User user, Account account, BigDecimal amount) throws DataValidationException {
        if (this.userDAO.isValidToAccount(account)) {
            Account creditAccount = this.userDAO.getUserAccount(account);
            BigDecimal newBalance = creditAccount.getCurrentBalance().add(amount);
            Account updatedAccount = Account.newBuilder()
                    .setUserID(creditAccount.getUserID())
                    .setAccountID(creditAccount.getAccountID())
                    .setCurrentBalance(newBalance).build();
            this.userDAO.updateAccountBalance(updatedAccount);
        } else {
            throw new DataValidationException(INVALID_CREDIT_ACCOUNT);
        }
        return true;
    }

    @Override
    public boolean debit(User user, Account account, BigDecimal amount) throws DataValidationException {
        Account debitAccount = this.userDAO.getUserAccount(user);
        if (debitAccount != null && this.userDAO.isValidFromAccount(debitAccount)) {
            if (debitAccount.getCurrentBalance().compareTo(amount) >= 0) {
                BigDecimal newBalance = debitAccount.getCurrentBalance().subtract(amount);
                Account updatedAccount = Account.newBuilder().setAccountID(account.getAccountID()).setUserID(user.getUserID()).setCurrentBalance(newBalance).build();
                this.userDAO.updateAccountBalance(updatedAccount);
                return true;
            } else {
                throw new DataValidationException(INSUFFICIENT_BALANCE);
            }
        } else {
            throw new DataValidationException(INVALID_DEBIT_ACCOUNT);
        }
    }

    private void reverseDebit(BigDecimal creditAccountInitialBalance, BigDecimal debitAccountInitialBalance, User user, Account fromAccount, Account toAccount) throws DataValidationException {
        if (creditAccountInitialBalance != null && this.userDAO.currentBalance(toAccount, false).compareTo(creditAccountInitialBalance) == 0) {
            LOGGER.info("Amount not credited for the money transfer initiated by the user {}", user);
            if (debitAccountInitialBalance != null && this.userDAO.currentBalance(fromAccount, true).compareTo(debitAccountInitialBalance) != 0) {
                LOGGER.info("Reversing the debit for the transaction initiated by the user {}", user);
                this.userDAO.updateAccountBalance(Account.newBuilder().setAccountID(fromAccount.getAccountID()).setCurrentBalance(debitAccountInitialBalance).build());
            }
        }
    }

    private ValidationResults validate(User user, Account fromAccount, Account toAccount, BigDecimal amountToTransfer) throws DataValidationException {
        ValidationResults.Builder builder = ValidationResults.newBuilder();
        builder.setValid(true);
        if (!this.userDAO.isValidUser(user)) {
            builder.addValidationResults(INVALID_USER);
            builder.setValid(false);
        }
        if (!this.userDAO.isValidFromAccount(fromAccount)) {
            builder.addValidationResults(INVALID_DEBIT_ACCOUNT);
            builder.setValid(false);
        } else if (this.userDAO.getUserAccount(fromAccount).getCurrentBalance().compareTo(amountToTransfer) < 0) {
            builder.addValidationResults(INSUFFICIENT_BALANCE);
            builder.setValid(false);
        }
        if (!this.userDAO.isValidToAccount(toAccount)) {
            builder.addValidationResults(INVALID_CREDIT_ACCOUNT);
            builder.setValid(false);
        }

        return builder.build();
    }
}

