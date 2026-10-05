package com.whitbread.premierinn.common.managebooking;

import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.whitbread.premierinn.amend.ParcelableRoomOperaAmend;

import org.threeten.bp.LocalDate;

import java.util.List;

@AutoValue
public abstract class ManageBookingInput implements Parcelable {

    public abstract String hotelCode();

    public abstract String bookingReference();

    @Nullable
    public abstract String selectedRatePlan();

    public abstract String surname();

    public abstract LocalDate arrivalDate();
    public abstract LocalDate departureDate();

    public abstract Boolean amendable();

    public abstract Boolean cancellable();

    public abstract Boolean isEmployeeBooking();

    public abstract Boolean isBusinessBooking();

    public abstract float dinnerAllowance();

    @Nullable
    public abstract List<ParcelableRoomOperaAmend> listOfRooms();

    public static Builder builder() {
        return new AutoValue_ManageBookingInput.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder hotelCode(String hotelCode);

        public abstract Builder bookingReference(String bookingReference);
        public abstract Builder selectedRatePlan(String selectedRatePlan);

        public abstract Builder listOfRooms(List<ParcelableRoomOperaAmend> parcelableRoomList);

        public abstract Builder surname(String surname);

        public abstract Builder arrivalDate(LocalDate arrivalDate);

        public abstract Builder departureDate(LocalDate departureDate);

        public abstract Builder amendable(Boolean amendable);

        public abstract Builder cancellable(Boolean cancellable);

        public abstract Builder isEmployeeBooking(Boolean isEmployeeBooking);


        public abstract Builder isBusinessBooking(Boolean isBusinessBooking);

        public abstract Builder dinnerAllowance(float dinnerAllowance);

        public abstract ManageBookingInput build();
    }
}