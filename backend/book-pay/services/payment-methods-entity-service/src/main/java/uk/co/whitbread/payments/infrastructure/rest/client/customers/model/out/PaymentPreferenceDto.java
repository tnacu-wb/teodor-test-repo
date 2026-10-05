package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;


import lombok.Data;

@Data
public class PaymentPreferenceDto {
  private Boolean electronicInvoiceRequired;
  private PaymentCardDto paymentCard;
}
