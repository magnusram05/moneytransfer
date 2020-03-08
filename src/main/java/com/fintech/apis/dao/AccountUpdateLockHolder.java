package com.fintech.apis.dao;

import com.fintech.apis.model.Account;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountUpdateLockHolder {
    private final Map<Account, Lock> accountLockMap = new ConcurrentHashMap<>();

    Lock getLock(Account account) {
        return accountLockMap.computeIfAbsent(account, account1 -> new ReentrantLock());
    }
}
