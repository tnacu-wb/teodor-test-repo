package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetailsDto {

  private String provider;
  private boolean offline;
  private BigDecimal amount;
  private CardRequestDto card;
}
