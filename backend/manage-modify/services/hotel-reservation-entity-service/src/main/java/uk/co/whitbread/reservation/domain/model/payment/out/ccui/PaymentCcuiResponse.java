package uk.co.whitbread.reservation.domain.model.payment.out.ccui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCcuiResponse {
  private String reference;
  private PaymentCcuiStatus status;
}
