package com.whitbread.premierinn.common.forms;

import com.google.auto.value.AutoValue;

import java.util.EnumSet;

@AutoValue
public abstract class Input {

    public abstract int id();

    public abstract Object value();

    public abstract EnumSet<ValidationType> validationTypes();

    public static Builder builder() {
        return new AutoValue_Input.Builder();
    }

    public enum ValidationType {
        REQUIRED, CARD_NUMBER, EMAIL, FIRST_NAME, LAST_NAME, POSTCODE_IF_UK, CARD_DATE, PHONE,
        PASSWORD, PASSWORDS_DONT_MATCH, CHECKED, NONE, CVV, GERMAN_POSTCODE, UK_POSTCODE, COMPANY_NAME, COMPANY_NAME_SPECIAL
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder id(int id);

        public abstract Builder value(Object text);

        public abstract Builder validationTypes(EnumSet<ValidationType> validationTypes);

        public abstract Input build();
    }
}
