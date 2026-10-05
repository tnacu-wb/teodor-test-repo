package uk.co.whitbread.domain.model.availability.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPriceBreakdown {

  private BigDecimal totalNetAmount;
  private BigDecimal totalGrossAmount;
  private BigDecimal totalTaxAmount;
  private BigDecimal totalCityTaxAmount;
  private BigDecimal totalRoomNetAmount;
  private BigDecimal packageAmount;
  private BigDecimal baseRateAmount;
  private BigDecimal effectiveRateAmount;
  private String packageCode;
  private String currencyCode;
  @Singular
  private List<DailyPrice> dailyPrices;
}
