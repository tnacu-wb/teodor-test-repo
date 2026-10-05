package uk.co.whitbread.basket.domain.model.refund.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefundRequest {

  private String hotelCode;
  private Refund refund;
  private Booking booking;
  private RefundType refundType;

}
