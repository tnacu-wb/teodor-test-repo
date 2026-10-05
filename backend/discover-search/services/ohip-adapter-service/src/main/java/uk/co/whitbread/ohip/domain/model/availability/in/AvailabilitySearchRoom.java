package uk.co.whitbread.ohip.domain.model.availability.in;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class AvailabilitySearchRoom {

  private String roomType;
  private Integer adultsNumber;
  private Integer childrenNumber;
  private Boolean cotRequired;

}
