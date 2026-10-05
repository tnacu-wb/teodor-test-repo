package uk.co.whitbread.booking.domain.model.information.out;

import java.util.Set;

public record UpcomingBookings(String checkInTime, String checkOutTime,
                               Set<BookingPackagesDetails> extrasItems, String email) {

}
