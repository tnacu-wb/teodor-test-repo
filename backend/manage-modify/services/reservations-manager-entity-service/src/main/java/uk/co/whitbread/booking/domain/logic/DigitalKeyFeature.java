package uk.co.whitbread.booking.domain.logic;

import java.util.EnumSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingStatus;
import uk.co.whitbread.booking.domain.properties.DigitalKeyProperties;


@RequiredArgsConstructor
@Slf4j
public class DigitalKeyFeature {

  private final DigitalKeyProperties digitalKeyProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public boolean isDigitalKeyAvailable(Booking bookingResponse) {

    if (digitalKeyProperties.getDkHotels() == null || bookingResponse.getHotelCode() == null
        || !digitalKeyProperties.getDkHotels().contains(bookingResponse.getHotelCode())) {
      log.warn("Given Hotel ID not supported: {}", bookingResponse.getHotelCode());
      return false;
    }


    int maxRoomsAllowed = digitalKeyProperties.getMaxRooms();
    if (bookingResponse.getNoOfRooms() > maxRoomsAllowed) {
      log.warn("Digital Key data not valid: (Reservation count: {}, Max allowed: {})",
          bookingResponse.getNoOfRooms(), maxRoomsAllowed);
      return false;
    }

    if (!EnumSet.of(BookingStatus.FUTURE, BookingStatus.PAST, BookingStatus.CHECKED_IN)
        .contains(bookingResponse.getBookingStatus())) {
      log.warn("Booking status not valid  :{}", bookingResponse.getBookingStatus());
      return false;
    }
    return true;
  }

  public void setDigitalKeyFlag(Booking bookingResponse,
                                 boolean isDigitalKey) {
    bookingResponse.setDigitalKeyEligible(isDigitalKey);
  }
}
