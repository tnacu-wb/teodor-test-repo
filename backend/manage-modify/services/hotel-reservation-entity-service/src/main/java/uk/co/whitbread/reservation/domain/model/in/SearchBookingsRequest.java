package uk.co.whitbread.reservation.domain.model.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class SearchBookingsRequest {

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
  private String language;
  private String country;
  private int offset;
  private int limit;

}
