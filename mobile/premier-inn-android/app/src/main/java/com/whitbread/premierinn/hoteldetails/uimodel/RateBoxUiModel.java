package com.whitbread.premierinn.hoteldetails.uimodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.domain.hotel.entity.RoomVariant;

@AutoValue
public abstract class RateBoxUiModel {

    public abstract String rateType();

    public abstract String pmsRoomType();

    @Nullable
    public abstract String title();

    @Nullable
    public abstract String description();

    @Nullable
    public abstract String price();
    @Nullable
    public abstract String formattedBaseRate();
    @Nullable
    public abstract String promotionCode();
    @Nullable
    public abstract String promoTag();

    public abstract boolean hasCityTax();

    public abstract RoomVariant roomVariant();

    public abstract int nights();

    public abstract int rooms();

    public abstract boolean alternativeType();

    public abstract boolean alternateRoomSelectionAvailable();

    @NonNull
    public abstract String rateClassification();

    public static Builder builder() {
        return new AutoValue_RateBoxUiModel.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {

        public abstract Builder rateType(String rateType);

        public abstract Builder pmsRoomType(String pmsRoomType);

        public abstract Builder title(String title);

        public abstract Builder description(String description);

        public abstract Builder price(String price);

        public abstract Builder formattedBaseRate(String formattedBaseRate);

        public abstract Builder promotionCode(String promotionCode);

        public abstract Builder promoTag(String promoTag);

        public abstract Builder roomVariant(RoomVariant roomVariant);

        public abstract Builder hasCityTax(boolean hasCityTax);

        public abstract Builder nights(int nights);

        public abstract Builder alternativeType(boolean value);

        public abstract Builder alternateRoomSelectionAvailable(boolean alternateRoomSelectionAvailable);

        public abstract Builder rooms(int rooms);

        public abstract Builder rateClassification(String rateClassification);

        public abstract RateBoxUiModel build();
    }
}