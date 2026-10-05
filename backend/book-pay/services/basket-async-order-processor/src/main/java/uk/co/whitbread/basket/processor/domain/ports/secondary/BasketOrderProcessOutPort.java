package uk.co.whitbread.basket.processor.domain.ports.secondary;

import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.CancelReservationResponse;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmAmendRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationRequest;
import uk.co.whitbread.basket.processor.domain.model.out.ConfirmReservationResponse;

public interface BasketOrderProcessOutPort {
  ConfirmReservationResponse confirmReservation(final ConfirmReservationRequest confirmReservationRequest);

  CancelReservationResponse cancelReservation(final CancelReservationRequest cancelReservationRequest);

  Boolean confirmAmend(final ConfirmAmendRequest confirmReservationRequest);

}
