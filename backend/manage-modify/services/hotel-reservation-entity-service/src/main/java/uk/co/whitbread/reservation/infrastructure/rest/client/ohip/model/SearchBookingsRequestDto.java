package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchBookingsRequestDto {

  private String bookingReference;
  private String bookerLastName;
  private String guestLastName;
  private String bookerPostcode;
  private String hotelId;
  private String bookerEmail;
  private String bookerPhone;
  private String arrivalDate;
  private String cancellationDate;
  private String companyName;
  private String thirdPartyBookingReferenceNumber;
  private int offset;
  private int limit;

}
