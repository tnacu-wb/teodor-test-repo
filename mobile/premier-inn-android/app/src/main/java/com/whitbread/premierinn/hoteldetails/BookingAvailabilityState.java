package com.whitbread.premierinn.hoteldetails;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.api.response.booking.BookingAvailability;

import io.reactivex.functions.Function;

@AutoValue
public abstract class BookingAvailabilityState {

    public static final Function<BookingAvailability, BookingAvailabilityState> MAPPER = availability -> {
        if (availability.fullyBooked()) {
            return BookingAvailabilityState.builder().bookingAvailability(availability).action(Action.FULLY_BOOKED).build();
        }
        if (availability.ratePlans() != null && !availability.ratePlans().isEmpty() && !availability.filteredRoomRatePlans().isEmpty()) {
            return BookingAvailabilityState.builder().bookingAvailability(availability).action(Action.SUCCESS).build();
        } else {
            return BookingAvailabilityState.builder().bookingAvailability(availability).action(Action.ERROR).build();
        }
    };

    @Nullable
    public abstract BookingAvailability bookingAvailability();

    public abstract Action action();

    public static Builder builder() {
        return new AutoValue_BookingAvailabilityState.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder bookingAvailability(BookingAvailability bookingAvailability);

        public abstract Builder action(Action action);

        public abstract BookingAvailabilityState build();
    }

    public enum Action {
        LOADING,
        FULLY_BOOKED,
        SUCCESS,
        ERROR
    }
}
