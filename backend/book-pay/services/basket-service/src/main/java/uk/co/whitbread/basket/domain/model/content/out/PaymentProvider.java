package uk.co.whitbread.basket.domain.model.content.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProvider {

  private String providerId;
  private List<PaymentMethod> paymentMethods;

}
