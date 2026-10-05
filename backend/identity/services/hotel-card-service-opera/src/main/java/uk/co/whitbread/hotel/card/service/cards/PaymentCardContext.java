package uk.co.whitbread.hotel.card.service.cards;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;

import java.util.EnumMap;
import java.util.Map;

@Service
public class PaymentCardContext {

  private final Map<SaveCardPurpose, PaymentCardStrategy> strategies = new EnumMap<>(SaveCardPurpose.class);

  public void registerStrategy(SaveCardPurpose saveCardPurpose, PaymentCardStrategy strategy) {
    strategies.put(saveCardPurpose, strategy);
  }

  public PaymentCardStrategy getStrategy(SaveCardPurpose saveCardPurpose) {
    return strategies.get(saveCardPurpose);
  }

  public SaveCardPurpose getSaveCardPurpose(PaymentCardDTO paymentCard) {
    if (Boolean.TRUE.equals(paymentCard.getBusiness())) {
      if (Boolean.TRUE.equals(paymentCard.getPersonalCard())) {
        return SaveCardPurpose.BB_PERSONAL;
      } else {
        return SaveCardPurpose.BB_CENTRAL;
      }
    } else {
      return SaveCardPurpose.PI_PERSONAL;
    }
  }
}
