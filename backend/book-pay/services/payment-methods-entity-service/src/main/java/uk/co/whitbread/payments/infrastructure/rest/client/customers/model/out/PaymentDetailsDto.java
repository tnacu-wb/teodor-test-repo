package uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentDetailsDto {

  private boolean profileLocked;
  private boolean allowIndividualCards;
  private List<PaymentCardDto> paymentCards;
}
