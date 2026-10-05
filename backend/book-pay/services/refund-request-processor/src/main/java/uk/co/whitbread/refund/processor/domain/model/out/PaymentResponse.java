package uk.co.whitbread.refund.processor.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

  private String paymentId;
  private ProviderResponse providerResponse;
  private Booking booking;
  private Payment payment;
  private boolean refunded;
  private String bookingReference;
  private String paymentStatus;
}