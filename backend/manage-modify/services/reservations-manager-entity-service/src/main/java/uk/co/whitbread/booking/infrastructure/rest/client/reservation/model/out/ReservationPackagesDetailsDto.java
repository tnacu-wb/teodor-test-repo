package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesDetailsDto {

  private String packageCode;
  private String description;
  private Integer totalQuantity;
  private BigDecimal unitPrice;
  private BigDecimal computedPrice;
  private String packageGroup;
}
