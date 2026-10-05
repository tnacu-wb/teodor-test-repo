package uk.co.whitbread.domain.logic;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.domain.model.availability.out.HotelAvailability;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityResultV2;
import uk.co.whitbread.domain.model.availability.out.PriceInfo;
import uk.co.whitbread.domain.model.basket.out.Basket;
import uk.co.whitbread.domain.model.basket.out.BasketItem;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.model.packages.out.PackagesResponse;
import uk.co.whitbread.domain.ports.secondary.BasketServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.ContentServiceOutPort;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;
import uk.co.whitbread.hotel.content.generated.models.HotelInformationExtendedDto;

@Slf4j
@UtilityClass
public class CityTaxUtils {

  private static final List<String> CHANNELS = List.of("PI", "CCUI", "BB", "DISTR");
  private static final List<String> REASON_OF_STAY_NO_TAX = List.of("NTLEI", "NTBUS");
  public static final String STAY_TYPE = "STAY";
  private static int NUMBER_OF_NIGHTS_CITY_TAX_APPLIES_FOR_EDI_MINUS_ONE = 4;

  public static void setPriceWithoutCityTaxIfApplicable(HotelAvailability availability, String channel,
      String startDateString, ContentServiceOutPort contentServiceOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper) {

    if (shouldSkipCityTax(channel, availability.getHotelId(), startDateString,
        contentServiceOutPort, unleashWrapper)) {
      return;
    }
    replaceNetAmountWithEffectiveRateAmount(availability);
  }

  public static void setPriceWithoutCityTaxIfApplicableForV2(HotelAvailabilityResultV2 availability, String channel,
      LocalDate startDate, LocalDate departureDate, ContentServiceOutPort contentServiceOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper) {

    if (shouldSkipCityTax(channel, availability.getHotelId(), startDate.toString(),
        contentServiceOutPort, unleashWrapper)) {
      if (isHotelFromEdinburgh(availability.getHotelId(), contentServiceOutPort)
          && isBookingLongerThanFiveNights(startDate, departureDate)) {
        log.debug("City tax does not apply to hotel {} for bookings n Edinburgh after 5 nights",
            availability.getHotelId());
        replaceNetAmountWithEffectiveRateAmountForV2AfterFiveNights(availability, startDate);
      }
      return;
    }
    replaceNetAmountWithEffectiveRateAmountForV2(availability);
  }

  public static void setPriceWithoutCityTaxForAmendIfApplicable(HotelAvailability availability,
      String originalBasketReference,
      ContentServiceOutPort contentServiceOutPort,
      HotelAvailabilityOutPort availabilityOhipPort,
      BasketServiceOutPort basketServiceOutPort,
      UnleashWrapper<FeatureFlag> unleashWrapper) {

    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUk())) {
      return;
    }

    if (!isHotelEligibleForCityTax(availability.getHotelId(), contentServiceOutPort)) {
      log.debug("City tax does not apply to hotel {}", availability.getHotelId());
      return;
    }

    var reservationId = getReservationIdFromBasket(basketServiceOutPort, originalBasketReference);
    if (reservationId == null) {
      log.debug("No stay item found in basket {}", originalBasketReference);
      return;
    }

    if (isCityTaxApplicableForReasonOfStay(availability.getHotelId(), reservationId, availabilityOhipPort)) {
      return;
    }

    replaceNetAmountWithEffectiveRateAmount(availability);
  }

  private static boolean isHotelEligibleForCityTax(String hotelId, ContentServiceOutPort contentServiceOutPort) {
    var globalConfig = contentServiceOutPort.getGlobalConfig(null, null);
    return globalConfig != null
        && globalConfig.getHotelsWithCityTax() != null
        && globalConfig.getHotelsWithCityTax().contains(hotelId);
  }

  private static String getReservationIdFromBasket(BasketServiceOutPort basketServiceOutPort, String basketReference) {
    return Optional.ofNullable(basketServiceOutPort.getBasket(basketReference))
        .map(Basket::getItems)
        .filter(items -> items != null && !items.isEmpty())
        .flatMap(items -> items.stream()
            .filter(item -> STAY_TYPE.equals(item.getType()))
            .findFirst()
            .map(BasketItem::getSourceId))
        .orElse(null);
  }

  private static boolean isCityTaxApplicableForReasonOfStay(String hotelId, String reservationId,
      HotelAvailabilityOutPort availabilityOhipPort) {

    return Optional.ofNullable(availabilityOhipPort.getLightweightReservations(hotelId, Set.of(reservationId)))
        .map(reservation -> Optional.ofNullable(reservation.getReservationByIdList())
            .filter(list -> !list.isEmpty())
            .map(list -> list.get(0))
            .map(firstReservation -> Optional.ofNullable(firstReservation.getPurposeOfStay())
                .filter(REASON_OF_STAY_NO_TAX::contains)
                .isEmpty())
            .orElse(true))
        .orElse(true);
  }

  public static boolean shouldSkipCityTax(String channel, String hotelId, String startDateString,
                                           ContentServiceOutPort contentServiceOutPort,
                                           UnleashWrapper<FeatureFlag> unleashWrapper) {
    if (!unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiCcuiCityTaxUk())) {
      return true;
    }

    if (isChannelNotApplicable(channel)
        || isHotelNotEligibleForCityTax(hotelId, contentServiceOutPort)) {
      return true;
    }

    var cityTaxConfigsForHotel = getHotelInformationExtendedDto(hotelId, contentServiceOutPort);
    if (cityTaxConfigsForHotel == null) {
      return true;
    }

    if (cityTaxShouldApplyForBookingDateAndEffectiveFromConfig(hotelId, startDateString, cityTaxConfigsForHotel)) {
      return true;
    }

    return false;
  }

  private static boolean cityTaxShouldApplyForBookingDateAndEffectiveFromConfig(String hotelId,
      String startDateString, HotelInformationExtendedDto cityTaxConfigsForHotel) {

    var startDate = LocalDate.parse(startDateString);
    var effectiveFrom = LocalDate.parse(cityTaxConfigsForHotel.getCityTax().getEffectiveFrom());
    var bookingDateFrom = LocalDate.parse(cityTaxConfigsForHotel.getCityTax().getBookingDateFrom());

    if ((effectiveFrom.isBefore(LocalDate.now()) || effectiveFrom.isEqual(LocalDate.now()))
        && (startDate.isAfter(bookingDateFrom) || startDate.isEqual(bookingDateFrom))) {
      log.debug("City tax applies to booking with start date {} for hotel {}",
          startDate, hotelId);
      return true;
    }
    return false;
  }

  private static HotelInformationExtendedDto getHotelInformationExtendedDto(
      String hotelId, ContentServiceOutPort contentServiceOutPort) {
    var cityTaxConfigsForHotel = contentServiceOutPort.getHotelInformation(null, null, hotelId);
    if (isCityTaxConfigInvalid(cityTaxConfigsForHotel)) {
      log.debug("Nonexistent or incomplete city tax config found for hotel {}", hotelId);
      return null;
    }
    return cityTaxConfigsForHotel;
  }

  private static boolean isChannelNotApplicable(String channel) {
    if (channel == null || !CHANNELS.contains(channel)) {
      log.debug("City tax does not apply to channel {}", channel);
      return true;
    }
    return false;
  }

  private static boolean isHotelNotEligibleForCityTax(String hotelId,
      ContentServiceOutPort contentServiceOutPort) {
    var globalConfig = contentServiceOutPort.getGlobalConfig(null, null);
    if (globalConfig == null || globalConfig.getHotelsWithCityTax() == null
        || !globalConfig.getHotelsWithCityTax().contains(hotelId)) {
      log.debug("City tax does not apply to hotel {}", hotelId);
      return true;
    }
    return false;
  }

  private static void replaceNetAmountWithEffectiveRateAmount(HotelAvailability availability) {

    Optional.ofNullable(availability.getRoomRates()).orElse(Collections.emptyList())
        .forEach(roomRate ->
            Optional.ofNullable(roomRate.getRoomTypes()).orElse(Collections.emptyList())
                .forEach(roomType ->
                    Optional.ofNullable(roomType.getRooms()).orElse(Collections.emptyList())
                        .forEach(room -> {
                          var roomPriceBreakdown = room.getRoomPriceBreakdown();

                          if (roomPriceBreakdown != null) {

                            if (roomPriceBreakdown.getEffectiveRateAmount() != null) {
                              roomPriceBreakdown.setTotalNetAmount(roomPriceBreakdown.getEffectiveRateAmount());
                              log.debug("Updated totalNetAmount with effective rate for RoomPriceBreakdown "
                                      + "in hotel {}",
                                  availability.getHotelId());
                            }

                            var dailyPrices = roomPriceBreakdown.getDailyPrices();
                            if (dailyPrices != null) {
                              dailyPrices.stream()
                                  .filter(dailyPrice -> dailyPrice.getEffectiveRate() != null)
                                  .forEach(dailyPrice -> dailyPrice.setNetPrice(dailyPrice.getEffectiveRate()));
                            }
                          }
                        })
                    )
    );
  }

  private static void replaceNetAmountWithEffectiveRateAmountForV2(HotelAvailabilityResultV2 availability) {
    processRoomRates(availability, priceInfo -> true);
  }

  private static void replaceNetAmountWithEffectiveRateAmountForV2AfterFiveNights(
      HotelAvailabilityResultV2 availability, LocalDate startDate) {
    LocalDate startDatePlusFiveNights = startDate.plusDays(NUMBER_OF_NIGHTS_CITY_TAX_APPLIES_FOR_EDI_MINUS_ONE);
    processRoomRates(availability, priceInfo ->
        priceInfo.getStayDate() != null && priceInfo.getStayDate().isAfter(startDatePlusFiveNights));
  }

  private static void processRoomRates(HotelAvailabilityResultV2 availability, Predicate<PriceInfo> predicate) {
    Optional.ofNullable(availability.getRoomStays()).orElse(Collections.emptyList())
        .forEach(roomRate ->
            Optional.ofNullable(roomRate.getRoomTypes()).orElse(Collections.emptyList())
                .forEach(roomType ->
                    Optional.ofNullable(roomType.getRoomRates()).orElse(Collections.emptyList())
                        .forEach(roomRateV2 ->
                            Optional.ofNullable(roomRateV2.getRoomRateInfo()).orElse(null)
                                .getPriceInfo().stream()
                                .filter(priceInfo -> priceInfo != null && predicate.test(priceInfo))
                                .forEach(priceInfo -> {
                                  if (priceInfo.getAmountBeforeTax() != null) {
                                    priceInfo.setAmountAfterTax(priceInfo.getAmountBeforeTax());
                                    log.debug("Updated totalNetAmount with effective rate for RoomPriceBreakdown "
                                            + "in hotel {}",
                                        availability.getHotelId());
                                  }
                                })
                        )
                )
    );
  }

  private static boolean isBookingLongerThanFiveNights(LocalDate startDate, LocalDate departureDate) {
    return departureDate.isAfter(startDate.plusDays(NUMBER_OF_NIGHTS_CITY_TAX_APPLIES_FOR_EDI_MINUS_ONE));
  }

  private static boolean isHotelFromEdinburgh(String hotelId, ContentServiceOutPort contentServiceOutPort) {
    var cityTaxConfigsForHotel = contentServiceOutPort.getHotelInformation(null, null, hotelId);
    if (isCityTaxConfigInvalid(cityTaxConfigsForHotel)) {
      log.debug("Nonexistent or incomplete city tax config found for hotel {}", hotelId);
      return false;
    }
    LocalDate lastEdiNonCityTaxDate = LocalDate.parse(cityTaxConfigsForHotel.getCityTax().getBookingDateFrom())
        .minusDays(1);
    return LocalDate.parse(cityTaxConfigsForHotel.getCityTax().getBookingDateFrom())
        .isAfter(lastEdiNonCityTaxDate);
  }

  private static boolean isCityTaxConfigInvalid(HotelInformationExtendedDto cityTaxConfigsForHotel) {
    return cityTaxConfigsForHotel == null
        || cityTaxConfigsForHotel.getCityTax() == null
        || cityTaxConfigsForHotel.getCityTax().getIsCityTaxHotel() == null
        || !cityTaxConfigsForHotel.getCityTax().getIsCityTaxHotel()
        || StringUtils.isEmpty(cityTaxConfigsForHotel.getCityTax().getEffectiveFrom())
        || StringUtils.isEmpty(cityTaxConfigsForHotel.getCityTax().getBookingDateFrom());
  }

  public void updateHasCityTaxFlags(String hotelId, String startDateString, PackagesResponse packagesResponse,
      ContentServiceOutPort contentServiceOutPort) {

    var isCityTaxPackageNotApplicableForDate = isCityTaxPackageNotApplicableForDate(hotelId, startDateString,
        contentServiceOutPort);

    if (!isCityTaxPackageNotApplicableForDate) {
      return;
    }

    if (packagesResponse.getHotelHasCityTaxForLeisure() != null
        && Boolean.TRUE.equals(packagesResponse.getHotelHasCityTaxForLeisure())) {
      packagesResponse.setHotelHasCityTaxForLeisure(false);
    }

    if (packagesResponse.getHotelHasCityTaxForBusiness() != null
        && Boolean.TRUE.equals(packagesResponse.getHotelHasCityTaxForBusiness())) {
      packagesResponse.setHotelHasCityTaxForBusiness(false);
    }
  }

  private static boolean isCityTaxPackageNotApplicableForDate(String hotelId, String startDateString,
      ContentServiceOutPort contentServiceOutPort) {

    var cityTaxConfigsForHotel = contentServiceOutPort.getHotelInformation(null, null, hotelId);

    if (cityTaxConfigsForHotel == null
        || cityTaxConfigsForHotel.getCityTax() == null
        || cityTaxConfigsForHotel.getCityTax().getIsCityTaxHotel() == null
        || !Boolean.TRUE.equals(cityTaxConfigsForHotel.getCityTax().getIsCityTaxHotel())) {
      log.debug("Invalid City Tax config or city tax doesn't apply for hotel: {}", hotelId);
      return false;
    }

    // German hotel
    if (Boolean.TRUE.equals(cityTaxConfigsForHotel.getCityTax().getIsCityTaxHotel())
        && (StringUtils.isEmpty(cityTaxConfigsForHotel.getCityTax().getEffectiveFrom())
        || StringUtils.isEmpty(cityTaxConfigsForHotel.getCityTax().getBookingDateFrom()))) {
      return false;
    }

    return !cityTaxShouldApplyForBookingDateAndEffectiveFromConfig(hotelId, startDateString, cityTaxConfigsForHotel);
  }
}
