package uk.co.whitbread.basket.processor.domain.ports.primary;

import uk.co.whitbread.basket.processor.domain.model.in.BasketOrder;

public interface BasketOrderProcessInPort {
  void processOrder(final BasketOrder basketOrder);

  void processCancelOrder(final BasketOrder basketOrder);

  void processAmend(final BasketOrder basketOrder);

  void handleFailedOrder(final BasketOrder basketOrder);
}
