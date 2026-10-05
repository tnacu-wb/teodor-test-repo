package uk.co.whitbread.ohip.domain.model.availability.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResultV2 {

  private String hotelId;
  private Boolean available;
  private String currency;
  private BigDecimal minimumRate;
}
