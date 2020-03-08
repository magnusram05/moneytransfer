package com.fintech.apis.model;

import java.util.Collections;
import java.util.List;

public class BaseResponse {
    private final ResponseCode responseCode;
    private final List<String> errorMessages;
    private final List<String> successMsgs;

    public BaseResponse(ResponseCode errorCode, List<String> errorMessages, List<String> successMsgs) {
        this.responseCode = errorCode;
        this.errorMessages = errorMessages;
        this.successMsgs = successMsgs;
    }

    public static BaseResponse successResponse(List<String> successMessages) {
        return new BaseResponse(ResponseCode.SUCCESS, null, Collections.unmodifiableList(successMessages));
    }

    public static BaseResponse errorResponse(List<String> errorMessages) {
        return new BaseResponse(ResponseCode.ERROR, Collections.unmodifiableList(errorMessages), null);
    }

    public ResponseCode getErrorCode() {
        return responseCode;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public List<String> getSuccessMsgs() {
        return successMsgs;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "responseCode=" + responseCode +
                ", errorMessages=" + errorMessages +
                ", successMsgs=" + successMsgs +
                '}';
    }

    public enum ResponseCode {
        SUCCESS(200), ERROR(503);
        final int responseCode;

        private ResponseCode(int responseCode) {
            this.responseCode = responseCode;
        }
    }

    public static class Builder {
        private ResponseCode errorCode;
        private List<String> errorMessages;
        private List<String> successMsgs;

        public Builder setErrorCode(ResponseCode responseCode) {
            this.errorCode = responseCode;
            return this;
        }

        public Builder setErrorMessages(List<String> errorMessages) {
            this.errorMessages = errorMessages;
            return this;
        }

        public Builder setSuccessMsgs(List<String> successMsgs) {
            this.successMsgs = successMsgs;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(this.errorCode, this.errorMessages, this.successMsgs);
        }
    }
}
