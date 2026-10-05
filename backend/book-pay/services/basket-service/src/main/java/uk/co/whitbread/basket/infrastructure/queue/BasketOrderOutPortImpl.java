package uk.co.whitbread.basket.infrastructure.queue;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentsConfirmation;
import uk.co.whitbread.basket.domain.model.payments.out.BookingConfirmationDetails;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOrderOutPort;
import uk.co.whitbread.basket.infrastructure.queue.producer.BasketOrderProducer;

@Slf4j
@AllArgsConstructor
public class BasketOrderOutPortImpl implements BasketOrderOutPort {

  private final BasketOrderProducer basketOrderProducer;

  @Override
  public void processOrder(final Basket basket, final String reqAction,
                           final BookingConfirmationDetails bookingConfirmationDetails,
                           final String ccAgentId) {
    log.info("Processing basket basketReference={}", basket.getBasketId());
    basketOrderProducer.sendOrderAsync(basket, reqAction, bookingConfirmationDetails, ccAgentId);
  }

  @Override
  public void processAmend(final Basket basket, final PaymentsConfirmation paymentsConfirmation,
                    final String reqAction) {
    log.info("Processing basket for amend with basketReference={}", basket.getBasketId());
    basketOrderProducer.sendAmendAsync(basket, paymentsConfirmation, reqAction);
  }
}
