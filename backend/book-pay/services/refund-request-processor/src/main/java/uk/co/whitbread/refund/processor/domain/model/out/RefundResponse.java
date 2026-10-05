package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

  private String requestId;
  private String refundId;
  private String paymentId;
  private boolean refunded;
}
