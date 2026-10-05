package uk.co.whitbread.reservation.domain.logic;

import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_ARRIVAL_DATE_COMPLAINT;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_BOOKER_EMAIL_NOT_ALLOWED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_COUNTRY_OR_HOTEL_NOT_ALLOWED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_DATA_NOT_VALID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_DE_REG_CARD_DISABLED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_PIBA_CARD_NOT_ALLOWED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_ROOM_LIMIT_EXCEEDED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_ROOM_STAY_RATES_INVALID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DIGITAL_CIOL_THIRD_PARTY_PREPAID_NOT_ALLOWED;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.util.Pair;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.properties.CheckInOnlineProperties;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;

@Slf4j
@RequiredArgsConstructor
public class CheckInOnlineLogic {

  private static final String CIOL_STATUS = "PRE_CHECKED_IN";
  private static final String CIOL_RC_FAILED = "CIOL_RC_FAILED";
  private static final String DE_COUNTRY_CODE = "DE";
  private static final String ID_CONTEXT = "3rd Party";
  private static final int FOLIO_VIEW_PIBA_CP_ONE = 1;
  private static final int FOLIO_VIEW_PIBA_CNP_TWO = 2;
  private static final List<String> PIBA_LIST = List.of(
      HotelReservationConstants.PIBA_UK_CARD_TYPE,
      HotelReservationConstants.PIBA_EURO_CARD_TYPE);

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd");
  private final CheckInOnlineProperties checkInOnlineProperties;
  private final ThirdpartyBookingProperties thirdpartyBookingProperties;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private static final java.util.regex.Pattern TM_PATTERN = java.util.regex.Pattern.compile("TM\\d{3}");

  public Pair<Boolean, String> isCiolAvailable(ReservationByBasketRefResponse reservations,
                                                      HotelInformationResponse hotelInformationResponse,
                                                      boolean isOta, String idContext) {
    if (!isCiolDataValid(hotelInformationResponse, reservations)) {
      return Pair.of(false, DIGITAL_CIOL_DATA_NOT_VALID);
    }
    if (!isDeRegCardEnabled(hotelInformationResponse)) {
      return Pair.of(false, DIGITAL_CIOL_DE_REG_CARD_DISABLED);
    }
    if (!isCiolCountryCodeCompliant(hotelInformationResponse)
        && !isCiolHotelCompliant(reservations, hotelInformationResponse)) {
      return Pair.of(false, DIGITAL_CIOL_COUNTRY_OR_HOTEL_NOT_ALLOWED);
    }
    if (!isCiolRoomCompliant(reservations, hotelInformationResponse)) {
      return Pair.of(false, DIGITAL_CIOL_ROOM_LIMIT_EXCEEDED);
    }

    var roomStays = reservations.getReservationByIdList()
        .stream()
        .filter(Objects::nonNull)
        .map(ReservationByIdResponse::getRoomStay)
        .filter(Objects::nonNull)
        .toList();
    List<String> nonComplaintRules = roomStays.stream()
        .map(this::getCiolRateCompliance)
        .map(result -> result.get(false))
        .filter(Objects::nonNull)
        .flatMap(List::stream).toList();
    if (roomStays.isEmpty() || !nonComplaintRules.isEmpty()) {
      return Pair.of(false, String.format(DIGITAL_CIOL_ROOM_STAY_RATES_INVALID, nonComplaintRules));
    }

    if (!isOta && !isCiolBookerEmailCompliant(reservations)) {
      return Pair.of(false, DIGITAL_CIOL_BOOKER_EMAIL_NOT_ALLOWED);
    }

    if (isPibaPaymentCard(reservations)) {
      return Pair.of(false, DIGITAL_CIOL_PIBA_CARD_NOT_ALLOWED);
    }

    if (!isPrepaidAllowed(reservations, idContext)) {
      return Pair.of(false, DIGITAL_CIOL_THIRD_PARTY_PREPAID_NOT_ALLOWED);
    }

    if (!isArrivalDateCompliant(roomStays.get(0), isOta)) {
      return Pair.of(false, DIGITAL_CIOL_ARRIVAL_DATE_COMPLAINT);
    }
    return Pair.of(true, "");
  }

  public boolean shouldDisableCiol(BasketResponse basketResponse) {
    return isCompliant(List.of(CIOL_STATUS, CIOL_RC_FAILED), basketResponse::getStatus);
  }

  private boolean isCiolRoomCompliant(ReservationByBasketRefResponse reservations,
                                      HotelInformationResponse hotelInformationResponse) {
    var rooms = reservations.getReservationByIdList().size();
    //for german hotels max room number is different
    var maxRooms = DE_COUNTRY_CODE.equals(hotelInformationResponse.getHotelCountryCode())
        ? checkInOnlineProperties.getDeRegCardRooms() : checkInOnlineProperties.getMaxRooms();
    return rooms <= maxRooms;
  }

  private boolean isCiolHotelCompliant(ReservationByBasketRefResponse reservations,
                                       HotelInformationResponse hotelInformationResponse) {

    var hotelList = DE_COUNTRY_CODE.equals(hotelInformationResponse.getHotelCountryCode())
        ? checkInOnlineProperties.getDeRegCardHotels() : checkInOnlineProperties.getCiolHotels();
    return isCompliant(hotelList, reservations::getHotelId);
  }

  private Map<Boolean, List<String>> getCiolRateCompliance(RoomStayByIdResponse room) {
    var excluded = checkInOnlineProperties.getCiolRatesExcluded();
    if (excluded.isEmpty()) {
      return Map.of(true, List.of());
    }
    boolean tmExcluded = excluded.contains("TM");
    List<String> rateCodes = List.of(room.getRatePlanCode());
    return rateCodes.stream()
        .collect(Collectors.partitioningBy(rate -> {
          boolean excludedRate = excluded.contains(rate);
          boolean tmPatternMatch = tmExcluded && TM_PATTERN.matcher(rate).matches();
          return !excludedRate && !tmPatternMatch;
        }));
  }

  private boolean isCiolCountryCodeCompliant(HotelInformationResponse hotelInformationResponse) {
    return isCompliant(checkInOnlineProperties.getCiolCountryCodes(),
        hotelInformationResponse::getHotelCountryCode);
  }


  private boolean isCiolBookerEmailCompliant(ReservationByBasketRefResponse reservations) {
    return isCompliant(checkInOnlineProperties.getCiolEmails(),
        () -> reservations.getReservationByIdList()
            .stream()
            .filter(reservation -> Objects.nonNull(reservation.getReservationBooker())
                && Objects.nonNull(reservation.getReservationBooker().getEmail()))
            .map(reservation -> reservation.getReservationBooker().getEmail())
            .findFirst()
            .orElse(""));
  }

  private <T> boolean isCompliant(Collection<T> allowedValues, Supplier<T> valueSupplier) {
    return allowedValues.isEmpty() || allowedValues.contains(valueSupplier.get());
  }

  private boolean isCiolDataValid(HotelInformationResponse hotelInformationResponse,
                                  ReservationByBasketRefResponse reservations) {
    return ObjectUtils.allNotNull(hotelInformationResponse, reservations) && ObjectUtils.allNotNull(
        hotelInformationResponse.getHotelCountryCode(),
        reservations.getReservationByIdList());

  }

  private boolean isArrivalDateCompliant(RoomStayByIdResponse room, boolean isOta) {
    LocalDate arrivalDate = LocalDate.parse(room.getArrivalDate(), DATE_TIME_FORMATTER);
    LocalDate currentDate = LocalDate.now();

    long daysBetween = ChronoUnit.DAYS.between(currentDate, arrivalDate);
    return daysBetween >= 0 && daysBetween <= (isOta ? thirdpartyBookingProperties.getDaysWithinArrival() :
            checkInOnlineProperties.getDaysWithinArrival());
  }

  private boolean isDeRegCardEnabled(HotelInformationResponse hotelInformationResponse) {
    if (DE_COUNTRY_CODE.equals(hotelInformationResponse.getHotelCountryCode())) {
      return unleashWrapper.isEnabled(unleashWrapper.featureFlag().getReleasePiBbMobileDeRegCard());
    }
    return true;
  }

  private boolean isPibaPaymentCard(ReservationByBasketRefResponse reservations) {
    var folioView = reservations.getReservationByIdList().get(0).getPaymentCard().getFolioView();
    var isPibaCPExcluded = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getMobileCiolPiba());
    var isPibaCNPExcluded = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getMobileCiolPibaCnp());
    var cardType = reservations.getReservationByIdList().get(0).getPaymentCard().getCardType();
    if (PIBA_LIST.stream().anyMatch(pibaCardType -> pibaCardType.equalsIgnoreCase(cardType))
        && folioView != null) {
      if (FOLIO_VIEW_PIBA_CP_ONE == folioView) {
        return isPibaCPExcluded;
      }
      if (FOLIO_VIEW_PIBA_CNP_TWO == folioView) {
        return isPibaCNPExcluded;
      }
    }
    return false;
  }

  private boolean isPrepaidAllowed(ReservationByBasketRefResponse reservations, String idContext) {
    var isPrepaidEnabled = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getMobileCiolPrepaid3rdParty());
    if (isPrepaidEnabled) {
      return true;
    } else {
      var cardType = reservations.getReservationByIdList().get(0).getPaymentCard().getCardType();
      var isPiba = PIBA_LIST.stream()
          .anyMatch(pibaCardType -> pibaCardType.equalsIgnoreCase(cardType));

      var folioView = reservations.getReservationByIdList().get(0).getPaymentCard().getFolioView();
      var isThirdPartyPrepaid = !isPiba
          && ID_CONTEXT.equalsIgnoreCase(idContext)
          && folioView != null
          && folioView == FOLIO_VIEW_PIBA_CNP_TWO;

      return !isThirdPartyPrepaid;
    }

  }

}
