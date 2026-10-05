package uk.co.whitbread.hotel.card.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequiredDetails {

  private String paymentRedirect;
  private String template;
  private String sessionId;
  private String providerUrl;
}
