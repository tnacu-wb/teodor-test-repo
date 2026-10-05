package uk.co.whitbread.content.infrastructure.rest.client.aem.model.hotel.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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