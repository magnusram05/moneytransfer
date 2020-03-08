package com.fintech.apis.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Account {
    private final long accountID;
    private final long userID;
    private final BigDecimal currentBalance;

    public Account(long accountID, long userID, BigDecimal currentBalance) {
        this.accountID = accountID;
        this.userID = userID;
        this.currentBalance = currentBalance;
    }

    public static Builder newBuilder() {
        return new Account.Builder();
    }

    public long getAccountID() {
        return accountID;
    }

    public long getUserID() {
        return userID;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountID == account.accountID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountID);
    }

    public static class Builder {
        private long accountID;
        private long userID;
        private BigDecimal currentBalance;

        public Builder setAccountID(long accountID) {
            this.accountID = accountID;
            return this;
        }

        public Builder setUserID(long userID) {
            this.userID = userID;
            return this;
        }

        public Builder setCurrentBalance(BigDecimal currentBalance) {
            this.currentBalance = currentBalance;
            return this;
        }

        public Account build() {
            return new Account(this.accountID, this.userID, this.currentBalance);
        }
    }
}
