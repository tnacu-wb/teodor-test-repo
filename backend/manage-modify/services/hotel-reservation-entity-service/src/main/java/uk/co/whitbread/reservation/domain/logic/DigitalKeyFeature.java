package uk.co.whitbread.reservation.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.properties.DigitalKeyProperties;

@RequiredArgsConstructor
@Slf4j
public class DigitalKeyFeature {

  private final DigitalKeyProperties digitalKeyProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public boolean isDigitalKeyAvailable(ReservationByBasketRefResponse reservations) {

    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobileDigitalKey())) {
      log.debug("Feature flag for Mobile Digital Key is disabled.");
      return false;
    }
    if (digitalKeyProperties.getDkHotels() == null || reservations.getHotelId() == null
        || !digitalKeyProperties.getDkHotels().contains(reservations.getHotelId())) {
      log.warn("Given Hotel ID not supported: {}", reservations.getHotelId());
      return false;
    }


    int reservationCount = reservations.getReservationByIdList().size();
    int maxRoomsAllowed = digitalKeyProperties.getMaxRooms();
    if (reservationCount > maxRoomsAllowed) {
      log.warn("Digital Key data not valid: {} (Reservation count: {}, Max allowed: {})",
          reservations, reservationCount, maxRoomsAllowed);
      return false;
    }

    if (!validReservationStatuses(reservations)) {
      log.warn("Reservation status not valid :{}", reservations);
      return false;
    }
    return true;

  }

  private boolean validReservationStatuses(ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList().stream()
        .anyMatch(reservation -> digitalKeyProperties.getReservationStatues()
            .contains(reservation.getReservationStatus()));
  }
}
