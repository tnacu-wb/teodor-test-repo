package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchBookingReservationDto {

  private String reservationId;
  private String confirmationId;
  private String cancellationId;
  private SearchBookingStayingGuestDto stayingGuest;

}
