package uk.co.whitbread.payments.domain.model.out;

import lombok.Data;

@Data
public class PaymentMethodsConfiguration {
  private String code;
  private String operaPaymentMethod;
  private String supportedChannels;
  private String supportedBookingTypes;
  private String listOrder;
  private String name;
  private String logo;
  private String supportedCards;
}
