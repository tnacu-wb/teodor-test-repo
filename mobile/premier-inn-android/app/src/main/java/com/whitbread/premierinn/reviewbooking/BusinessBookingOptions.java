package com.whitbread.premierinn.reviewbooking;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.booking.BookingPrice;

@AutoValue
public abstract class BusinessBookingOptions implements Parcelable {

    public abstract boolean alcoholAllowed();

    @Nullable
    public abstract String atosPassword();

    @Nullable
    public abstract String breakfastCode();

    public abstract boolean carParkingAllowed();

    public abstract boolean cardNotPresentAuth();

    @Nullable
    public abstract String customerReference();

    @Nullable
    public abstract BookingPrice dinnerAllowance();

    @Nullable
    public abstract String purchaseOrder();

    public abstract boolean wifiAccessAllowed();

    public abstract BusinessBookingOptions.Builder toBuilder();

    public static BusinessBookingOptions.Builder builder() {
        return new AutoValue_BusinessBookingOptions.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract BusinessBookingOptions.Builder alcoholAllowed(boolean alcoholAllowed);

        public abstract BusinessBookingOptions.Builder atosPassword(String password);

        public abstract BusinessBookingOptions.Builder breakfastCode(String breakfastCode);

        public abstract BusinessBookingOptions.Builder carParkingAllowed(boolean parkingAllowed);

        public abstract BusinessBookingOptions.Builder cardNotPresentAuth(boolean cnpAuth);

        public abstract BusinessBookingOptions.Builder customerReference(String ref);

        public abstract BusinessBookingOptions.Builder dinnerAllowance(BookingPrice allowance);

        public abstract BusinessBookingOptions.Builder purchaseOrder(String purchaseOrder);

        public abstract BusinessBookingOptions.Builder wifiAccessAllowed(boolean wifiAccessAllowed);

        public abstract BusinessBookingOptions build();
    }

}