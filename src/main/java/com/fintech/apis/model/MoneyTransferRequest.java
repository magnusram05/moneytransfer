package com.fintech.apis.model;

import java.math.BigDecimal;

public class MoneyTransferRequest {
    private final long userID;
    private final long fromAccount;
    private final long toAccount;
    private final BigDecimal amountToTransfer;

    public MoneyTransferRequest(long userID, long fromAccount, long toAccount, String amountToTransfer) {
        this.userID = userID;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amountToTransfer = BigDecimal.valueOf(Double.parseDouble(amountToTransfer));
    }

    public static Builder newBuilder() {
        return new MoneyTransferRequest.Builder();
    }

    public long getFromAccount() {
        return fromAccount;
    }

    public long getToAccount() {
        return toAccount;
    }

    public BigDecimal getAmountToTransfer() {
        return amountToTransfer;
    }

    public long getUserID() {
        return userID;
    }

    public static class Builder {
        private long userID;
        private long fromAccount;
        private long toAccount;
        private String amountToTransfer;

        public Builder setUserID(long userID) {
            this.userID = userID;
            return this;
        }

        public Builder setFromAccount(long fromAccount) {
            this.fromAccount = fromAccount;
            return this;
        }

        public Builder setToAccount(long toAccount) {
            this.toAccount = toAccount;
            return this;
        }

        public Builder setAmountToTransfer(String amountToTransfer) {
            this.amountToTransfer = amountToTransfer;
            return this;
        }

        public MoneyTransferRequest build() {
            return new MoneyTransferRequest(this.userID, this.fromAccount, this.toAccount, this.amountToTransfer);
        }
    }
}
