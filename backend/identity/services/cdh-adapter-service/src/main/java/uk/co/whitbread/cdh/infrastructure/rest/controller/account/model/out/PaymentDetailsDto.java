package uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetailsDto {
  private boolean allowIndividualCards;
  private List<PaymentCardDto> paymentCards;
  private boolean profileLocked;
}
