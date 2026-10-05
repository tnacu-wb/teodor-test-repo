package uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class HotelInfoDto {
  private AddressDto address;
  private List<AcceptedCreditCardDto> acceptedCreditCards;
  private List<PaymentMethodsConfigurationDto> paymentMethodsConfiguration;
  private Map<String, String> paymentMethodsOpera;
  private List<PaymentProviderDto> paymentProviders;
  private String brand;
  private Boolean isDataTransEnabled;
}
