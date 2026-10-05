package com.whitbread.premierinn.mybookings.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.data.booking.entity.BookingEntity;

@AutoValue
public abstract class MyBookingOnClickAdapterOutput {

    public abstract int adapterPosition();

    public abstract Type type();

    @Nullable
    public abstract BookingEntity entity();

    public static MyBookingOnClickAdapterOutput create(int adapterPosition, @NonNull Type type, @NonNull BookingEntity entity) {
        return new AutoValue_MyBookingOnClickAdapterOutput(adapterPosition, type, entity);
    }

    public static MyBookingOnClickAdapterOutput create(int adapterPosition, @NonNull Type type) {
        return new AutoValue_MyBookingOnClickAdapterOutput(adapterPosition, type, null);
    }

    public enum Type {
        PLAN_TRIP,
        BOOKING_DETAILS
    }
}
