package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class StayPriceDto {

  private BigDecimal amount;
  private String currency;
}
