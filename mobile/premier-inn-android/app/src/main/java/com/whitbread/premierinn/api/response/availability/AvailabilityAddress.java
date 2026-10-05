package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;
import androidx.annotation.NonNull;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class AvailabilityAddress implements Parcelable {

    private static final String COMMA_SPACE = ", ";

    @SerializedName("addressline1")
    public abstract String addressLine1();

    @SerializedName("addressline2")
    public abstract String addressLine2();

    @SerializedName("addressline3")
    public abstract String addressLine3();

    @SerializedName("country")
    public abstract String country();

    @SerializedName("postcode")
    public abstract String postcode();

    public String commaSeparatedAddress() {
        StringBuilder stringBuilder = new StringBuilder(addressLine1());
        if (!addressLine2().isEmpty()) {
            stringBuilder.append(COMMA_SPACE).append(addressLine2());
        }
        if (!addressLine3().isEmpty()) {
            stringBuilder.append(COMMA_SPACE).append(addressLine3());
        }
        if (!postcode().isEmpty()) {
            stringBuilder.append(COMMA_SPACE).append(postcode());
        }
        return stringBuilder.toString();
    }

    @NonNull
    public static TypeAdapter<AvailabilityAddress> typeAdapter(Gson gson) {
        return new AutoValue_AvailabilityAddress.GsonTypeAdapter(gson);
    }

    public static Builder builder() {
        return new AutoValue_AvailabilityAddress.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder addressLine1(String line1);
        public abstract Builder addressLine2(String line2);
        public abstract Builder addressLine3(String line3);
        public abstract Builder country(String country);
        public abstract Builder postcode(String postcode);
        public abstract AvailabilityAddress build();
    }
}
