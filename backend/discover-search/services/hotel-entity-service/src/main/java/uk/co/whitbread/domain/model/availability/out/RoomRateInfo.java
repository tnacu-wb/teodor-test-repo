package uk.co.whitbread.domain.model.availability.out;

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

  private String ratePlan;
  private String roomType;
  private String currency;
  private BigDecimal totalPrice;

}
