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
public class RoomRateInfo {

  String ratePlan;
  String roomType;
  String currency;
  BigDecimal totalPrice;

}
