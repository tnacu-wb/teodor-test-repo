package uk.co.whitbread.refund.processor.domain.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequest {

  private String itemId; //REFUND#BASKETREFERENCE
  private String basketReference;
  private String paymentId;
  private String hotelCode;
  private Refund refund;
  private Booking booking;

}
