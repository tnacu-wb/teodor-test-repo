package uk.co.whitbread.booking.infrastructure.rest.client.basket;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.basket.service.BasketClient;

@Slf4j
@RequiredArgsConstructor
public class BasketOutPortImpl implements BasketOutPort {

  private final BasketClient basketClient;

  @Override
  public Map<String, BasketStatus> getBasketStatusesForBookingRefs(Set<String> bookingRefs) {

    Map<String, BasketStatus> basketStatusMap = new HashMap<>();

    if (!bookingRefs.isEmpty()) {
      log.debug("calling basket service with booking refs: {}", bookingRefs);
      var baskets = basketClient.getBasketsForBookingReferences(bookingRefs);

      for (var basket : baskets) {
        basketStatusMap.put(basket.getBookingReference(), basket.getStatus());
      }

    }
    return basketStatusMap;
  }

  @Override
  public Map<String, Set<String>> getBasketSourceIdsForBookingRefs(Set<String> bookingRefs) {
    Map<String, Set<String>> basketSourceIdsMap = new HashMap<>();

    if (!bookingRefs.isEmpty()) {
      var baskets = basketClient.getBasketsForBookingReferences(bookingRefs);
      for (var basket : baskets) {
        var items = basket.getItems();
        Set<String> sourceIds = (items == null)
            ? Collections.emptySet()
            : items.stream()
                .map(item -> item.getSourceId())
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
        basketSourceIdsMap.put(basket.getBookingReference(), sourceIds);
      }
    }
    return basketSourceIdsMap;
  }
}
