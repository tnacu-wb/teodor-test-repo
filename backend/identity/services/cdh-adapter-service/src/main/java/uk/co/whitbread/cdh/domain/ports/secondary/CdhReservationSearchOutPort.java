package uk.co.whitbread.cdh.domain.ports.secondary;

import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;

public interface CdhReservationSearchOutPort {

  ReservationSearch getReservationSearch(ReservationSearchCriteria reservationSearchCriteria);

  ReservationSearch getReservationById(String reservationId);

  ReservationInvoicesResponse getBookingInvoices(String bookingReference, String accessedBy,
      String accessContext);

}
