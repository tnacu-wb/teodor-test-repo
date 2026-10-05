package uk.co.whitbread.reservation.domain.model.payment.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

  private String requestId;
  private String tmpBasketRef;
  private Payment payment;
  private Booking booking;
  private BusinessAccount businessAccount;
  private List<String> specialRequests;
  private List<String> bookingNotes;
}
