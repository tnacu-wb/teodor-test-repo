package uk.co.whitbread.basket.infrastructure.queue.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.refund.in.Booking;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequestEvent {
  private String itemId;
  private String basketReference;
  private String paymentId;
  private String hotelCode;
  private Refund refund;
  private Booking booking;
}
