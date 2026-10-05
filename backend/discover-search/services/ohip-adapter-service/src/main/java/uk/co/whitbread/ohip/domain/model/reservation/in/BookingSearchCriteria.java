package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@ToString
public class BookingSearchCriteria {

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
