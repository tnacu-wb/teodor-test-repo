package com.whitbread.premierinn.common;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class AddressField implements Parcelable {

    @Nullable
    public abstract String postcode();

    @Nullable
    public abstract String addressLine1();

    @Nullable
    public abstract String addressLine2();

    @Nullable
    public abstract String city();

    @Nullable
    public abstract String companyName();

    public static Builder builder() {
        return new AutoValue_AddressField.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder postcode(String postcode);

        public abstract Builder addressLine1(String addressLine1);

        public abstract Builder addressLine2(String addressLine2);

        public abstract Builder city(String city);

        public abstract Builder companyName(String companyName);

        public abstract AddressField build();
    }
}
