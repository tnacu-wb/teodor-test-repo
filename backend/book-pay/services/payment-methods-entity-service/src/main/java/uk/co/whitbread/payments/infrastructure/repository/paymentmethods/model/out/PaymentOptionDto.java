package uk.co.whitbread.payments.infrastructure.repository.paymentmethods.model.out;

import lombok.Data;

@Data
public class PaymentOptionDto {
  private String type;
  private int order;
  private boolean enabled;
}
