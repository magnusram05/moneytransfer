package com.fintech.apis.model;

import java.util.Objects;

public class User {
    private final long userID;
    private final long accountID;

    public User(long userID, long accountID) {
        this.userID = userID;
        this.accountID = accountID;
    }

    public static Builder newBuilder() {
        return new User.Builder();
    }

    public long getUserID() {
        return userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userID == user.userID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    @Override
    public String toString() {
        return "User{" +
                "userID=" + userID +
                '}';
    }

    public static class Builder {
        private long userID;
        private long accountID;

        public Builder setUserID(long userID) {
            this.userID = userID;
            return this;
        }

        public Builder setAccountID(long accountID) {
            this.accountID = accountID;
            return this;
        }

        public User build() {
            return new User(this.userID, this.accountID);
        }
    }
}
