package uk.co.whitbread.reservation.domain.model.payment.in;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentsConfirmation {

  private String reference;
  private String paymentId;
  private String paymentStatus;
  private String bookingReference;
  private String countryCode;
  private String language;
  private String channel;
  private String token;
  private String paymentOptionSelected;
  private String emailAddress;
}
