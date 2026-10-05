package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentRequiredDetailsDto {

  private String paymentRedirect;
  private String template;
  private String sessionId;
}
