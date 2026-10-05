package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleListDto {

  private String consumptionDate;
  private BigDecimal unitPrice;
  private int totalQuantity;
  private float computedResvPrice;
  private int unitAllowance;
  private String reservationDate;
  private float originalUnitPrice;
  private int originalUnitAllowance;

}
