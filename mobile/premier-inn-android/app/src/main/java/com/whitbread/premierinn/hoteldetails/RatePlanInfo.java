package com.whitbread.premierinn.hoteldetails;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.availability.HotelInfo;
import com.whitbread.premierinn.api.response.booking.BookingAvailability;
import com.whitbread.premierinn.api.response.booking.BookingRatePlan;

@AutoValue
public abstract class RatePlanInfo {

    public abstract BookingAvailability availability();

    public abstract HotelInfo hotelInfo();

    public abstract BookingRatePlan plan();

    public static Builder builder() {
        return new AutoValue_RatePlanInfo.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder availability(BookingAvailability availability);

        public abstract Builder hotelInfo(HotelInfo hotelInfo);

        public abstract Builder plan(BookingRatePlan plan);

        public abstract RatePlanInfo build();
    }
}
