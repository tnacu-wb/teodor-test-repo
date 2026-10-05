package uk.co.whitbread.reservation.domain.logic.utils;

import static java.util.Objects.isNull;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.COUNTRY_CODE_DE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.COUNTRY_CODE_GB;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C105_ERRORED_BOOKING;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C106_CANCELLED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C107_CHECKED_IN;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C108_CHECKED_OUT;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C109_NO_SHOW;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C118_NEGOTIATED_RATES;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C119_PREPAID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C120_MULTIROOM;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C121_BREAKFAST_ALLOWANCE;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C105_ERRORED_BOOKING;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C106_CANCELLED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C107_CHECKED_IN;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C108_CHECKED_OUT;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C109_NO_SHOW;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_ADVANCE;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_ADVANCE_PID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_FLEXRATE;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_FLEXRATE_PID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX_PID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_SEMIFLEX;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_SEMIFLEX_PID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_STANDARD;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_STANDARD_PID;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_MAXROOMS;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ManageBookingUtils {

  private static final String EMPLOYEE_RATE_PLAN = "EMPLOYEE";
  private static final String TRAVEL_INDUSTRY_RATE_PLAN = "FCDNLR30";
  private static final String BFL_RATE_PLAN_SET = "BFL";
  public static final String NEGOCIATED_RATE = "Negrate";
  private static final String NONFLEX_RATE = "NONFLEX";
  private static final String GUARANTEE_CODE_CC = "CC";
  public static final String EXT_REF_CONTENT_ID = "BART_OHIP";
  public static final List<String> BUSINESS_BOOKER = List.of("42", "92", "91", "93");
  public static final List<String> PI_SOURCE = List.of("44", "54", "40", "70");
  public static final List<String> CCUI_SOURCE = List.of("30", "33");

  public static final String FLEXRATE = "FLEXRATE";
  public static final String FLEX = "Flex";
  public static final String SEMIFLEXRATE = "SEMIFLEX";
  public static final String SEMIFLEX = "Semi-Flex";
  public static final String ADVANCERATE = "ADVANCE";
  public static final String ADVANCE = "Advance";
  public static final String STANDARDRATE = "STANDARD";
  public static final String STANDARD = "Standard";
  public static final String NONFLEXDRATE = "NONFLEXD";
  public static final String NONFLEX = "Non-Flex";
  public static final String NONFLEXRATE = "NONFLEX";
  public static final String NONFXDBBRATE = "NONFXDBB";
  public static final String NONFXDMDRATE = "NONFXDMD";


  public static final String CANCELLED_STATUS = "Cancelled";
  public static final String CHECKED_IN_STATUS = "InHouse";
  public static final String CHECKED_OUT_STATUS = "CheckedOut";
  public static final String NO_SHOW_STATUS = "NoShow";

  public static final List<String> unwantedStatuses =
          Arrays.asList(CANCELLED_STATUS, CHECKED_IN_STATUS, CHECKED_OUT_STATUS, NO_SHOW_STATUS);

  public static String getLastModifyTimestamp(BasketResponse basket) {
    final var lastModifiedAt = basket.getLastModifiedAt();
    if (StringUtils.isEmpty(lastModifiedAt)) {
      return Long.toString(Instant.parse(basket.getCreatedAt()).toEpochMilli());
    }
    return Long.toString(Instant.parse(lastModifiedAt).toEpochMilli());
  }

  //duplicate method
  public static void validateToken(String token, String basketReference) {
    if (StringUtils.isEmpty(token) || (!TokenUtils.isValid(token, basketReference))) {
      var ex = new InvalidTokenException(ErrorCode.DIGITAL_INVALID_TOKEN2, "Invalid token");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  //duplicate method
  public static Set<String> extractReservationIds(BasketResponse basket) {
    return basket.getItems()
            .stream()
            .map(BasketItemResponse::getSourceId)
            .collect(Collectors.toSet());
  }

  public static Integer findDifference(ReservationByIdResponse reservation) {

    LocalDate startDate = LocalDate.parse(reservation.getRoomStay().getArrivalDate());
    LocalDate endDate = LocalDate.parse(reservation.getRoomStay().getDepartureDate());

    return Period.between(startDate, endDate).getDays();
  }

  public static String checkRateBooking(ReservationByBasketRefResponse reservations,
      String basketReference) {
    return reservations.getReservationByIdList()
        .stream()
        .map(reservation -> reservation.getRoomStay().getRatePlanCode())
        .filter(ratePlanCode -> EMPLOYEE_RATE_PLAN.equals(ratePlanCode)
            || TRAVEL_INDUSTRY_RATE_PLAN.equals(ratePlanCode))
        .findFirst()
        .map(ratePlanCode -> {
          String rateType = EMPLOYEE_RATE_PLAN.equalsIgnoreCase(ratePlanCode)
              ? HotelReservationConstants.EMPLOYEE : TRAVEL_INDUSTRY_RATE_PLAN;
          log.info(String.format("%s Rate Booking found for basketReference = %s", rateType,
              sanitize(basketReference)));
          return rateType;
        })
        .orElse("");
  }

  public static boolean isRatePlanSetNegotiatedWithMeals(String ratePlanSet,
                                                   ReservationByBasketRefResponse reservations,
                                                   Set<String> negociatedRatePlanSet) {
    var reservationsWithPackageList = reservations.getReservationByIdList().stream()
            .filter(reservationByIdResponse -> !reservationByIdResponse.getReservationPackageList()
                    .isEmpty())
            .toList();
    boolean mealsIncluded;

    if (!reservationsWithPackageList.isEmpty()) {
      mealsIncluded = !reservationsWithPackageList.stream()
              .filter(reservationByIdResponse -> reservationByIdResponse.getReservationPackageList()
                      .stream()
                      .anyMatch(ManageBookingUtils::isPackageDateNull))
              .toList()
              .isEmpty();
    } else {
      mealsIncluded = false;
    }
    return isRatePlanSetNegotiatedExceptBfl(negociatedRatePlanSet, ratePlanSet) && mealsIncluded;
  }

  public static boolean isPackageDateNull(ReservationPackagesDetailsResponse packageResponse) {
    return (StringUtils.isBlank(packageResponse.getStartDate())
            || packageResponse.getStartDate().equals("null"))
            && (StringUtils.isBlank(packageResponse.getEndDate())
            || packageResponse.getEndDate().equals("null"));
  }

  public static boolean isRatePlanSetNegotiatedExceptBfl(Set<String> negociatedRatePlanSet, String ratePlanSet) {
    return isRatePlanSetNegotiated(negociatedRatePlanSet, ratePlanSet) && !BFL_RATE_PLAN_SET.equals(ratePlanSet);
  }

  public static boolean isRatePlanSetNegotiated(Set<String> negociatedRatePlanSet, String ratePlanSet) {
    return negociatedRatePlanSet.contains(ratePlanSet);
  }

  public static String getWbRoomRate(String ratePlanCode, String ratePlanSet, BookingChannel bookingChannel,
                               Set<String> negociatedRatePlanSet) {
    var wbRoomRate = convertToWbRoomRate(ratePlanCode);
    if ((bookingChannel.isBb() || bookingChannel.isDistr() || bookingChannel.isCcui())
            && ManageBookingUtils.isRatePlanSetNegotiated(negociatedRatePlanSet, ratePlanSet)
            && !wbRoomRate.equals(NONFLEX)) {
      wbRoomRate = NEGOCIATED_RATE;
    }
    return wbRoomRate;
  }

  public static String convertToWbRoomRate(String operaRoomRate) {
    return switch (operaRoomRate) {
      case FLEXRATE -> FLEX;
      case SEMIFLEXRATE -> SEMIFLEX;
      case ADVANCERATE -> ADVANCE;
      case STANDARDRATE -> STANDARD;
      case NONFLEX_RATE, NONFLEXDRATE, NONFXDBBRATE, NONFXDMDRATE -> NONFLEX;
      case EMPLOYEE_RATE_PLAN -> HotelReservationConstants.EMPLOYEE;
      default -> operaRoomRate;
    };
  }

  public static ManageBookingResponse getManageBookingResponseOperaUiAndNegocitedRates() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C118_NEGOTIATED_RATES)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponseOperaUiAndPrepaid() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C119_PREPAID)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponseReturnAllFalse() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(null)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponse(boolean isCancellable,
      boolean isAmendable,
      boolean isRuleCompliant) {
    return getManageBookingResponse(isCancellable, isAmendable, isRuleCompliant, null);
  }

  public static ManageBookingResponse getManageBookingResponse(boolean isCancellable,
      boolean isAmendable,
      boolean isRuleCompliant, String aemLabelKey) {
    return ManageBookingResponse.builder()
        .isCancellable(isCancellable)
        .isAmendable(isAmendable)
        .isRuleCompliant(isRuleCompliant)
        .aemLabelKey(aemLabelKey)
        .build();
  }

  public static ManageBookingResponse getManageBookingResponseOperaUiAndMultiroom() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C120_MULTIROOM)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponseOperaUiAndBreakfastAllowances() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C121_BREAKFAST_ALLOWANCE)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponseOperaAndDoesntCheckRules() {
    return ManageBookingResponse.builder()
        .isCancellable(Boolean.FALSE)
        .isAmendable(Boolean.FALSE)
        .isRuleCompliant(Boolean.FALSE)
        .aemLabelKey(DASHBOARD_BOOKINGS_ERROR_MAXROOMS)
        .build();
  }

  public static ManageBookingResponse getManageBookingResponseErroredBookingPiOrBb() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(DASHBOARD_BOOKINGS_ERROR_C105_ERRORED_BOOKING)
            .build();
  }

  public static ManageBookingResponse getManageBookingResponseErroredBookingCcui() {
    return ManageBookingResponse.builder()
            .isCancellable(Boolean.FALSE)
            .isAmendable(Boolean.FALSE)
            .isRuleCompliant(Boolean.FALSE)
            .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C105_ERRORED_BOOKING)
            .build();
  }

  public static PaymentOption getReservationPaymentOption(ReservationByBasketRefResponse reservation,
                                                    BasketResponse basket) {
    var hasCreditCardGuarantee = reservation.getReservationByIdList().stream()
        .anyMatch(rsv -> GUARANTEE_CODE_CC.equals(rsv.getGuaranteeCode()));

    if (hasCreditCardGuarantee) {
      return PaymentOption.PAY_ON_ARRIVAL;
    } else {
      return basket.getPaymentOption();
    }
  }

  public static boolean isPayNowReservation(PaymentOption paymentOption, BigDecimal amountPaid) {
    if (Objects.isNull(paymentOption)) {
      return BigDecimal.ZERO.compareTo(amountPaid) > 0;
    }
    return PaymentOption.PAY_NOW.equals(paymentOption);
  }

  public static String convertToLocalDateTime(String hotelTimeZone) {
    LocalDateTime now = LocalDateTime.now();
    ZonedDateTime systemZonedDateTime = now.atZone(ZoneId.systemDefault());
    ZonedDateTime hotelZonedDateTime =
            systemZonedDateTime.withZoneSameInstant(ZoneId.of(hotelTimeZone));
    return hotelZonedDateTime.toLocalDateTime()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
  }

  public static boolean containsMigratedReservations(ReservationsDetailsEnhancedResponse operaReservation,
                                               String reservationNumber) {
    return operaReservation.getReservations().getReservationInfo().stream()
            .map(ReservationInfo::getExternalReferences)
            .flatMap(List::stream)
            .filter(ref -> ref.getId().split("-")[0].equals(reservationNumber))
            .anyMatch(ref -> ref.getIdContext().equals(EXT_REF_CONTENT_ID));
  }

  public static CreateBasketRequestDto.PaymentOptionEnum getPaymentOption(
          ReservationsDetailsEnhancedResponse operaRes) {
    return !operaRes.getAmountPaid().equals(BigDecimal.ZERO) ? CreateBasketRequestDto.PaymentOptionEnum.PAY_NOW
            : CreateBasketRequestDto.PaymentOptionEnum.PAY_ON_ARRIVAL;
  }

  public static boolean isValidOperaReservation(
          ReservationsDetailsEnhancedResponse reservationsDetailsEnhancedResponse,
          String channel, String reservationNumber) {
    if (channel.equals(PI_BOOKING_CHANNEL)) {
      var reservations = reservationsDetailsEnhancedResponse.getReservations().getReservationInfo()
              .stream()
              .filter(roomStay -> isNull(roomStay.getRoomStay().getSourceCode())
                      || !BUSINESS_BOOKER.contains(roomStay.getRoomStay().getSourceCode()))
              .toList();
      if (reservations.isEmpty()) {
        return false;
      }
    }
    if (CollectionUtils.isEmpty(
            reservationsDetailsEnhancedResponse.getReservations().getReservationInfo())) {
      log.info("Could not find resNo = {} in Opera.", reservationNumber);
      return false;
    }

    return true;
  }


  public static boolean requestMatchesResDetails(FindBookingRequest findBookingRequest,
                                                 ReservationsDetailsEnhancedResponse operaReservation,
                                                 boolean shouldBypassMatchesOpera) {

    if (operaReservation == null || operaReservation.getReservations() == null
        || operaReservation.getReservations().getReservationInfo() == null
        || operaReservation.getReservations().getReservationInfo().isEmpty()) {
      return false;
    }
    if (shouldBypassMatchesOpera) {
      log.info("Bypassing reservation details matching for booking reference: {}",
          sanitize(findBookingRequest.getResNo()));
      return true;
    }
    boolean nameExists =
        operaReservation.getBilling() != null && operaReservation.getBilling().getLastName()
            .equalsIgnoreCase(findBookingRequest.getLastName());
    if (!nameExists) {
      nameExists = operaReservation.getReservations().getReservationInfo().stream()
              .anyMatch(guest -> guest.getReservationGuest().getSurname()
                      .equalsIgnoreCase(findBookingRequest.getLastName()));
    }
    boolean sameArrivalDate =
            operaReservation.getReservations().getReservationInfo().get(0).getRoomStay()
                    .getArrivalDate().toString()
                    .equals(findBookingRequest.getArrivalDate());

    return nameExists && sameArrivalDate;
  }


  /**
   * Checks if the booking is an OTA booking. A booking is considered OTA if the
   * 'mobile_accepts_ota_booking' feature flag is enabled and the idContext is not in the excluded in configuration
   * (e.g., BOOKING.COM), and channel and subchannel (e.g., PI.MOBILE) matche the configuration.
   *
   * @param bookingChannel      the booking channel
   * @param idContext           the ID context
   * @param providers           the excluded providers
   * @param defSubchannel       the default subchannel
   * @param isAcceptsOtaBooking the feature flag status
   * @return true if the booking is OTA or throws exception if not allowed
   */
  public static boolean isOtaBooking(final BookingChannel bookingChannel, final String idContext,
      final Set<String> providers, final Set<String> defSubchannel,
      final boolean isAcceptsOtaBooking) {

    boolean isProviderAllowed = isProviderAllowed(idContext, providers);
    boolean isOtaAllowed = isOtaBookingsAllowed(bookingChannel, defSubchannel, isAcceptsOtaBooking);
    return isProviderAllowed && isOtaAllowed;
  }

  /**
   * Checks if the provider is allowed based on the given idContext and providers set. Digital
   * booking have idContext=WB_DIGITAL, distribution booking have idContext=WB_DIGITAL, 3rd party
   * booking have other idContext ( ex idContext = Booking.com)
   *
   * @param idContext the idContext
   * @param providers the providers excluded by configuration
   * @return true if the provider is allowed, false otherwise
   */
  public static boolean isProviderAllowed(String idContext, Set<String> providers) {
    return providers == null || providers.isEmpty() || (!providers.contains(idContext));
  }

  public static boolean requestMatchesOperaResDetails(FindBookingRequest findBookingRequest,
                                                      ReservationByBasketRefResponse operaReservations,
                                                      boolean shouldBypassMatchesOpera) {

    if (CollectionUtils.isEmpty(operaReservations.getReservationByIdList())) {
      return false;
    }
    if (shouldBypassMatchesOpera) {
      log.info("Bypassing opera reservation details matching for booking reference: {}",
              sanitize(findBookingRequest.getResNo()));
      return true;
    }
    String bookingRequestLastName = findBookingRequest.getLastName();
    boolean nameExists =
            operaReservations.getReservationByIdList().stream().map(ReservationByIdResponse::getBilling)
                    .filter(Objects::nonNull)
                    .anyMatch(billingResponse -> billingResponse.getLastName()
                            .equalsIgnoreCase(bookingRequestLastName));

    if (!nameExists) {
      nameExists = operaReservations.getReservationByIdList()
              .stream()
              .map(ReservationByIdResponse::getReservationGuestList)
              .flatMap(List::stream)
              .filter(Objects::nonNull)
              .anyMatch(guest -> guest.getSurName().equalsIgnoreCase(bookingRequestLastName));

    }
    boolean sameArrivalDate =
            operaReservations.getReservationByIdList().get(0).getRoomStay().getArrivalDate()
                    .equals(findBookingRequest.getArrivalDate());

    return nameExists && sameArrivalDate;
  }

  public static String getAemLabelKeyFromRatePlanCode(String ratePlanCode,
                                                      String hotelCountryCode) {
    if (COUNTRY_CODE_GB.equals(hotelCountryCode)) {
      return switch (ratePlanCode) {
        case SEMIFLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_SEMIFLEX;
        case ADVANCERATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_ADVANCE;
        case STANDARDRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_STANDARD;
        case NONFLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX;
        case FLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_FLEXRATE;
        default -> null;
      };
    } else if (COUNTRY_CODE_DE.equals(hotelCountryCode)) {
      return switch (ratePlanCode) {
        case SEMIFLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_SEMIFLEX_PID;
        case ADVANCERATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_ADVANCE_PID;
        case STANDARDRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_STANDARD_PID;
        case NONFLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX_PID;
        case FLEXRATE -> DASHBOARD_BOOKINGS_ERROR_CANCEL_FLEXRATE_PID;
        default -> null;
      };
    }
    return null;
  }

  public static String getAemLabelKeyFromUnwantedStatuses(String unwantedStatus,
                                                          BookingChannel bookingChannel) {
    if (bookingChannel.isCcui()) {
      return switch (unwantedStatus) {
        case CANCELLED_STATUS -> CCUI_MANAGE_BOOKING_ERROR_C106_CANCELLED;
        case CHECKED_IN_STATUS -> CCUI_MANAGE_BOOKING_ERROR_C107_CHECKED_IN;
        case CHECKED_OUT_STATUS -> CCUI_MANAGE_BOOKING_ERROR_C108_CHECKED_OUT;
        case NO_SHOW_STATUS -> CCUI_MANAGE_BOOKING_ERROR_C109_NO_SHOW;
        default -> null;
      };
    } else if (bookingChannel.isPi() || bookingChannel.isBb()) {
      return switch (unwantedStatus) {
        case CANCELLED_STATUS -> DASHBOARD_BOOKINGS_ERROR_C106_CANCELLED;
        case CHECKED_IN_STATUS -> DASHBOARD_BOOKINGS_ERROR_C107_CHECKED_IN;
        case CHECKED_OUT_STATUS -> DASHBOARD_BOOKINGS_ERROR_C108_CHECKED_OUT;
        case NO_SHOW_STATUS -> DASHBOARD_BOOKINGS_ERROR_C109_NO_SHOW;
        default -> null;
      };
    }

    return null;
  }

  public static boolean isOtaBookingsAllowed(BookingChannel bookingChannel,
      Set<String> defSubchannel, boolean isAcceptsOtaBooking) {
    boolean isOta = false;
    if (bookingChannel.getChannel() != null) {
      String subchannel =
          bookingChannel.getSubchannel() != null ? String.format("%s.%s",
              bookingChannel.getChannel(),
              bookingChannel.getSubchannel()) : bookingChannel.getChannel();
      if (defSubchannel != null && !defSubchannel.isEmpty()) {
        isOta = defSubchannel.contains(subchannel) && isAcceptsOtaBooking;
      }
    }
    return isOta;
  }

}
