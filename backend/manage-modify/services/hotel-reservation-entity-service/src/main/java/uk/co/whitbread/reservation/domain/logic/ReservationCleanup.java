package uk.co.whitbread.reservation.domain.logic;

import java.util.List;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;

public interface ReservationCleanup {

  void cleanupTempBasket(BasketResponse basketResponse);

  void cleanupTempBasket(String basketReference);

  void cleanupOriginalBasket(BasketResponse originalBasket,
      List<String> originalRsvIdsToBeDeleted);
}
