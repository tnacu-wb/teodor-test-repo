package com.whitbread.premierinn.common.analytics;

import com.google.auto.value.AutoValue;

import java.util.List;

import androidx.annotation.Nullable;


@AutoValue
public abstract class ProductValues {

    private static final String FIELD_DELIMITER = ";";
    private static final String MERCHANDISING_DELIMITER = "|";

    public static final int UNAVAILABLE = -1;
    public static final int EMPTY = 0;

    public abstract String category();

    public abstract String product();

    public abstract int quantity();

    public abstract float totalPrice();

    @Nullable
    public abstract List<String> events();

    @Nullable
    public abstract List<String> evars();

    public static ProductValues.Builder builder() {
        return new AutoValue_ProductValues.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder category(String category);

        public abstract Builder product(String product);

        public abstract Builder quantity(int quantity);

        public abstract Builder totalPrice(float totalPrice);

        public abstract Builder events(List<String> events);

        public abstract Builder evars(List<String> evars);

        public abstract ProductValues build();
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append(category())
                .append(FIELD_DELIMITER)
                .append(product());

        if (quantity() != UNAVAILABLE) {
            builder.append(FIELD_DELIMITER)
                    .append(quantity() != EMPTY ? quantity() : "");
        }

        if (totalPrice() != UNAVAILABLE) {
            builder.append(FIELD_DELIMITER)
                    .append(totalPrice() != EMPTY ? totalPrice() : "");
        }

        if (events() != null) {
            builder.append(FIELD_DELIMITER);

            for (int event = 0; event < events().size(); event++) {

                builder.append(events().get(event));

                if (event != events().size() - 1) {
                    builder.append(MERCHANDISING_DELIMITER);
                }
            }
        }

        if (evars() != null) {
            builder.append(FIELD_DELIMITER);

            for (int evar = 0; evar < evars().size(); evar++) {

                builder.append(evars().get(evar));

                if (evar != evars().size() - 1) {
                    builder.append(MERCHANDISING_DELIMITER);
                }
            }
        }

        return builder.toString();
    }
}