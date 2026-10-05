package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class PaymentPreference {

  private Boolean electronicInvoiceRequired;
  private PaymentCard paymentCard;
}
