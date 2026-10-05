package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelRoomRateInfo {

  private String hotelCode;
  private String roomType;
  private BigDecimal amount;
  private String rateCode;
  private String classification;
  private String currency;
}