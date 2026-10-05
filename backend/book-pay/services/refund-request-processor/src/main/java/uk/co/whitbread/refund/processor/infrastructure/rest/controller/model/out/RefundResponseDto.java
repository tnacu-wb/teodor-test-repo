package uk.co.whitbread.refund.processor.infrastructure.rest.controller.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponseDto {

  private String requestId;
  private String refundId;
  private String paymentId;
  private boolean refunded;
}