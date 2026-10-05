package uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentProviderDto {
  private String providerId;
  private List<PaymentMethodDto> paymentMethods;
}
