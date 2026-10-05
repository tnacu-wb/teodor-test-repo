package com.whitbread.premierinn.api.response;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.booking.BookingPrice;

@AutoValue
public abstract class CardInfo implements Parcelable {

    private static final String BUSINESS_CARD = "AT";

    @NonNull
    public static TypeAdapter<CardInfo> typeAdapter(Gson gson) {
        return new AutoValue_CardInfo.GsonTypeAdapter(gson);
    }

    @SerializedName("cardType")
    public abstract String cardType();

    @Nullable
    @SerializedName("cardLegend")
    public abstract String cardLegend();

    @SerializedName("startDateRequired")
    public abstract boolean startDateRequired();

    @SerializedName("issueNumberRequired")
    public abstract boolean issueNumberRequired();

    @SerializedName("cardFeeApplies")
    public abstract boolean cardFeeApplies();

    @Nullable
    @SerializedName("cardFeeAmount")
    public abstract BookingPrice cardFeeAmount();

    public boolean isBusinessCard() {
        return cardType().equalsIgnoreCase(BUSINESS_CARD);
    }

    public abstract CardInfo.Builder toBuilder();

    public static CardInfo.Builder builder() {
        return new AutoValue_CardInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder cardType(String cardType);
        public abstract Builder cardLegend(String cardLegend);
        public abstract Builder startDateRequired(boolean startDateRequired);
        public abstract Builder issueNumberRequired(boolean issueNumberRequired);
        public abstract Builder cardFeeApplies(boolean cardFeeApplies);
        public abstract Builder cardFeeAmount(BookingPrice cardFeeAmount);
        public abstract CardInfo build();
    }
}
