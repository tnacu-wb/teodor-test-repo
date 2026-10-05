package uk.co.whitbread.payments.domain.ports.secondary;

import uk.co.whitbread.payments.domain.model.out.Basket;

public interface BasketPort {
  Basket getBasket(String basketReference);
}
