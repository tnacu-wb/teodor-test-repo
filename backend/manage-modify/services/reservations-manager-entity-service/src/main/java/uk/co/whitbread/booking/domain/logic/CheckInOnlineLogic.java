package uk.co.whitbread.booking.domain.logic;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import uk.co.whitbread.booking.domain.logic.utils.AEMLabelKeyConstants;
import uk.co.whitbread.booking.domain.model.channel.BookingChannel;
import uk.co.whitbread.booking.domain.model.feature.FeatureFlag;
import uk.co.whitbread.booking.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.booking.domain.model.history.out.BasketStatus;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.model.history.out.BookingResponse;
import uk.co.whitbread.booking.domain.model.history.out.BookingStatus;
import uk.co.whitbread.booking.domain.model.history.out.CiolEligibleBookingsAndHotels;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.PaymentInfoOutPort;
import uk.co.whitbread.booking.domain.properties.CheckInOnlineProperties;

@RequiredArgsConstructor
@Slf4j
public class CheckInOnlineLogic {

  private static final String DE_COUNTRY_CODE = "DE";
  private static final String MOBILE = "MOBILE";
  private final BasketOutPort basketOutPort;
  private final CheckInOnlineProperties checkInOnlineProperties;
  private final HotelInfoOutPort hotelInfoOutPort;
  private final PaymentInfoOutPort paymentInfoOutPort;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;

  public void setCiolDataEligibleBookings(BookingResponse originalBookings,
      CiolEligibleBookingsAndHotels eligibleBookingsAndHotels) {

    // only get basket status for bookings already eligible for CIOL,
    // no point calling basket service for every booking
    var reservationIds = Objects.nonNull(eligibleBookingsAndHotels)
        && Objects.nonNull(eligibleBookingsAndHotels.reservationIds())
        ? eligibleBookingsAndHotels.reservationIds() : new HashSet<String>();

    var hotelCountriesMap = Objects.nonNull(eligibleBookingsAndHotels)
        && Objects.nonNull(eligibleBookingsAndHotels.hotelCountries())
        ? eligibleBookingsAndHotels.hotelCountries() : new HashMap<String, String>();
    var baskets = basketOutPort.getBasketStatusesForBookingRefs(reservationIds);
    log.debug("CIOL eligible bookings found {}", baskets);
    for (Booking booking : originalBookings.getBookings()) {
      var basketStatus = baskets.get(booking.getBookingReference());

      setCiolFlagForEligibleBookings(reservationIds, booking, basketStatus);
      booking.setBasketStatus(basketStatus);
      booking.setHotelCountry(hotelCountriesMap.get(booking.getHotelCode()));
    }
  }

  public CiolEligibleBookingsAndHotels getEligibleBookingsForCiol(BookingResponse bookingResponse) {
    // Phase 1: Pre-hotel validations
    var preValidatedBookings = bookingResponse.getBookings().stream()
        .filter(this::validatePreHotelChecks).toList();
    // Fetch hotel info only for bookings that passed Phase 1
    var hotelsCountries = hotelInfoOutPort.getHotelInfo(preValidatedBookings);
    // Phase 2: Post-hotel validations
    var reservationIds = preValidatedBookings.stream()
        .filter(booking -> validatePostHotelChecks(booking, hotelsCountries))
        .map(Booking::getBookingReference)
        .collect(Collectors.toSet());
    return new CiolEligibleBookingsAndHotels(reservationIds, hotelsCountries);
  }

  private boolean isCiolHotelAndCountryCompliant(Booking bookings,
      final Map<String, String> hotelInfo) {
    var hotelCountry = hotelInfo.getOrDefault(bookings.getHotelCode(), "");

    if (DE_COUNTRY_CODE.equals(hotelCountry)) {
      return isCiolHotelCompliant(bookings, checkInOnlineProperties.getDeRegCardHotels());
    } else {
      return isCiolHotelCompliant(bookings, checkInOnlineProperties.getCiolHotels())
          || isCiolCountryCodeCompliant(bookings,
          hotelInfo);
    }
  }

  private void setCiolFlagForEligibleBookings(Set<String> eligibleBookingRef, Booking booking,
      BasketStatus basketStatus) {

    if (isBookingEligibleCiol(eligibleBookingRef, booking)
        && Objects.nonNull(basketStatus)
        && !BasketStatus.shouldDisableCiol(basketStatus)) {

      booking.setCheckInOnlineAvailable(true);
    }
  }

  private boolean isBookingEligibleCiol(Set<String> eligibleBookingRef, Booking booking) {

    return eligibleBookingRef.contains(booking.getBookingReference());
  }

  private boolean isArrivalDateWithinCiol(Booking booking) {

    LocalDate arrivalDate = booking.getArrivalDate();
    LocalDate currentDate = LocalDate.now();

    long daysBetween = ChronoUnit.DAYS.between(currentDate, arrivalDate);
    return daysBetween >= 0 && daysBetween <= checkInOnlineProperties.getDaysWithinArrival();
  }

  private boolean isCiolHotelCompliant(Booking booking, Set<String> hotels) {
    return isCompliantWithList(
        hotels,
        booking.getHotelCode()
    );
  }

  private boolean isCiolRateCompliant(Booking booking) {
    boolean tmPatternMatch = checkInOnlineProperties.getCiolRatesExcluded().contains("TM")
        && booking.getRateName().matches("TM\\d{3}");
    return checkInOnlineProperties.getCiolRatesExcluded().isEmpty()
        || (!checkInOnlineProperties.getCiolRatesExcluded().contains(booking.getRateName())
        && !tmPatternMatch);
  }

  private boolean isCiolCountryCodeCompliant(Booking booking, Map<String, String> hotelIno) {
    var hotelCountry = hotelIno.getOrDefault(booking.getHotelCode(), "");
    return isCompliantWithList(
        checkInOnlineProperties.getCiolCountryCodes(),
        hotelCountry
    );
  }

  private boolean isCiolEmailCompliant(Booking booking) {
    String email = "";

    if (Objects.nonNull(booking.getBooker())) {
      email = booking.getBooker().getEmailAddress();
    }
    return isCompliantWithList(checkInOnlineProperties.getCiolEmails(), email);
  }

  private boolean isCiolRoomCompliant(Booking booking, Map<String, String> hotelIno) {
    var hotelCountry = hotelIno.getOrDefault(booking.getHotelCode(), "");
    var rooms = DE_COUNTRY_CODE.equals(hotelCountry) ? checkInOnlineProperties.getDeRegCardRooms()
        : checkInOnlineProperties.getMaxRooms();
    return booking.getNoOfRooms() <= rooms;
  }

  private <T> boolean isCompliantWithList(Collection<T> allowedValues, T value) {
    return allowedValues.isEmpty() || allowedValues.contains(value);
  }

  private boolean isGermanHotelWithDeRegCardOn(Booking booking, Map<String, String> hotelIno) {
    var hotelCountry = hotelIno.getOrDefault(booking.getHotelCode(), "");
    if (DE_COUNTRY_CODE.equals(hotelCountry)) {
      return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiBbMobileDeRegCard());
    }
    return true;
  }

  /**
   * For the reservations paid with PIBA card from the eligible list if the feature flag is on, sets
   * isCheckInOnline = false and set the error label key for those bookings.
   *
   * @param checkingOnlineBookings the eligible list of bookings to check for PIBA card payment
   * @param bookingList            the list of bookings
   * @param bookingChannel         the booking channel to check if it's MOBILE, as PIBA card
   *                               restriction only applies for mobile bookings
   * @return the modified list of bookings reference with isCheckingOnline = false and ciol error
   *         label key if applicable
   */
  public List<Booking> filterByPibaCard(List<Booking> checkingOnlineBookings,
      List<Booking> bookingList, BookingChannel bookingChannel) {
    List<Booking> modifiedBookings = bookingList;
    if (bookingChannel != null && MOBILE.equals(bookingChannel.getSubchannel())) {
      boolean excludePibaCP = unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getMobileCiolPiba());
      boolean excludePibaCNP = unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getMobileCiolPibaCnp());
      List<String> filteredBookingsByPiba = paymentInfoOutPort.getPibaCpReservations(
          checkingOnlineBookings, excludePibaCP, excludePibaCNP);
      modifiedBookings.stream()
          .filter(booking -> filteredBookingsByPiba.contains(booking.getBookingReference()))
          .forEach(booking -> {
            booking.setCheckInOnlineAvailable(false);
            booking.setCiolErrorLabelKey(
                AEMLabelKeyConstants.DIGITAL_CIOL_PIBA_CARD_NOT_ALLOWED);
          });
    }
    return modifiedBookings;
  }

  //Set error label key for CIOL (CTECH-5568)
  private boolean validatePreHotelChecks(Booking booking) {
    if (!ObjectUtils.allNotNull(booking.getBookingStatus(), booking.getHotelCode(), booking.getBooker())
        || !BookingStatus.FUTURE.equals(booking.getBookingStatus())) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_DATA_NOT_VALID);
      return false;
    }
    if (!isArrivalDateWithinCiol(booking)) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_ARRIVAL_DATE_COMPLAINT);
      return false;
    }
    if (!isCiolRateCompliant(booking)) {
      booking.setCiolErrorLabelKey(String.format(AEMLabelKeyConstants.DIGITAL_CIOL_ROOM_STAY_RATES_INVALID,
          booking.getRateName()));
      return false;
    }
    if (!isCiolEmailCompliant(booking)) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_BOOKER_EMAIL_NOT_ALLOWED);
      return false;
    }
    return true;
  }

  //Set error label key for CIOL (CTECH-5568)
  private boolean validatePostHotelChecks(Booking booking, Map<String, String> hotelsCountries) {
    if (!isGermanHotelWithDeRegCardOn(booking, hotelsCountries)) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_DE_REG_CARD_DISABLED);
      return false;
    }
    if (!isCiolHotelAndCountryCompliant(booking, hotelsCountries)) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_COUNTRY_OR_HOTEL_NOT_ALLOWED);
      return false;
    }
    if (!isCiolRoomCompliant(booking, hotelsCountries)) {
      booking.setCiolErrorLabelKey(AEMLabelKeyConstants.DIGITAL_CIOL_ROOM_LIMIT_EXCEEDED);
      return false;
    }
    return true;
  }

}

