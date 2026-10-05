package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.in.BookerDetails;

public interface HotelAccountOutPort {

  void updateCustomer(BookerDetails bookerDetails, String customerId, String authorization);
}