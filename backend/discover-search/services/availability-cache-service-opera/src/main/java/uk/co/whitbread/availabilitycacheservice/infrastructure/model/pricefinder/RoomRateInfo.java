package uk.co.whitbread.availabilitycacheservice.infrastructure.model.pricefinder;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomRateInfo {

  private String hotelCode;
  private String roomType;
  private BigDecimal amount;
  private String rateCode;
  private String classification;
  private String currency;
}