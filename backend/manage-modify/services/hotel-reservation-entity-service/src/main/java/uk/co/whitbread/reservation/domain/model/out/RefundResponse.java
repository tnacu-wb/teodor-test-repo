package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

  private String paymentId;
  private String refundId;
  private Boolean refunded;
  private String requestId;

}
