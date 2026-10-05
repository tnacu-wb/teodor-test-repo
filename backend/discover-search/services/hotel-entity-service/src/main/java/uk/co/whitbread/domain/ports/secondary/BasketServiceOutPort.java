package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.basket.out.Basket;

public interface BasketServiceOutPort {

  Basket getBasket(String basketReference);
}
