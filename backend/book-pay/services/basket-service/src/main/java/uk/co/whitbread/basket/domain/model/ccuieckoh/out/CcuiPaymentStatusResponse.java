package uk.co.whitbread.basket.domain.model.ccuieckoh.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CcuiPaymentStatusResponse {
  private PaymentStatus status;
}
