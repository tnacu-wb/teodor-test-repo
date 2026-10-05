package uk.co.whitbread.basket.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.email.out.TransactionData;

public interface EmailNotificationOutPort {

  void sendEmailNotificationEvent(final Basket basket, final EmailNotificationEventType event,
      final String emailAddress, final TransactionData transactionData, List<String> mailSuppressionSorceCodes);

}
