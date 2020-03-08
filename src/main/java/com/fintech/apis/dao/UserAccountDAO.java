package com.fintech.apis.dao;

import com.fintech.apis.exception.DataValidationException;
import com.fintech.apis.model.Account;
import com.fintech.apis.model.User;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.fintech.apis.util.AppConstants.BALANCE_UPDATE_FAILED;
import static com.fintech.apis.util.AppConstants.USER_ACCOUNT_NOT_FOUND;

public class UserAccountDAO implements IUserAccountDAO {
    private final Set<User> users = new HashSet<>();
    private final Set<Account> accounts = new HashSet<>();
    private final AccountUpdateLockHolder accountUpdateLockHolder;

    public UserAccountDAO(AccountUpdateLockHolder lockHolder) {
        Account account1 = new Account(1, 1, BigDecimal.valueOf(1000));
        Account account2 = new Account(2, 2, BigDecimal.valueOf(2000));
        Account account3 = new Account(3, 3, BigDecimal.valueOf(3000));
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);

        User user1 = new User(1, 1);
        User user2 = new User(2, 2);
        User user3 = new User(3, 3);
        users.add(user1);
        users.add(user2);
        users.add(user3);

        this.accountUpdateLockHolder = lockHolder;
    }

    @Override
    public Account getUserAccount(User user) throws DataValidationException {
        Account account = this.accounts.stream().filter(account1 -> account1.getUserID() == user.getUserID()).findFirst().orElse(null);
        if (account != null)
            return Account.newBuilder()
                    .setCurrentBalance(account.getCurrentBalance())
                    .setUserID(account.getUserID())
                    .setAccountID(account.getAccountID()).build();
        else
            throw new DataValidationException(USER_ACCOUNT_NOT_FOUND);
    }

    @Override
    public Account getUserAccount(Account requestAccount) throws DataValidationException {
        Optional<Account> account = this.accounts.stream()
                .filter(account1 -> account1.equals(requestAccount))
                .findFirst();
        if (account.isPresent()) {
            return Account.newBuilder()
                    .setAccountID(account.get().getAccountID())
                    .setUserID(account.get().getUserID())
                    .setCurrentBalance(account.get().getCurrentBalance())
                    .build();
        }
        throw new DataValidationException(USER_ACCOUNT_NOT_FOUND);
    }

    @Override
    public boolean updateAccountBalance(Account updateAccount) throws DataValidationException {
        if (updateAccount == null)
            return false;
        Lock lock = null;
        try {
            lock = accountUpdateLockHolder.getLock(updateAccount);
            lock.tryLock(4000, TimeUnit.MILLISECONDS);

            Account accountToUpdate = null;
            Iterator<Account> iterator = this.accounts.iterator();
            while (iterator.hasNext()) {
                Account cAccount = iterator.next();
                if (updateAccount.equals(cAccount)) {
                    accountToUpdate = Account.newBuilder()
                            .setUserID(cAccount.getUserID())
                            .setAccountID(cAccount.getAccountID())
                            .setCurrentBalance(updateAccount.getCurrentBalance())
                            .build();
                    iterator.remove();
                    break;
                }
            }
            if (accountToUpdate != null) {
                return this.accounts.add(accountToUpdate);
            }
            return false;
        } catch (Exception ex) {
            throw new DataValidationException(BALANCE_UPDATE_FAILED, ex);
        } finally {
            if (lock != null)
                lock.unlock();
        }
    }

    @Override
    public boolean isValidFromAccount(Account accountToValidate) {
        return accountToValidate != null && this.accounts.stream()
                .anyMatch(account1 -> account1.equals(accountToValidate) && account1.getUserID() == accountToValidate.getUserID());
    }

    @Override
    public boolean isValidToAccount(Account accountToValidate) {
        return accountToValidate != null && this.accounts.stream()
                .anyMatch(account1 -> account1.equals(accountToValidate));
    }

    @Override
    public boolean isValidUser(User user) {
        if (user == null)
            return false;
        return this.users.contains(user);
    }

    @Override
    public BigDecimal currentBalance(Account account, boolean isDebit) throws DataValidationException {
        if ((isDebit && !isValidFromAccount(account)) || (!isValidToAccount(account)))
            throw new DataValidationException(USER_ACCOUNT_NOT_FOUND);
        return BigDecimal.valueOf(this.accounts.stream().filter(account1 -> account1.equals(account)).findFirst().map(Account::getCurrentBalance).get().doubleValue());
    }
}
