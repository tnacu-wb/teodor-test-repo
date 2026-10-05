package uk.co.whitbread.reservation.domain.logic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.CheckOutOnlineProperties;

@RequiredArgsConstructor
@Slf4j
public class CheckOutOnlineLogic {

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd");
  private static final String LATE_CHECKOUT = "HSCOU2";
  private final CheckOutOnlineProperties checkOutOnlineProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final List<String> validBasketStatuses = List.of("COMPLETED", "PRE_CHECKED_IN",
      "CIOL_FAILED", "SECURE_FAILED");
  private final CheckInOnlineProperties checkInOnlineProperties;

  public boolean isCoolAvailable(ReservationByBasketRefResponse reservations,
      HotelInformationResponse hotelInformationResponse, BasketResponse basketResponse) {

    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiBbMobileCheckOut())) {
      return false;
    }

    if (!isCoolDataValid(hotelInformationResponse, reservations, basketResponse)) {
      log.warn("COOL data not valid for booking:{}", reservations);
      return false;
    }

    if (!isPilotHotel(reservations) && !isCountryCompliant(hotelInformationResponse)) {
      log.warn("COOL hotel:{} is not in the pilot list:{}", reservations.getHotelId(),
          checkOutOnlineProperties.getPilotHotels());
      return false;
    }
    var endingHour = getEndTimeForCheckOut(reservations);
    return isReservationCheckedIn(reservations)
        && isWithinDepartureDay(reservations)
        && isBetweenStartAndEndTime(hotelInformationResponse, endingHour);
  }

  private boolean isBetweenStartAndEndTime(HotelInformationResponse hotelInformationResponse,
      int endingHour) {
    try {
      var hotelTimeZone = ZoneId.of(hotelInformationResponse.getHotelTimeZone());
      var currentTime = ZonedDateTime.now(hotelTimeZone).toLocalTime();
      var startingHour = LocalTime.of(checkOutOnlineProperties.getStartingHour(), 0);
      var endHour = LocalTime.of(endingHour, 0);
      return currentTime.isAfter(startingHour) && currentTime.isBefore(endHour);
    } catch (Exception ex) {
      var exMessage = String.format("COOL: Cannot parse hotel timezone:%s",
          hotelInformationResponse.getHotelTimeZone());
      ExceptionLogger.log(log, ex, exMessage);
      return false;
    }
  }

  private boolean isWithinDepartureDay(ReservationByBasketRefResponse reservations) {
    var bookingRef = reservations.getBookingReference();
    var roomStayByIdResponse = reservations.getReservationByIdList()
        .stream()
        .filter(Objects::nonNull)
        .map(ReservationByIdResponse::getRoomStay)
        .filter(Objects::nonNull)
        .findFirst();

    if (roomStayByIdResponse.isPresent()) {
      try {
        var departureDate = roomStayByIdResponse.get().getDepartureDate();
        LocalDate departure = LocalDate.parse(departureDate, DATE_TIME_FORMATTER);
        LocalDate currentDate = LocalDate.now();

        long daysBetween = ChronoUnit.DAYS.between(currentDate, departure);
        return daysBetween >= 0 && daysBetween <= checkOutOnlineProperties.getDaysUntilDeparture();
      } catch (Exception ex) {
        var exMessage = String.format("COOL: Cannot parse departure date for booking:%s",
            bookingRef);
        ExceptionLogger.log(log, ex, exMessage);
        return false;
      }
    }
    return false;
  }

  private boolean isCoolDataValid(HotelInformationResponse hotelInformationResponse,
      ReservationByBasketRefResponse reservations, BasketResponse basketResponse) {
    return ObjectUtils.allNotNull(hotelInformationResponse, reservations) && ObjectUtils.allNotNull(
        hotelInformationResponse.getHotelCountryCode(),
        reservations.getReservationByIdList(), reservations.getHotelId()) && hasValidBasketStatus(
        basketResponse.getStatus());
  }

  private boolean hasValidBasketStatus(String status) {
    if (Objects.nonNull(status)) {
      return validBasketStatuses.contains(status);
    }
    return false;
  }

  private boolean isReservationCheckedIn(ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList().stream()
        .filter(reservation -> Objects.nonNull(reservation.getReservationStatus()))
        .anyMatch(reservation ->
            checkOutOnlineProperties.getCoolReservationStatus()
                .contains(reservation.getReservationStatus()));
  }

  private int getEndTimeForCheckOut(ReservationByBasketRefResponse reservations) {
    var isLco = reservations.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getReservationPackageList)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(ReservationPackagesDetailsResponse::getPackageCode)
        .filter(Objects::nonNull)
        .anyMatch(LATE_CHECKOUT::equals);

    return isLco ? checkOutOnlineProperties.getEndingHourLco()
        : checkOutOnlineProperties.getEndingHour();
  }

  private boolean isPilotHotel(ReservationByBasketRefResponse reservations) {
    if (!checkOutOnlineProperties.getPilotHotels().isEmpty()) {
      return checkOutOnlineProperties.getPilotHotels().contains(reservations.getHotelId());
    }
    return true;
  }

  private boolean isCountryCompliant(HotelInformationResponse hotelInformationResponse) {
    if (!checkInOnlineProperties.getCiolCountryCodes().isEmpty()) {
      return checkInOnlineProperties.getCiolCountryCodes()
          .contains(hotelInformationResponse.getHotelCountryCode());
    }
    return true;
  }
}
