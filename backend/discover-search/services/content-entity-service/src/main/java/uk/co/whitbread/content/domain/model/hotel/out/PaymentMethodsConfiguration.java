package uk.co.whitbread.content.domain.model.hotel.out;

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
