package uk.co.whitbread.kiosk.domain.model.checkin.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleList {

  private String consumptionDate;
  private BigDecimal unitPrice;
  private int totalQuantity;
  private float computedResvPrice;
  private int unitAllowance;
  private String reservationDate;
  private float originalUnitPrice;
  private int originalUnitAllowance;

}
