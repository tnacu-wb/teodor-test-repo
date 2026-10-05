package uk.co.whitbread.availabilitycacheservice.domain.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommonHotel {

  private String hotelCode;

  private Boolean available;

  private Boolean limitedAvailability;

  private Boolean hasMlosRestriction;
}
