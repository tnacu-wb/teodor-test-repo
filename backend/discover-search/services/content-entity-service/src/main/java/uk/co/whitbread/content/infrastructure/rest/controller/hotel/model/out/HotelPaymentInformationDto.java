package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

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
public class HotelPaymentInformationDto {
  private AddressDto address;
  private List<AcceptedCreditCardDto> acceptedCreditCards;
  private List<PaymentMethodsConfigurationDto> paymentMethodsConfiguration;
  private Map<String, String> paymentMethodsOpera;
  private List<PaymentProviderDto> paymentProviders;
  private String brand;
  private Boolean isDataTransEnabled;
}