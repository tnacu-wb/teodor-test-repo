package uk.co.whitbread.ohip.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SearchBookingReservation {

  private String reservationId;
  private String confirmationId;
  private String cancellationId;
  private SearchBookingStayingGuest stayingGuest;
}
