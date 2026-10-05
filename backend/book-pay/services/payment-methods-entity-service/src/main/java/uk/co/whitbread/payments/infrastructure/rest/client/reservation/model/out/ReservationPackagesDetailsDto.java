package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackagesDetailsDto {

  private BigDecimal computedPrice;
  private String description;
  private String packageCode;
  private Integer totalQuantity;
  private BigDecimal unitPrice;
  private BigDecimal vatTax;

}