package com.whitbread.premierinn.api.response.booking;


import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.whitbread.premierinn.api.response.availability.UpsellItem;
import com.whitbread.premierinn.data.hotel.mapper.HotelMappersKt;
import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Deprecated
@AutoValue
public abstract class BookingAvailability implements Parcelable {

    @NonNull
    public static TypeAdapter<BookingAvailability> typeAdapter(Gson gson) {
        return new AutoValue_BookingAvailability.GsonTypeAdapter(gson);
    }

    @Nullable
    @SerializedName("ratePlans")
    public abstract List<BookingRatePlan> ratePlans();

    @SerializedName("available")
    public abstract boolean available();

    @SerializedName("limitedAvailability")
    public abstract boolean limitedAvailability();

    @SerializedName("totalGuests")
    public abstract int totalGuests();

    @SerializedName("totalNights")
    public abstract int totalNights();

    @SerializedName("sessionId")
    @Nullable
    public abstract String bookingId();

    @SerializedName("hotelCode")
    public abstract String hotelCode();

    @SerializedName("hotelBrand")
    public abstract String brand();

    @Nullable
    @SerializedName("notes")
    public abstract List<BookingNote> notes();

    @SerializedName("prepaymentAllowed")
    public abstract boolean prepaymentAllowed();

    @SerializedName("cityTaxForLeisure")
    public abstract boolean cityTaxForLeisure();

    @SerializedName("cityTaxForBusiness")
    public abstract boolean cityTaxForBusiness();

    @Nullable
    @SerializedName("paymentProvider")
    public abstract String paymentProvider();

    @Nullable
    @SerializedName("cnpAuthorisation")
    public abstract CnpAuthorisation cnpAuthorisation();

    public List<BookingRatePlan> filteredRoomRatePlans() {

        List<BookingRatePlan> bookingRatePlans = new ArrayList<>();

        if (ratePlans() == null) {
            return bookingRatePlans;
        }

        // Construct normal rooms
        for (BookingRatePlan bookingRatePlan : ratePlans()) {
            if (Objects.equals(bookingRatePlan.getRateType(), BookingRatePlan.UNKNOWN_RATE)) {
                continue;
            }
            bookingRatePlans.add(bookingRatePlan);
        }
        return bookingRatePlans;
    }

    public List<UpsellItem> upsellItems() {
        List<UpsellItem> upsellItems = new ArrayList<>();
        if (ratePlans() != null && !ratePlans().isEmpty()) {
            for (UpsellItem item : ratePlans().get(0).getUpsellItems()) {
                if (item.foodUpsell()) {
                    upsellItems.add(item);
                }
            }
        }
        return upsellItems;
    }

    public boolean fullyBooked() {
        return !available() || ratePlans() == null || ratePlans().isEmpty();
    }

    public Hotel.Brand hotelBrand() {
        return HotelMappersKt.mapToBrand(brand());
    }
}
