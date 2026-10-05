package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ReservationPackagesDetailsResponse {

  private String packageCode;

  private String description;

  private BigDecimal unitPrice;

  private Integer totalQuantity;

  private BigDecimal computedPrice;
  private BigDecimal grossPrice;
  private BigDecimal vatTax;
  private String startDate;
  private String endDate;
  private String packageGroup;
}
