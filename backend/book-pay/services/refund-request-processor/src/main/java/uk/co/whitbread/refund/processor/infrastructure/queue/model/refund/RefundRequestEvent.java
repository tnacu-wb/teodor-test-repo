package uk.co.whitbread.refund.processor.infrastructure.queue.model.refund;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RefundRequestEvent {
  private String itemId; //REFUND#BASKETREFERENCE
  private String basketReference;
  private String paymentId;
  private String hotelCode;
  private Refund refund;
  private Booking booking;
}
