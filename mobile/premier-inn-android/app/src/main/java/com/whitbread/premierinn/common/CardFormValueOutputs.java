package com.whitbread.premierinn.common;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CardFormValueOutputs {

    public abstract String cardNumber();

    public abstract String startDate();

    public abstract String expiryDate();

    public abstract String issueNumber();

    public abstract String cardHolderName();

    public static CardFormValueOutputs.Builder builder() {
        return new AutoValue_CardFormValueOutputs.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract CardFormValueOutputs.Builder cardNumber(String cardNumber);
        public abstract CardFormValueOutputs.Builder startDate(String startDate);
        public abstract CardFormValueOutputs.Builder expiryDate(String expiryDate);
        public abstract CardFormValueOutputs.Builder issueNumber(String issueNumber);
        public abstract CardFormValueOutputs.Builder cardHolderName(String cardHolderName);
        public abstract CardFormValueOutputs build();
    }
}
