package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResultV2Dto {

  private String hotelId;
  private Boolean available;
  private String currency;
  private BigDecimal minimumRate;
}
