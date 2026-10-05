package com.whitbread.premierinn.mealpreferences;

import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BreakfastRadioButtonInput {

    public static final String NONE_CODE = "none";

    public static BreakfastRadioButtonInput create(@NonNull String code, @NonNull String legend, @NonNull CharSequence price,
                                                   @NonNull String description) {
        return builder().code(code).legend(legend).price(price).description(description).isSelected(false).build();
    }

    public abstract String code();

    public abstract String legend();

    public abstract CharSequence price();

    public abstract String description();

    public abstract boolean isSelected();

    public static Builder builder() {
        return new AutoValue_BreakfastRadioButtonInput.Builder()
                .isSelected(false);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder code(String code);

        public abstract Builder legend(String legend);

        public abstract Builder price(CharSequence price);

        public abstract Builder description(String description);

        public abstract Builder isSelected(boolean isSelected);

        public abstract BreakfastRadioButtonInput build();
    }
}
