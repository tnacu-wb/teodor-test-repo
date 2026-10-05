package com.whitbread.premierinn.api.response.booking;


import com.whitbread.premierinn.api.response.InstanceFactory;

import java.util.ArrayList;
import java.util.List;

public class BookingAvailabilityFactory extends InstanceFactory {

    public static BookingAvailability createWithBookingRatePlan(List<BookingRatePlan> bookingRatePlanList) {
        return new AutoValue_BookingAvailability(bookingRatePlanList, true, false, 2, 2, "HOTEL_CODE", "SESSION_ID", "PI", null,
                true, true, false, "", null);
    }

    public static BookingAvailability createWithAvailability(boolean isAvailable) {
        return new AutoValue_BookingAvailability(new ArrayList<>(), isAvailable, false, 2, 2, "HOTEL_CODE", "SESSION_ID", "PI",
                null, true, true, false, "", null);
    }

    public static BookingAvailability fromJson() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-request.json");
    }

    public static BookingAvailability fromJsonWithSemiflex() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-request-semiflex.json");
    }

    public static BookingAvailability fromJsonWithCitytax() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-frankfurt-citytax.json");
    }

    // This returns two rate plans - a flex and a sleepparkfly
    public static BookingAvailability sleepParkFly() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-request-sleepparkfly.json");
    }

    public static BookingAvailability sleepParkFlyOnly() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-request-sleepparkfly-only.json");
    }

    public static BookingAvailability fromJsonAccessibleBathroomAlternatives() {
        return create(BookingAvailability.class, "apiTest/booking-availabilities-response-accessible-bathrooms.json");
    }
}
