package uk.co.whitbread.reservation.domain.model.index.header.data.out;

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
