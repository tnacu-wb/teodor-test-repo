package uk.co.whitbread.kiosk.domain.model.checkin.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {

  private String provider;
  private boolean offline;
  private BigDecimal amount;
  private CardRequest card;

}
