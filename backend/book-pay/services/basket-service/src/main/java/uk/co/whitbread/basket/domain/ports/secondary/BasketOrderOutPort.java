package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;

public interface BasketOrderOutPort {
  void processOrder(final Basket basket, final String reqAction,
                    final BookingConfirmationDetails bookingConfirmationDetails,
                    final String ccAgentId);

  void processAmend(final Basket basket, final PaymentsConfirmation paymentsConfirmation,
                    final String reqAction);
}
