package com.whitbread.premierinn.api.response.availability;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

import static com.whitbread.premierinn.data.common.Constants.EMPTY_STRING;

@Deprecated // Use com.whitbread.premierinn.data.remote.BookingRule
@AutoValue
public abstract class BookingRule implements Parcelable {

    public static final String AMENDMENT = "AMENDMENT";
    public static final String CANCELLATION = "CANCELLATION";

    @SerializedName("days")
    public abstract int days();

    @Nullable
    @SerializedName("policy")
    public abstract Policy policy();

    @Nullable
    @SerializedName("type")
    public abstract String type();

    @Nullable
    @SerializedName("text")
    public abstract String text();

    public static TypeAdapter<BookingRule> typeAdapter(Gson gson) {
        return new AutoValue_BookingRule.GsonTypeAdapter(gson);
    }

    public boolean isCancellationPolicy() {
        return type() != null && type().equalsIgnoreCase(CANCELLATION);
    }

    public boolean isAmendmentPolicy() {
        return type() != null && type().equalsIgnoreCase(AMENDMENT);
    }

    private String cancellationPolicyText() {
        if (type() != null && type().equalsIgnoreCase(CANCELLATION)) {
            return text() == null ? EMPTY_STRING : text();
        }

        return EMPTY_STRING;
    }

    private String amendmentPolicyText() {
        if (type() != null && type().equalsIgnoreCase(AMENDMENT)) {
            return text() == null ? EMPTY_STRING : text();
        }

        return EMPTY_STRING;
    }

    public String rateBookingRules() {
        return cancellationPolicyText() + amendmentPolicyText();
    }

    public static Builder builder() {
        return new AutoValue_BookingRule.Builder();
    }

    public boolean allowed() {
        return !Policy.NOT_ALLOWED.equals(policy());
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder days(int days);

        public abstract Builder policy(Policy policy);

        public abstract Builder type(String type);

        public abstract Builder text(String text);

        public abstract BookingRule build();
    }

    @Deprecated // Use com.whitbread.premierinn.data.remote.BookingRule.Policy
    public enum Policy {
        @SerializedName("STANDARD_POLICY") STANDARD,
        @SerializedName("NOT_ALLOWED") NOT_ALLOWED,
        @SerializedName("NOT_ALLOWED_WITHIN_X_DAYS_OF_ARRIVAL") NOT_ALLOWED_WITHIN_X_DAYS_OF_ARRIVAL,
        @SerializedName("ONLY_ALLOWED_WITHIN_X_DAYS_OF_BOOKING") ONLY_ALLOWED_WITHIN_X_DAYS_OF_BOOKING
    }
}
