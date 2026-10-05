package uk.co.whitbread.reservation.domain.model.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.co.whitbread.reservation.domain.model.validator.SelfValidation;

@Data
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class FindBookingRequest implements SelfValidation<FindBookingRequest> {

  private String resNo;
  private String arrivalDate;
  private String lastName;
  private String country = "gb";
  private String language = "en";
  private Boolean isOldBooking = false;
}