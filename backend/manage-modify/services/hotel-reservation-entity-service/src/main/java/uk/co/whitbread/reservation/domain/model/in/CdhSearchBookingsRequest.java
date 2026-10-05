package uk.co.whitbread.reservation.domain.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdhSearchBookingsRequest {

  private String bookingReference;
  private String bookerLastName;
  private String guestLastName;
  private String bookerPostcode;
  private String hotelId;
  private String bookerEmail;
  private String bookerPhone;
  private String arrivalDateFrom;
  private String arrivalDateTo;
  private String cancellationDate;
  private String companyName;
  private String thirdPartyBookingReferenceNumber;
  private boolean bookingsDatabaseSearch;
  private Integer pageSize = null;
  private String continuationToken = null;
  private Integer pageNumber = null;
}
