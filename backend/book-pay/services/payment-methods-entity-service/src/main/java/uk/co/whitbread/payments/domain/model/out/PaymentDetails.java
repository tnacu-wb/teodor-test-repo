package uk.co.whitbread.payments.domain.model.out;

import java.util.List;
import lombok.Data;

@Data
public class PaymentDetails {

  private boolean profileLocked;
  private boolean allowIndividualCards;
  private List<PaymentCard> paymentCards;
}
