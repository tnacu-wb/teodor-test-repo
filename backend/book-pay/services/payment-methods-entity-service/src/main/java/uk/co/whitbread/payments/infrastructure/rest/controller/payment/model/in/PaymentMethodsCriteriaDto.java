package uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentMethodsCriteriaDto {

  @NotEmpty
  private String basketReference;

  @NotEmpty
  private String country;

  @NotEmpty
  private String language;

  private UserType userType;

  private String clientChannel;

  private boolean changePaymentBIC;

  private String flow;
}
