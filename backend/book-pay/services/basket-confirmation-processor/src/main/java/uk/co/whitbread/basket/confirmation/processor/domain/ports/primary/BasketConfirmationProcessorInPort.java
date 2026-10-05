package uk.co.whitbread.basket.confirmation.processor.domain.ports.primary;

import uk.co.whitbread.basket.confirmation.processor.domain.model.in.BasketAcknowledge;

public interface BasketConfirmationProcessorInPort {

  void processAcknowledge(final BasketAcknowledge basketAcknowledge);

}
