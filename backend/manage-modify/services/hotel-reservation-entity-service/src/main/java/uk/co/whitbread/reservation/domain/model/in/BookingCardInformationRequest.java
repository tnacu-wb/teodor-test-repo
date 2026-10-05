package uk.co.whitbread.reservation.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingCardInformationRequest {

  private String bookingReference;
  private String arrivalDate;
  private String bookerLastName;
  private String language;
  private String country;

}
