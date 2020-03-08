package com.fintech.apis.model;

public class UserAccountRequest {
    private final long userID;
    private final long accountID;

    public UserAccountRequest(long userID, long accountID) {
        this.userID = userID;
        this.accountID = accountID;
    }

    public long getUserID() {
        return userID;
    }

    public long getAccountID() {
        return accountID;
    }
}
