package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class PaymentOption {
  private String type;
  private int order;
  private boolean enabled;
}