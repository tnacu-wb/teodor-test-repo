package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentProvider {
  private String providerId;
  private List<HotelPaymentMethod> paymentMethods;
}