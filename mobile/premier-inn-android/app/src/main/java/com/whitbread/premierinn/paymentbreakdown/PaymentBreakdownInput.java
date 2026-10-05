package com.whitbread.premierinn.paymentbreakdown;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.api.response.booking.BookingPrice;
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom;
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem;

import org.threeten.bp.LocalDate;

import java.util.List;

@AutoValue
public abstract class PaymentBreakdownInput implements Parcelable {

    public abstract boolean isHub();

    public abstract String hotelBrand();

    public abstract int totalGuests();

    public abstract int totalAdults();

    public abstract int totalChildren();

    public abstract int totalNights();

    public abstract LocalDate arrivalDate();

    public abstract List<SummaryBreakdownRoom> summaryBreakdownRooms();

    public abstract BookingPrice donation();

    public abstract BookingPrice totalPrice();

    @Nullable
    public abstract String chosenRateName();

    @Nullable
    public abstract String chosenRateCode();

    public abstract String chosenRateDescription();

    public abstract List<UpsellItem> selectedUpsells();

    public abstract boolean isFreeForChildren();

    public abstract boolean isEmployeeRateSelected();

    @Nullable
    public abstract List<ParcelableExtrasItem> selectedExtras();

    public abstract int totalChildrenBreakfast();

    public static Builder builder() {
        return new AutoValue_PaymentBreakdownInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder isHub(boolean isHub);

        public abstract Builder hotelBrand(String hotelBrand);

        public abstract Builder totalGuests(int totalGuests);

        public abstract Builder totalAdults(int totalAdults);

        public abstract Builder totalChildren(int totalChildren);

        public abstract Builder totalNights(int totalNights);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder summaryBreakdownRooms(List<SummaryBreakdownRoom> summaryBreakdownRooms);

        public abstract Builder donation(BookingPrice donation);

        public abstract Builder totalPrice(BookingPrice totalPrice);

        public abstract Builder chosenRateName(String chosenRateName);

        public abstract Builder chosenRateCode(String chosenRateCode);

        public abstract Builder chosenRateDescription(String chosenRateDescription);

        public abstract Builder selectedUpsells(List<UpsellItem> upsellItems);

        public abstract Builder isFreeForChildren(boolean isFreeForChildren);

        public abstract Builder isEmployeeRateSelected(boolean isEmployeeRateEnabled);

        public abstract Builder selectedExtras(List<ParcelableExtrasItem> selectedExtras);

        public abstract Builder totalChildrenBreakfast(int totalChildrenBreakfast);

        public abstract PaymentBreakdownInput build();
    }
}
