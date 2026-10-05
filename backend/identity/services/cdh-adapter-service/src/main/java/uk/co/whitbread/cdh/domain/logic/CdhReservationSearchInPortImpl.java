package uk.co.whitbread.cdh.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.ports.primary.CdhReservationSearchInPort;
import uk.co.whitbread.cdh.domain.ports.secondary.CdhReservationSearchOutPort;

@Slf4j
@RequiredArgsConstructor
@Component
public class CdhReservationSearchInPortImpl implements CdhReservationSearchInPort {

  private final CdhReservationSearchOutPort cdhReservationSearchOutPort;

  @Override
  public ReservationSearch getReservationSearch(ReservationSearchCriteria reservationSearchCriteria) {
    return this.cdhReservationSearchOutPort.getReservationSearch(reservationSearchCriteria);
  }

  @Override
  public ReservationSearch getReservationById(String reservationId) {
    return cdhReservationSearchOutPort.getReservationById(reservationId);
  }

  @Override
  public ReservationInvoicesResponse getBookingInvoices(String bookingReference, String accessedBy,
      String accessContext) {
    return cdhReservationSearchOutPort.getBookingInvoices(bookingReference, accessedBy,
        accessContext);
  }

}
