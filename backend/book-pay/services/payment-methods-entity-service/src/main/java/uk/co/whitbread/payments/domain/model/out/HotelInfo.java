package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HotelInfo {
  private Address address;
  private List<AcceptedCreditCard> acceptedCreditCards;
  private List<PaymentMethodsConfiguration> paymentMethodsConfiguration;
  private Map<String, String> paymentMethodsOpera;
  private List<PaymentProvider> paymentProviders;
  private String brand;
  private Boolean isDataTransEnabled;
}
