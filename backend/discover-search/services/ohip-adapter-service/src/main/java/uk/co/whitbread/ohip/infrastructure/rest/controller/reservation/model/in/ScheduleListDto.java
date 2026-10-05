package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

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
  private Integer totalQuantity;
  private BigDecimal computedResvPrice;
  private Integer unitAllowance;
  private String reservationDate;
  private BigDecimal originalUnitPrice;
  private Integer originalUnitAllowance;

}
