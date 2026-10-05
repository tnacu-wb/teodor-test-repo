package uk.co.whitbread.basket.domain.model.payments.out;

import java.util.Date;
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
  private Date createdOn;
  private Date refundedOn;
}
