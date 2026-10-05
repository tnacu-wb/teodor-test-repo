package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

  private Booking booking;
  private String hotelCode;
  private Refund refund;
  private RefundTypeEnum refundType;

}
