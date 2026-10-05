package uk.co.whitbread.basket.confirmation.processor.domain.ports.secondary;

import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;

public interface BasketConfirmationProcessorOutPort {

  void sendAcknowledge(final BasketAcknowledge basketAcknowledge);
}
