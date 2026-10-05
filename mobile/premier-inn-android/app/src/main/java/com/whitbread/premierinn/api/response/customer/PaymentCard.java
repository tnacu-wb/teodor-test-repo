package com.whitbread.premierinn.api.response.customer;

import android.os.Parcelable;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class PaymentCard implements Parcelable {

    public static final String BUSINESS_CARD = "AT";
    public static final String BUSINESS_CARD_OPERA = "PI";
    public static final String BUSINESS_CARD_OPERA_EURO = "BD";

    @Nullable
    @SerializedName("cardHolderName")
    public abstract String cardHolderName();

    @Nullable
    @SerializedName("cardNumber")
    public abstract String cardNumber();

    @Nullable
    @SerializedName("cardType")
    public abstract String cardType();

    @Nullable
    @SerializedName("expiryDate")
    public abstract String expiryDate();

    @Nullable
    @SerializedName("issueNumber")
    public abstract String issueNumber();

    @Nullable
    @SerializedName("startDate")
    public abstract String startDate();

    public boolean isBusinessCard() {
        return BUSINESS_CARD.equalsIgnoreCase(cardType());
    }

    public static Builder builder() {
        return new AutoValue_PaymentCard.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder cardHolderName(String cardHolderName);

        public abstract Builder cardNumber(String cardNumber);

        public abstract Builder cardType(String cardType);

        public abstract Builder expiryDate(String expiryDate);

        public abstract Builder issueNumber(String issueNumber);

        public abstract Builder startDate(String startDate);

        public abstract PaymentCard build();
    }

    public static TypeAdapter<PaymentCard> typeAdapter(Gson gson) {
        return new AutoValue_PaymentCard.GsonTypeAdapter(gson);
    }
}
