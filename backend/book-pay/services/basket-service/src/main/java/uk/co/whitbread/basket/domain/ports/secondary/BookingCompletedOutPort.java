package uk.co.whitbread.basket.domain.ports.secondary;

import uk.co.whitbread.basket.domain.model.basket.out.Basket;

public interface BookingCompletedOutPort {
  void publishBookingCompleted(Basket basket);
}
