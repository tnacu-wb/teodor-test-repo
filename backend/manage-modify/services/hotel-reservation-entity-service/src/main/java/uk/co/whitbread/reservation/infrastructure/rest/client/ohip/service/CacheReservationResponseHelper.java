package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.model.cache.HotelReservationCache;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.ReservationRequest;

@Slf4j
@Component
public class CacheReservationResponseHelper {

  private final CacheManager cacheManager;

  public CacheReservationResponseHelper(
      @Qualifier("reservationCacheManager30Minutes") CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  public void cacheReservationRequest(ReservationRequest createReservationRequest,
                                                        String basketReference, String hotelId) {
    var reservations = Optional.ofNullable(createReservationRequest.getReservations())
        .filter(CollectionUtils::isNotEmpty)
        .orElseThrow(() -> new IllegalArgumentException("Reservations list is empty"));

    int totalAdults = createReservationRequest.getReservations()
        .stream()
        .mapToInt(Reservation::getAdultsNumber)
        .sum();
    int totalChildren = createReservationRequest.getReservations()
        .stream()
        .mapToInt(Reservation::getChildrenNumber)
        .sum();
    Reservation reservation = reservations.get(0);
    HotelReservationCache hotelReservationCache = buildHotelReservationCache(hotelId,
        basketReference, reservation, totalAdults, totalChildren,
        createReservationRequest.getBookingFlowId());
    Optional.ofNullable(cacheManager.getCache(HotelReservationConstants.RESERVATION_CACHE))
        .ifPresent(cache -> cache.put(basketReference, hotelReservationCache));
  }

  private HotelReservationCache buildHotelReservationCache(String hotelId, String basketReference,
              Reservation reservation, int totalAdults, int totalChildren, String bookingFlowId) {
    return HotelReservationCache.builder()
        .hotelId(hotelId)
        .startDate(reservation.getArrival())
        .endDate(reservation.getDeparture())
        .nightsNumber(calculateNumberOfNights(reservation.getArrival(),
            reservation.getDeparture()))
        .rateCode(reservation.getRoomRates().getRatePlanCode())
        .adultsNumber(totalAdults)
        .childrenNumber(totalChildren)
        .reservationId(basketReference)
        .bookingFlowId(bookingFlowId)
        .build();
  }

  private Integer calculateNumberOfNights(String arrival, String departure) {
    LocalDate startDate = LocalDate.parse(arrival);
    LocalDate endDate = LocalDate.parse(departure);
    return Math.toIntExact(DAYS.between(startDate, endDate));
  }
}
