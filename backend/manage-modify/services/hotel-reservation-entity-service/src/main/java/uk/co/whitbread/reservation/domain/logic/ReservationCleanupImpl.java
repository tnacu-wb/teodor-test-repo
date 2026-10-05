package uk.co.whitbread.reservation.domain.logic;

import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum.AMENDED;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum.OPEN;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;

@Slf4j
@RequiredArgsConstructor
public class ReservationCleanupImpl implements ReservationCleanup {

  private final BasketOutPort basketOutPort;
  private final HotelReservationOhipOutPort hotelReservationOhipOutPort;

  public void cleanupTempBasket(String basketReference) {

    var basket = basketOutPort.getBasketById(basketReference);
    cleanupTempBasket(basket);

  }

  public void cleanupTempBasket(BasketResponse tempBasket) {

    if (!(OPEN.getValue().equals(tempBasket.getStatus()) || AMENDED.getValue()
        .equals(tempBasket.getStatus()))) {
      var ex = new GenericBadRequestException(ErrorCode.DIGITAL_DELETE_BASKET_EXCEPTION,
          "Only Baskets with OPEN or AMENDED status can be deleted");
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    var eTag = tempBasket.getETag();
    for (BasketItemResponse item : tempBasket.getItems()) {
      hotelReservationOhipOutPort.deleteReservation(tempBasket.getHotelId(), item.getSourceId());
      var newTempBasket = basketOutPort.removeItem(tempBasket.getReference(),
          item.getSourceId(), eTag.replace("\"", ""));
      eTag = newTempBasket.getETag();
    }
    basketOutPort.deleteBasket(tempBasket.getReference(), eTag.replace("\"", ""));
  }

  public void cleanupOriginalBasket(BasketResponse originalBasket,
      List<String> originalRsvIdsToBeDeleted) {
    if (Objects.nonNull(originalRsvIdsToBeDeleted) && !originalRsvIdsToBeDeleted.isEmpty()) {
      basketOutPort.removeItems(originalBasket.getReference(), originalRsvIdsToBeDeleted,
          originalBasket.getETag());
    }
  }
}
