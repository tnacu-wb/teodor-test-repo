package uk.co.whitbread.basket.domain.logic;

import java.util.List;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.email.out.EmailNotificationEventType;
import uk.co.whitbread.basket.domain.model.reservation.out.Deposits;

public interface EmailNotificationService {

  void sendEmailNotificationEvent(final Basket basket, final EmailNotificationEventType event,
      final String emailAddress, final Boolean isTransactionDataRequired);

  void sendEmailNotificationEvent(final Basket basket, final EmailNotificationEventType event,
      final String emailAddress, final boolean failedRefund, final List<Deposits> deposits);
}
