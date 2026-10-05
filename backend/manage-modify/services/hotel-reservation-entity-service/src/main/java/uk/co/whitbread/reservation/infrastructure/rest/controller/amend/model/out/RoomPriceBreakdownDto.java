package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPriceBreakdownDto {

  private BigDecimal totalNetAmount;
  private BigDecimal totalGrossAmount;
  private BigDecimal totalTaxAmount;
  private BigDecimal packageAmount;
  private String packageCode;
  private String currencyCode;
  private List<DailyPriceDto> dailyPrices;
}
