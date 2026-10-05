package uk.co.whitbread.cdh.domain.model.account.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails {
  @JsonProperty("AllowIndividualCards")
  private boolean allowIndividualCards;
  @JsonProperty("PaymentCards")
  private List<PaymentCard> paymentCards;
  @JsonProperty("ProfileLocked")
  private boolean profileLocked;
}
