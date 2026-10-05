package uk.co.whitbread.basket.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReservationPackagesDetails {

  private BigDecimal computedPrice;
  private String description;
  private String packageCode;
  private Integer totalQuantity;
  private BigDecimal unitPrice;
  private BigDecimal vatTax;
}
