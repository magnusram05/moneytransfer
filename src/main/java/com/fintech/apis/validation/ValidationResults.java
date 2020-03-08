package com.fintech.apis.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResults {
    private final boolean isValid;
    private final List<String> validationResults;

    private ValidationResults(boolean isValid, List<String> validationResults) {
        this.isValid = isValid;
        this.validationResults = validationResults;
    }

    public static Builder newBuilder() {
        return new ValidationResults.Builder();
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getValidationResults() {
        return Collections.unmodifiableList(validationResults);
    }

    public static class Builder {
        private boolean isValid;
        private List<String> validationResults = new ArrayList<>();

        public Builder setValid(boolean valid) {
            isValid = valid;
            return this;
        }

        public Builder addValidationResults(String validationResult) {
            this.validationResults.add(validationResult);
            return this;
        }

        public ValidationResults build() {
            return new ValidationResults(isValid, validationResults);
        }
    }
}
