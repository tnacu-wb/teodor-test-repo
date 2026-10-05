package uk.co.whitbread.booking.domain.model.history.out;

import java.util.Map;
import java.util.Set;

public record CiolEligibleBookingsAndHotels(
    Set<String> reservationIds,
    Map<String, String> hotelCountries) {

}
