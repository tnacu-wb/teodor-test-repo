package uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentProviderDto {
  private String providerId;
  private List<HotelPaymentMethodDto> paymentMethods;
}
