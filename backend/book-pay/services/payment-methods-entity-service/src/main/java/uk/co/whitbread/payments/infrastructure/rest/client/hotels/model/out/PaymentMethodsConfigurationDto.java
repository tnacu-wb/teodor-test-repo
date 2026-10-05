package uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out;

import lombok.Data;

@Data
public class PaymentMethodsConfigurationDto {
  private String code;
  private String operaPaymentMethod;
  private String supportedChannels;
  private String supportedBookingTypes;
  private String listOrder;
  private String name;
  private String logo;
  private String supportedCards;
}
