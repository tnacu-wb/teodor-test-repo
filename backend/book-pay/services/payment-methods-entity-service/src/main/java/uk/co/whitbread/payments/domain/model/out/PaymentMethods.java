package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class PaymentMethods {
  private List<PaymentMethod> paymentMethods;
}