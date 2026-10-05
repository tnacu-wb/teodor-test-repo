package uk.co.whitbread.basket.domain.model.content.out;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelPaymentInformation {

  private Address address;
  private List<AcceptedCreditCard> acceptedCreditCards;
  private Map<String, String> paymentMethodsOpera;
  private List<PaymentProvider> paymentProviders;

}
