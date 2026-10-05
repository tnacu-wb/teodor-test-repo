package uk.co.whitbread.payments.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaypalRequest {
  private boolean isPaypalEnabled;
  private String clientToken;
  private String clientId;
}
