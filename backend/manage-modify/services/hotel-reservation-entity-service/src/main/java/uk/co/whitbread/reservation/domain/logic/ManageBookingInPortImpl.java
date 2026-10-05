package uk.co.whitbread.reservation.domain.logic;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum.FAILED;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum.OPEN;
import static uk.co.whitbread.reservation.domain.logic.AmendLogicInPortImpl.getTotal;
import static uk.co.whitbread.reservation.domain.logic.ManageBookingLogic.THIRD_PARTY_RESERVATION_IS_NOT_ALLOWED;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C110_MIGRATED_BOOKING;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C111_CANCEL_A2C;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C114_AMEND_PIBA;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C116_AMEND_NEG_MEAL;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C110_MIGRATED_BOOKING;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_C115_AMEND_BOOKER;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX;
import static uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants.DASHBOARD_BOOKINGS_ERROR_MAXROOMS;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.CANCELLED_STATUS;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.CHECKED_IN_STATUS;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.EXT_REF_CONTENT_ID;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.containsMigratedReservations;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.getAemLabelKeyFromUnwantedStatuses;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.isOtaBooking;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.isOtaBookingsAllowed;
import static uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils.unwantedStatuses;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.PI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.utils.SanitizingUtils.sanitize;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.util.Pair;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.AddBasketItemTypeDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto.StatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ChannelInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.BasketStatusEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestDto.PaymentOptionEnum;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.CreateBasketRequestReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.PaymentInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.ReservationBasketInfoDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.UserInfoDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.exceptions.GenericBadRequestException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.HotelReservationOhipException;
import uk.co.whitbread.reservation.domain.logic.utils.ManageBookingUtils;
import uk.co.whitbread.reservation.domain.logic.utils.UserDefinedFieldsConstants;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CdhSearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.PaymentOption;
import uk.co.whitbread.reservation.domain.model.in.SearchBookingsRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRule;
import uk.co.whitbread.reservation.domain.model.out.CancelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.CdhResults;
import uk.co.whitbread.reservation.domain.model.out.CharacterUDFs;
import uk.co.whitbread.reservation.domain.model.out.Classifications;
import uk.co.whitbread.reservation.domain.model.out.DepositFoliosResponse;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.HotelInformationResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.RatePlan;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationInfo;
import uk.co.whitbread.reservation.domain.model.out.ReservationsDetailsEnhancedResponse;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.Rooms;
import uk.co.whitbread.reservation.domain.model.out.RulesAmendmentResponse;
import uk.co.whitbread.reservation.domain.model.out.SearchBookingsResponse;
import uk.co.whitbread.reservation.domain.model.out.UniqueIDType;
import uk.co.whitbread.reservation.domain.model.out.UserDefinedFields;
import uk.co.whitbread.reservation.domain.model.searchrules.out.SearchRules;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.CdhSearchBookingOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.domain.properties.BusinessBookerConfigProperties;
import uk.co.whitbread.reservation.domain.properties.ThirdpartyBookingProperties;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RequiredArgsConstructor
public class ManageBookingInPortImpl implements ManageBookingInPort {

  public static final String OPERA = "Opera";
  public static final List<String> BUSINESS_BOOKER = List.of("42", "92", "91", "93");
  public static final String CITYTAX = "CITYTAX";
  private static final String EMPLOYEE_RATE_PLAN = "EMPLOYEE";
  private static final String TRI_RATE_PLAN = "FCDNLR30";
  private static final List<String> PIBA_CARD_TYPE = List.of("ZZ", "BU", "BD");
  private static final String NONFLEX_RATE = "NONFLEX";
  private static final String SOURCE_TYPE_ALLOWANCE = "ALLOWANCE";
  private static final String TARGET_ID_BREAKFAST = "BREAK";
  private static final String ID_CONTEXT = "3rd Party";
  private static final String THIRD_PARTY_RESERVATION_COULD_NOT_MATCH =
      "Third-party reservation could not match reservation with what it found.";
  private final HotelReservationOhipOutPort hotelReservationOhipOutPort;
  private final BasketOutPort basketOutPort;
  private final RulesOutPort rulesOutPort;
  private final ContentOutPort contentOutPort;
  private final AmendLogicInPort amendLogic;
  private final AuthenticatedUserService authenticatedUserService;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final BusinessBookerConfigProperties businessBookerConfigProperties;
  private final CdhSearchBookingOutPort cdhSearchBookingOutPort;
  private final ManageBookingLogic manageBookingLogic;
  private final CheckInOnlineLogic checkInOnlineLogic;
  private final CheckOutOnlineLogic checkOutOnlineLogic;
  private final DigitalKeyFeature digitalKeyFeature;
  private final ThirdpartyBookingProperties otaBookingProperties;

  private boolean checkRules(SearchRules rules, ReservationByBasketRefResponse reservations) {
    return checkMaxRoomsRule(rules, reservations) && checkMaxNightsRule(rules, reservations);
  }

  private boolean checkMaxRoomsRule(SearchRules rules, ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList().size() <= rules.getMaxRooms();
  }

  private boolean checkMaxRoomsAmendRule(SearchRules rules, ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList().size() <= rules.getMaxRoomsAmend();
  }

  private boolean checkMaxNightsRule(SearchRules rules, ReservationByBasketRefResponse reservations) {
    return ManageBookingUtils.findDifference(reservations.getReservationByIdList().get(0)) <= rules.getMaxNights();
  }

  private SearchRules fetchRules(String channel, String ratePlanCode) {
    String channelInput = Stream.of(EMPLOYEE_RATE_PLAN, TRI_RATE_PLAN)
        .filter(plan -> StringUtils.equalsIgnoreCase(plan, ratePlanCode))
        .findFirst()
        .orElse(channel);

    Integer maxRooms;
    Integer maxRoomsAmend;
    Integer numberOfNights;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())) {
      var searchRules = contentOutPort.getSearchRules(channelInput, Optional.empty());
      maxRooms = searchRules.getMaxRooms();
      maxRoomsAmend = searchRules.getMaxRoomsAmend();
      numberOfNights = searchRules.getMaxNights();
    } else {
      maxRooms = rulesOutPort.getMaxRoomsRule(channelInput).getMaxRooms();
      maxRoomsAmend = maxRooms;
      numberOfNights = rulesOutPort.getMaxNightsRule(channel).getMaxNights();
    }

    return SearchRules.builder()
        .maxRooms(maxRooms)
        .maxRoomsAmend(maxRoomsAmend)
        .maxNights(numberOfNights).build();
  }

  private String getRatePlanSet(String ratePlanCode, String hotelId) {
    var ratePlansResponse = hotelReservationOhipOutPort.getRatePlans(
        Collections.singletonList(ratePlanCode), hotelId);

    return ratePlansResponse
        .getRatePlans()
        .stream()
        .findFirst()
        .map(RatePlan::getClassifications)
        .map(Classifications::getDisplaySet)
        .orElse(null);
  }

  @Override
  public ManageBookingResponse getManageBookingInformation(
      String hotelId, String basketReference,
      String userDateTime, String token, BookingChannel bookingChannel, boolean isTokenMandatory,
      ReservationByBasketRefResponse originalReservations) {

    log.info("Entered getManageBookingInformation with hotelId={}, basketReference={}, "
            + "userDateTime={}, token={}, bookingChannel={}",
        hotelId, basketReference, userDateTime, token, bookingChannel);

    if (isTokenMandatory && !authenticatedUserService.isUserAuthenticated()) {
      ManageBookingUtils.validateToken(token, basketReference);
    }

    var basket = basketOutPort.getBasketById(basketReference);
    var reservationsIds = ManageBookingUtils.extractReservationIds(basket);
    var reservations = getReservations(originalReservations, basket.getHotelId(),
        reservationsIds);

    var ratePlanCodeSet = reservations.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getRoomStay)
        .filter(Objects::nonNull)
        .map(RoomStayByIdResponse::getRatePlanCode)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    var rulesSet = ratePlanCodeSet.stream()
        .map(ratePlanCode -> fetchRules(bookingChannel.getChannel(), ratePlanCode))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    var hotelInformationResponse = hotelReservationOhipOutPort.getHotelInformation(hotelId);
    var isCoolAvailable = checkOutOnlineLogic.isCoolAvailable(reservations,
        hotelInformationResponse, basket);

    var checkedRules = rulesSet.stream()
        .collect(Collectors.partitioningBy(rules -> checkRules(rules, reservations)));
    List<SearchRules> notCompliant = checkedRules.get(false);

    boolean isRuleCompliant = notCompliant.isEmpty();

    var isDigitalKey = digitalKeyFeature.isDigitalKeyAvailable(reservations);

    if (hasUnwantedStatuses(reservations)) {
      return buildUnwantedStatusResponse(basketReference, bookingChannel, reservations, isRuleCompliant,
          isCoolAvailable, isDigitalKey);
    }

    if (checkInOnlineLogic.shouldDisableCiol(basket)) {
      return buildDisableCiolResponse(isCoolAvailable, isRuleCompliant, isDigitalKey);
    }

    var cancelInformation = hotelReservationOhipOutPort.getCancelInformation(hotelId, reservationsIds, userDateTime);
    log.info("Received cancelInformation={} for basketReference={}", cancelInformation, basketReference);

    var ratePlanCode = ratePlanCodeSet.stream().findFirst().get();
    var ratePlanSet = getRatePlanSet(ratePlanCode, hotelId);

    var isOtaAllowed = isOtaAllowed(basket.getIdContext(), bookingChannel);

    Pair<Boolean, String> ciolResult = checkInOnlineLogic.isCiolAvailable(reservations,
        hotelInformationResponse, isOtaAllowed, basket.getIdContext());

    var amendInformation =
        isOtaAllowed ? RulesAmendmentResponse.builder().isBookingAmendable(false).build()
            : getAmendableInformation(hotelId, reservations, ratePlanCode, basket, bookingChannel);

    // get manage booking response for employee or travel industry ratePlanCode
    String reservationRateType = ManageBookingUtils.checkRateBooking(reservations, basketReference);
    if (StringUtils.isNotEmpty(reservationRateType) && isRuleCompliant) {
      var manageBookingResponse = getManageBookingResponse(cancelInformation, amendInformation, basketReference);
      setCiolAndCoolFlags(manageBookingResponse, ciolResult, isCoolAvailable);
      setDigitalKeyFlag(manageBookingResponse, isDigitalKey);
      return manageBookingResponse;
    }
    if (manageBookingLogic.operaUiRsv(basket.getBookingReference())) {
      boolean excludeNegRatePlanCheck =
          isOtaAllowed && "MOBILE".equals(bookingChannel.getSubchannel());
      if (isRatePlanSetNegotiated(ratePlanSet) && !excludeNegRatePlanCheck) {
        var manageBookingNegocitedRatesResponse = ManageBookingUtils.getManageBookingResponseOperaUiAndNegocitedRates();
        manageBookingNegocitedRatesResponse.setCheckInOnlineAvailable(ciolResult.getFirst());
        manageBookingNegocitedRatesResponse.setCiolErrorLabelKey(ciolResult.getFirst() ? null : ciolResult.getSecond());
        setDigitalKeyFlag(manageBookingNegocitedRatesResponse, isDigitalKey);
        return manageBookingNegocitedRatesResponse;
      } else {
        var multiRoomReservation = findMultiRoomReservation(reservations);
        var checkGuestNumber = findReservationWithMultipleGuests(reservations);
        boolean isPayedNow = PaymentOption.PAY_NOW.equals(basket.getPaymentOption());

        if (Boolean.TRUE.equals(bookingChannel.isCcui())) {
          if (isPayedNow) {
            return ManageBookingUtils.getManageBookingResponseOperaUiAndPrepaid();
          }

          if (multiRoomReservation.isPresent()) {
            return ManageBookingUtils.getManageBookingResponseOperaUiAndMultiroom();
          }

          if (hasBreakfastAllowances(basket)) {
            return ManageBookingUtils.getManageBookingResponseOperaUiAndBreakfastAllowances();
          }

          if (checkGuestNumber.isPresent()) {
            return ManageBookingUtils.getManageBookingResponseReturnAllFalse();
          }
        }
        if (Boolean.TRUE.equals(bookingChannel.isPi()) && (isPayedNow
            || multiRoomReservation.isPresent()
            || hasBreakfastAllowances(basket)
            || checkGuestNumber.isPresent())) {
          var manageBookingResponse = ManageBookingUtils.getManageBookingResponseReturnAllFalse();
          setCiolAndCoolFlags(manageBookingResponse, ciolResult, isCoolAvailable);
          setDigitalKeyFlag(manageBookingResponse, isDigitalKey);
          return manageBookingResponse;
        }
      }
    }

    // disabling any amend and cancel action if checkRules = false
    if (!isRuleCompliant) {
      return nonCompliantManageBookingResponse(bookingChannel, ciolResult, isCoolAvailable, isDigitalKey);
    }

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getAemSearchRules())
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMaxRoomsAmend())
        && (!isOtaAllowed)
        && !checkMaxRoomsAmendRule(rulesSet.stream().findFirst().get(), reservations)) {
      return nonCompliantMaxRoomsAmendManageBookingResponse(bookingChannel, cancelInformation,
          ciolResult, isCoolAvailable, isDigitalKey);
    }

    // disabling any amend and cancel action if basket.isErroredBooking = true
    if (basket.isErroredBooking()) {
      return erroredBookingResponse(basketReference, bookingChannel, true, basket);
    }

    if ((bookingChannel.isBb() && !isUserSameAsBooker(reservations))) {
      return bbManageBookingResponse(bookingChannel, reservations, cancelInformation,
          ciolResult, isCoolAvailable);
    }

    if (PaymentOption.ACCOUNT_COMPANY.equals(basket.getPaymentOption())) {
      return getManageBookingResponseA2C(bookingChannel, basket, cancelInformation);
    }

    var defaultManageBookingResponse = getDefaultManageBookingResponse(cancelInformation,
        amendInformation, reservations, basket, reservationsIds, ratePlanCode,
        bookingChannel.isCcui(), hotelInformationResponse);

    setCiolAndCoolFlags(defaultManageBookingResponse, ciolResult, isCoolAvailable);
    setDigitalKeyFlag(defaultManageBookingResponse, isDigitalKey);

    if (Boolean.TRUE.equals(bookingChannel.isCcui())) {
      return getManageBookingResponseCcui(cancelInformation,
          defaultManageBookingResponse, ratePlanSet, reservations, basket.getPaymentOption(),
          ratePlanCode, basketReference);
    }

    log.info("isCancellable response is {}, isAmendable response is {} for basketReference={}",
        defaultManageBookingResponse.getIsCancellable(),
        defaultManageBookingResponse.getIsAmendable(), basketReference);

    if (isOtaAllowed) {
      defaultManageBookingResponse.setIsAmendable(false);
      defaultManageBookingResponse.setIsCancellable(false);
    }

    return defaultManageBookingResponse;
  }

  private boolean isOtaAllowed(String idContext, BookingChannel bookingChannel) {
    var isOta = ID_CONTEXT.equals(idContext);
    if (isOta) {
      var isOtaFlag = unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getMobileAcceptsOtaBooking());
      return isOtaBookingsAllowed(bookingChannel, otaBookingProperties.getSubchannel(),
          isOtaFlag);
    }
    return false;
  }

  private ManageBookingResponse bbManageBookingResponse(BookingChannel bookingChannel,
      ReservationByBasketRefResponse reservations, CancelInformationResponse cancelInformation,
      Pair<Boolean, String> ciolResult, boolean isCoolAvailable) {
    log.info("BookingChannel=BB && !isUserSameAsBooker={} then "
            + "isCancellable={}, isAmendable=false",
        (bookingChannel.isBb() && !isUserSameAsBooker(reservations)),
        cancelInformation.getIsCancellable());

    return ManageBookingResponse.builder()
        .isCancellable(cancelInformation.getIsCancellable())
        .isAmendable(Boolean.FALSE)
        .isRuleCompliant(Boolean.TRUE)
        .aemLabelKey(DASHBOARD_BOOKINGS_ERROR_C115_AMEND_BOOKER)
        .isCheckInOnlineAvailable(ciolResult.getFirst())
        .isCheckOutOnlineAvailable(isCoolAvailable)
        .ciolErrorLabelKey(ciolResult.getFirst() ? null : ciolResult.getSecond())
        .build();
  }

  private static ManageBookingResponse erroredBookingResponse(String basketReference,
      BookingChannel bookingChannel, boolean isRuleCompliant, BasketResponse basket) {
    log.info("!checkRules={} , isErroredBooking={} for basketReference={} then "
            + "isCancellable=false, isAmendable=false",
        !isRuleCompliant, basket.isErroredBooking(), basketReference);

    return Boolean.TRUE.equals(bookingChannel.isCcui())
        ? ManageBookingUtils.getManageBookingResponseErroredBookingCcui()
        : ManageBookingUtils.getManageBookingResponseErroredBookingPiOrBb();
  }

  private ManageBookingResponse nonCompliantManageBookingResponse(BookingChannel bookingChannel,
      Pair<Boolean, String> ciolResult, boolean isCoolAvailable, boolean isDigitalKey) {
    if ((bookingChannel.isBb() || bookingChannel.isPi())) {
      var manageBookingResponse = ManageBookingUtils.getManageBookingResponseOperaAndDoesntCheckRules();
      setCiolAndCoolFlags(manageBookingResponse, ciolResult, isCoolAvailable);
      setDigitalKeyFlag(manageBookingResponse, isDigitalKey);
      return manageBookingResponse;
    }
    return ManageBookingUtils.getManageBookingResponseReturnAllFalse();
  }

  private ManageBookingResponse nonCompliantMaxRoomsAmendManageBookingResponse(BookingChannel bookingChannel,
      CancelInformationResponse cancelInformation, Pair<Boolean, String> ciolResult,
      boolean isCoolAvailable, boolean isDigitalKey) {
    var aemLabel = (bookingChannel.isBb() || bookingChannel.isPi()) ? DASHBOARD_BOOKINGS_ERROR_MAXROOMS : null;
    var manageBookingResponse = ManageBookingResponse.builder()
        .isCancellable(cancelInformation.getIsCancellable())
        .isAmendable(false)
        .isRuleCompliant(Boolean.TRUE)
        .aemLabelKey(aemLabel).build();
    setCiolAndCoolFlags(manageBookingResponse, ciolResult, isCoolAvailable);
    setDigitalKeyFlag(manageBookingResponse, isDigitalKey);
    return manageBookingResponse;
  }

  private boolean isRatePlanSetNegotiated(String ratePlanSet) {
    return ManageBookingUtils.isRatePlanSetNegotiated(
        businessBookerConfigProperties.getNegociatedRatePlanSet(),
        ratePlanSet);
  }

  private static Optional<ReservationByIdResponse> findReservationWithMultipleGuests(
      ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList()
        .stream()
        .filter(reservation -> reservation.getRoomStay().getAdultsNumber() > 1)
        .findAny();
  }

  private static Optional<ReservationByIdResponse> findMultiRoomReservation(
      ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList()
        .stream()
        .filter(reservationByIdResponse ->
            Boolean.TRUE.equals(reservationByIdResponse.getOperaLinkedReservation()))
        .findAny();
  }

  private static ManageBookingResponse buildDisableCiolResponse(boolean isCoolAvailable,
                                                                boolean isRuleCompliant, boolean isDigitalKey) {
    return ManageBookingResponse.builder()
        .isCancellable(false)
        .isAmendable(false)
        .isRuleCompliant(isRuleCompliant)
        .aemLabelKey(null)
        .isCheckOutOnlineAvailable(isCoolAvailable)
        .isDigitalKey(isDigitalKey)
        .build();
  }

  private boolean hasUnwantedStatuses(ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList().stream()
        .anyMatch(reservation -> unwantedStatuses.contains(reservation.getReservationStatus()));
  }

  private static ManageBookingResponse buildUnwantedStatusResponse(String basketReference,
      BookingChannel bookingChannel,
      ReservationByBasketRefResponse reservations, boolean isRuleCompliant, boolean isCoolAvailable,
                                                                   boolean isDigitalKey) {
    var reservationsWithUnwantedStatus = reservations.getReservationByIdList().stream()
        .filter(reservation -> unwantedStatuses.contains(reservation.getReservationStatus()))
        .map(reservation -> String.format("reservation id=%s has status=%s",
            reservation.getReservationId(), reservation.getReservationStatus()))
        .toList();
    log.info("{} for basketReference={} then isCancellable=false, isAmendable=false",
            reservationsWithUnwantedStatus, basketReference);

    String unwantedStatus = reservations.getReservationByIdList().get(0) != null
        ? reservations.getReservationByIdList().get(0).getReservationStatus()
        : StringUtils.EMPTY;

    return ManageBookingResponse.builder()
        .isCancellable(false)
        .isAmendable(false)
        .isRuleCompliant(isRuleCompliant)
        .aemLabelKey(getAemLabelKeyFromUnwantedStatuses(unwantedStatus, bookingChannel))
        .isCheckOutOnlineAvailable(isCoolAvailable)
        .isDigitalKey(CHECKED_IN_STATUS.equals(unwantedStatus) ? isDigitalKey : false)
        .build();
  }

  private ManageBookingResponse getManageBookingResponseA2C(BookingChannel bookingChannel,
      BasketResponse basket, CancelInformationResponse cancelInformationResponse) {
    boolean isCcuiChannel =
        bookingChannel.isCcui() && CCUI_BOOKING_CHANNEL.equals(basket.getChannel());
    log.info(String.format(
        "A2C basketReference=%s - isCancellable=%s, isAmendable=%s",
        basket.getReference(),
        cancelInformationResponse.getIsCancellable(),
        isCcuiChannel));

    if (Boolean.TRUE.equals(cancelInformationResponse.getIsCancellable())) {
      return ManageBookingResponse.builder()
              .isCancellable(Boolean.TRUE)
              .isAmendable(Boolean.TRUE)
              .isRuleCompliant(Boolean.TRUE)
              .aemLabelKey(null)
              .build();
    }

    return ManageBookingResponse.builder()
              .isCancellable(Boolean.FALSE)
              .isAmendable(isCcuiChannel)
              .isRuleCompliant(Boolean.TRUE)
              .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C111_CANCEL_A2C)
              .build();
  }

  private boolean hasBreakfastAllowances(BasketResponse basket) {
    var bookingAllowancesFromBasket = basket.getBookingAllowances();
    var businessAllowancesRules = rulesOutPort.getBusinessAllowanceRules();
    Set<String> allowancesTargetIds = new HashSet<>();

    if (Objects.nonNull(bookingAllowancesFromBasket)) {
      bookingAllowancesFromBasket.forEach(bookingAllowance -> {
        var targetIds = businessAllowancesRules.getBusinessAllowances()
                .stream()
                .filter(businessAllowanceRule -> businessAllowanceRule.getSourceId()
                        .equals(bookingAllowance.getAllowance())
                        && SOURCE_TYPE_ALLOWANCE.equals(businessAllowanceRule.getSourceType()))
                .map(BusinessAllowanceRule::getTargetId)
                .toList();

        if (!targetIds.isEmpty()) {
          allowancesTargetIds.addAll(targetIds);
        }
      });
    }
    return allowancesTargetIds.contains(TARGET_ID_BREAKFAST);
  }

  private ReservationByBasketRefResponse getReservations(ReservationByBasketRefResponse originalReservations,
                                                         String hotelId,
                                                         Set<String> reservationsIds) {
    var reservations = originalReservations;
    if (Objects.isNull(reservations)) {
      reservations = hotelReservationOhipOutPort
              .getReservationsByIds(hotelId, reservationsIds.stream().toList(), false);
    }
    return reservations;
  }

  private ManageBookingResponse getDefaultManageBookingResponse(CancelInformationResponse cancelInformationResponse,
                                                                RulesAmendmentResponse amendInformationResponse,
                                                                ReservationByBasketRefResponse reservations,
                                                                BasketResponse basket, Set<String> reservationsIds,
                                                                String ratePlanCode,
                                                                boolean isCcui,
                                                                HotelInformationResponse hotelInformationResponse) {

    boolean isAmendable = isAmendable(ManageBookingUtils.getReservationPaymentOption(reservations, basket),
            reservations.getAmountPaid(),
            reservationsIds);

    var aemLabelKeyNonAmendable = isCcui
        ? CCUI_MANAGE_BOOKING_ERROR_C110_MIGRATED_BOOKING
        : DASHBOARD_BOOKINGS_ERROR_C110_MIGRATED_BOOKING;

    return isAmendable ? ManageBookingResponse.builder()
            .isCancellable(cancelInformationResponse.getIsCancellable())
            .isAmendable(amendInformationResponse.getIsBookingAmendable())
            .isRuleCompliant(Boolean.TRUE)
            .aemLabelKey(
              ManageBookingUtils.getAemLabelKeyFromRatePlanCode(ratePlanCode,
                  hotelInformationResponse.getHotelCountryCode()))
        .build() :
            ManageBookingResponse.builder()
                    .isCancellable(cancelInformationResponse.getIsCancellable())
                    .isAmendable(Boolean.FALSE)
                    .isRuleCompliant(Boolean.TRUE)
                    .aemLabelKey(aemLabelKeyNonAmendable)
                .build();
  }

  private ManageBookingResponse getManageBookingResponseCcui(CancelInformationResponse cancelInformationResponse,
                                ManageBookingResponse defaultManageBookingResponse, String ratePlanSet,
                                ReservationByBasketRefResponse reservations, PaymentOption paymentOption,
                                String ratePlanCode, String basketReference) {

    var cardType = reservations.getReservationByIdList().get(0).getPaymentCard() != null
        ? reservations.getReservationByIdList().get(0).getPaymentCard().getCardType() : "";

    // if channel is CCUI and reservation was overridden, set flags to true
    var reservationOverridden = reservations.getReservationByIdList().get(0)
        .isReservationOverridden();
    var isPIBA = PIBA_CARD_TYPE.contains(cardType)
        && !unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAmendPiba());
    var isRatePlanSet = ManageBookingUtils.isRatePlanSetNegotiatedWithMeals(ratePlanSet,
        reservations,
        businessBookerConfigProperties.getNegociatedRatePlanSet());

    if (isPIBA) {
      log.info("isPIBA = true");
      return ManageBookingResponse.builder()
          .isCancellable(reservationOverridden ? Boolean.TRUE : cancelInformationResponse.getIsCancellable())
          .isAmendable(Boolean.FALSE)
          .isRuleCompliant(Boolean.TRUE)
          .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C114_AMEND_PIBA)
          .build();
    }

    if (isRatePlanSet) {
      log.info("isRatePlanSet = true");

      return ManageBookingResponse.builder()
              .isCancellable(reservationOverridden ? Boolean.TRUE : cancelInformationResponse.getIsCancellable())
              .isAmendable(Boolean.FALSE)
              .isRuleCompliant(Boolean.TRUE)
              .aemLabelKey(CCUI_MANAGE_BOOKING_ERROR_C116_AMEND_NEG_MEAL)
              .build();
    }

    if (ManageBookingUtils.isPayNowReservation(paymentOption, reservations.getAmountPaid())
            && ratePlanCode.equalsIgnoreCase(NONFLEX_RATE)) {
      log.info(String.format(
          "isRatePlanSetNegotiatedWithMeals=false, "
              + "isPayNowReservation or NONFLEX_RATE =%s basketReference=%s then "
              + "isCancellable=%s, isAmendable=false",
          ManageBookingUtils.isPayNowReservation(paymentOption, reservations.getAmountPaid())
              && ratePlanCode.equalsIgnoreCase(NONFLEX_RATE),
          sanitize(basketReference),
          reservationOverridden ? Boolean.TRUE : cancelInformationResponse.getIsCancellable())
      );

      return ManageBookingResponse.builder()
              .isCancellable(reservationOverridden ? Boolean.TRUE : cancelInformationResponse.getIsCancellable())
              .isAmendable(Boolean.FALSE)
              .isRuleCompliant(Boolean.TRUE)
              .aemLabelKey(DASHBOARD_BOOKINGS_ERROR_CANCEL_NONFLEX)
              .build();
    }

    if (reservationOverridden) {
      log.info(String.format(
          "reservationOverridden=true, basketReference=%s then isCancellable=true, isAmendable=true",
          sanitize(basketReference)));

      return ManageBookingResponse.builder()
              .isCancellable(Boolean.TRUE)
              .isAmendable(Boolean.TRUE)
              .isRuleCompliant(Boolean.TRUE)
              .aemLabelKey(null)
              .build();
    }

    return defaultManageBookingResponse;
  }


  private ManageBookingResponse getManageBookingResponse(CancelInformationResponse cancelInformation,
      RulesAmendmentResponse amendInformation, String basketReference) {

    log.info(String.format("ManageBookingResponse for basketReference=%s - isCancellable=%s, isAmendable=%s",
        basketReference, cancelInformation.getIsCancellable(), amendInformation.getIsBookingAmendable()));

    return ManageBookingResponse.builder()
        .isCancellable(cancelInformation.getIsCancellable())
        .isAmendable(amendInformation.getIsBookingAmendable())
        .isRuleCompliant(Boolean.TRUE)
        .aemLabelKey(null).build();
  }

  private boolean isUserSameAsBooker(ReservationByBasketRefResponse reservations) {
    var optionalAccount = authenticatedUserService.getCurrentUserAccount();
    if (optionalAccount.isPresent()) {
      var account = optionalAccount.get();

      var bookerEmployeeAccountId = reservations.getReservationByIdList().stream()
          .findFirst()
          .map(ReservationByIdResponse::getUserDefinedFields)
          .map(UserDefinedFields::getCharacterUDFs)
          .flatMap(characterUDFs -> characterUDFs.stream()
              .filter(udf -> UserDefinedFieldsConstants.USER_ACCOUNT_ID_UDFC_35.equals(udf.getName()))
              .findFirst())
          .map(CharacterUDFs::getValue)
          .orElse(null);

      log.info("LoggedInEmployeeAccountId:{}, bookerEmployeeAccountId:{}", account.getEmployeeId(),
          bookerEmployeeAccountId);
      return Objects.nonNull(account.getEmployeeId()) && account.getEmployeeId().equals(bookerEmployeeAccountId);
    }
    return false;
  }

  private boolean isAmendable(PaymentOption paymentOption, BigDecimal amountPaid,
      Set<String> reservationIds) {
    if (ManageBookingUtils.isPayNowReservation(paymentOption, amountPaid)) {
      return reservationIds.stream()
          .filter(reservationId -> Objects.isNull(basketOutPort.getCharges(reservationId)))
          .findFirst()
          .isEmpty();
    }
    return Boolean.TRUE;
  }

  private RulesAmendmentResponse getAmendableInformation(final String hotelId,
      final ReservationByBasketRefResponse reservations, String ratePlanCode,
      final BasketResponse basket, BookingChannel bookingChannel) {

    var ratePlanSet = getRatePlanSet(ratePlanCode, hotelId);

    final var rateType = ManageBookingUtils.getWbRoomRate(ratePlanCode, ratePlanSet,
        bookingChannel,
        businessBookerConfigProperties.getNegociatedRatePlanSet());

    var hotelInformationResponse = hotelReservationOhipOutPort.getHotelInformation(hotelId);

    var arrivalDate = LocalDate.parse(reservations.getReservationByIdList().get(0).getRoomStay()
        .getArrivalDate()).format(DateTimeFormatter.BASIC_ISO_DATE);
    var hotelLocalDateTime = ManageBookingUtils.convertToLocalDateTime(hotelInformationResponse.getHotelTimeZone());

    var cancelledReservationsList = reservations.getReservationByIdList().stream()
            .filter(reservationByIdResponse -> CANCELLED_STATUS.equals(reservationByIdResponse.getReservationStatus()))
            .toList();

    var rulesAmendmentResponse =
        rulesOutPort.isBookingAmendable(rateType, arrivalDate, hotelLocalDateTime,
            hotelInformationResponse.getHotelCountryCode());

    if (cancelledReservationsList.size() == reservations.getReservationByIdList().size()
        || CANCELLED_STATUS.equals(basket.getStatus())) {
      rulesAmendmentResponse.setIsBookingAmendable(false);
    }

    log.info(String.format("Received amendInformation=%s for basketReference=%s",
        rulesAmendmentResponse, basket.getReference()));

    return rulesAmendmentResponse;
  }

  @Override
  public FindBookingResponse findBooking(FindBookingRequest findBookingRequest,
      BookingChannel bookingChannel) {
    //TODO treat the 404-NotFound case by throwing a pretty-format exception

    log.debug("Entered findBooking with resNo={}", findBookingRequest.getResNo());

    if (isNull(bookingChannel.getChannel()) || bookingChannel.getChannel().isEmpty()) {
      bookingChannel.setChannel(PI_BOOKING_CHANNEL);
    }
    var isOperaConfirmAllowed = manageBookingLogic.isSearchFlowEnabled();
    GenericBadRequestException invalidOtaException = null;
    try {
      findBookingRequest.setResNo(manageBookingLogic.replaceHotelId(findBookingRequest.getResNo()));

      if (isEligibleForBasketSearch(findBookingRequest, isOperaConfirmAllowed)) {
        var result = findBookingByBasketAndOpera(findBookingRequest, bookingChannel);
        if (result.isPresent()) {
          return result.get();
        }
      }
    } catch (GenericBadRequestException exception) {
      //basket finds the reservation by id, but details do not match the search
      ExceptionLogger.log(log, exception, "Failed to match the returned status.");
      if (ErrorCode.DIGITAL_INVALID_OTA_EXCEPTION.getCode() == exception.getErrorCode()
          || ErrorCode.DIGITAL_NOT_MATCH_OTA_EXCEPTION.getCode() == exception.getErrorCode()) {
        invalidOtaException = exception;
      } else {
        return null;
      }
    } catch (RuntimeException exception) {
      ExceptionLogger.log(log, exception, "Failed to find the booking by reference id.");
    }

    if (invalidOtaException != null) {
      throw invalidOtaException;
    }

    return findBookingInOpera(findBookingRequest, bookingChannel, isOperaConfirmAllowed);
  }

  @Override
  public SearchBookingsResponse searchBookings(SearchBookingsRequest searchBookingsRequest) {

    log.debug(
        "Entered searchBookings with bookingRef={}, bookerLastName={}, arrivalDate={}, hotelId= {}, "
            + "cancellationDate={}, bookerPhone={}, bookerEmail={}, bookerPostcode={}, "
            + "companyName={}, guestLastName={}, thirdPartyBookingReferenceNumber={},"
            + "offset={}, limit={}",
        searchBookingsRequest.getBookingReference(), searchBookingsRequest.getBookerLastName(),
        searchBookingsRequest.getArrivalDate(), searchBookingsRequest.getHotelId(),
        searchBookingsRequest.getCancellationDate(), searchBookingsRequest.getBookerPhone(),
        searchBookingsRequest.getBookerEmail(), searchBookingsRequest.getBookerPostcode(),
        searchBookingsRequest.getCompanyName(), searchBookingsRequest.getGuestLastName(),
        searchBookingsRequest.getThirdPartyBookingReferenceNumber(),
        searchBookingsRequest.getOffset(), searchBookingsRequest.getLimit());

    var searchBookingsResponse = hotelReservationOhipOutPort.searchBookings(searchBookingsRequest);

    if ((searchBookingsResponse.getBookings() != null && !searchBookingsResponse.getBookings()
        .isEmpty())
        || searchBookingsResponse.isResponseLimitExceeded()) {
      return searchBookingsResponse;
    }

    return SearchBookingsResponse.builder().bookings(new ArrayList<>()).totalResults(0).offset(0)
        .limit(searchBookingsRequest.getLimit()).hasMore(false).totalPages(0)
        .responseLimitExceeded(false).build();
  }

  private Optional<FindBookingResponse> findBookingByBasketAndOpera(
      FindBookingRequest findBookingRequest,
      BookingChannel bookingChannel) {
    var optionalBasket = basketOutPort.getBasketByReference(findBookingRequest.getResNo());

    if (optionalBasket.isEmpty()) {
      return Optional.empty();
    }

    BasketResponse basket = optionalBasket.get();
    if (basket.getItems().isEmpty()) {
      if (StatusEnum.COMPLETED.name().equals(basket.getStatus())) {
        log.info(
            "Empty basket having resNo = {} and status = COMPLETED found, which needs to be deleted.",
            findBookingRequest.getResNo());
        basketOutPort.deleteBasket(basket.getReference(), ManageBookingUtils.getLastModifyTimestamp(basket));
      }
      log.info("Could not find a valid basket resNo= {}.", findBookingRequest.getResNo());
      return Optional.empty();
    }

    if (FAILED.name().equals(basket.getStatus()) || OPEN.name()
        .equals(basket.getStatus())) {
      log.info("Basket with resNo= {} has status {}.", findBookingRequest.getResNo(),
          basket.getStatus());
      return Optional.empty();
    }

    var operaRes = hotelReservationOhipOutPort.getReservationsByIds(
        basket.getHotelId(),
        basket.getItems().stream().map(BasketItemResponse::getSourceId).toList(), false);

    boolean operaResStatus =
        operaRes.getReservationByIdList().stream()
            .filter(rsv -> rsv.getReservationStatus().equals(CANCELLED_STATUS))
            .toList().size() == operaRes.getReservationByIdList().size();

    if (operaResStatus && !basket.getStatus().equals(BasketStatusEnum.CANCELLED.name())) {
      basketOutPort.cancelBasket(basket.getReference(), false, false, null);
    }
    // get channel from basket if it is available
    var channel = Optional.ofNullable(optionalBasket.get().getChannel())
        .orElse(bookingChannel.getChannel());
    //TODO if channel is PI Booking Channel why filter for business booker ?
    if (channel.equals(PI_BOOKING_CHANNEL)) {
      var reservations = operaRes.getReservationByIdList()
          .stream()
          .filter(roomStay -> isNull(roomStay.getRoomStay().getSourceCode())
              || !BUSINESS_BOOKER.contains(roomStay.getRoomStay().getSourceCode()))
          .toList();
      if (reservations.isEmpty()) {
        return Optional.empty();
      }
    }

    var sourceCode = getSourceCode(operaRes);

    String idContextOpera = operaRes.getIdContext();

    var result = manageBookingLogic.getFindBookingResponse(findBookingRequest, bookingChannel, idContextOpera,
        sourceCode, basket);

    return getResponseForMatchesOperaResDetails(findBookingRequest, operaRes, basket, result,
        idContextOpera, sourceCode, bookingChannel);
  }

  private void getAndSaveCharges(String hotelId, Set<String> reservationsIds) {
    DepositFoliosResponse depositFolios = hotelReservationOhipOutPort
        .getGeneratedDepositFolios(hotelId, reservationsIds);
    basketOutPort.saveCharges(depositFolios);
  }

  private boolean shouldSaveChargesForExistingReservations(BasketResponse basket,
      ReservationByBasketRefResponse reservations) {

    if (!PAY_NOW.equals(basket.getPaymentOption())
        || reservations.getReservationByIdList().stream()
        .anyMatch(
            res -> BigDecimal.ZERO.compareTo(res.getRateInfo().getSummary().getGuestPay()) != 0)) {
      return false;
    }

    var hasCharges = reservations.getReservationByIdList().stream()
        .map(ReservationByIdResponse::getReservationId)
        .anyMatch(this::hasCharges);
    return !hasCharges;
  }

  private boolean hasCharges(String reservationId) {
    var charges = basketOutPort.getCharges(reservationId);
    return Objects.nonNull(charges) && CollectionUtils.isNotEmpty(charges.getDepositFolios());
  }


  /**
   * Creates a basket for a given external Opera (OTA or migrated booking).
   * Validates the OTA booking before creating the basket and throws the digital exception.
   *
   * @param findBookingRequest the findBooking request
   * @param bookingChannel     the booking channel
   * @param operaExternalRes   the external Opera booking
   * @return the FindBookingResponse with the created basket, or null if not created
   */
  private FindBookingResponse findExternalBookingInOpera(
      final FindBookingRequest findBookingRequest,
      final BookingChannel bookingChannel,
      final ReservationsDetailsEnhancedResponse operaExternalRes) {

    String idContext = null;
    String sourceCode = null;

    if (operaExternalRes != null && operaExternalRes.getReservations() != null
        && operaExternalRes.getReservations().getReservationInfo() != null
        && !operaExternalRes.getReservations().getReservationInfo().isEmpty()) {
      idContext = operaExternalRes.getReservations().getReservationInfo()
          .stream()
          .filter(resInfo -> nonNull(resInfo.getExternalReferences()))
          .map(extRefs -> extRefs.getExternalReferences())
          .flatMap(List::stream)
          .map(ref -> ref.getIdContext()).findFirst()
          .orElse(null);
    }

    var isOtaFlag = manageBookingLogic.isOtaFlowEnabled();

    boolean requestMatchesResDetails = ManageBookingUtils.requestMatchesResDetails(findBookingRequest, operaExternalRes,
        manageBookingLogic.shouldBypassMatchesOpera(bookingChannel));

    boolean isCreateBasketFlow = false;
    var isIdContext = false;

    if (EXT_REF_CONTENT_ID.equals(idContext)) {
      var isValidExternalBartRef =
          ManageBookingUtils.isValidOperaReservation(operaExternalRes,
              bookingChannel.getChannel(), findBookingRequest.getResNo());
      var isMigrated = isValidExternalBartRef
          && containsMigratedReservations(operaExternalRes, findBookingRequest.getResNo())
          && requestMatchesResDetails;
      if (isMigrated) {
        log.info(
            "resNo = {} is an BART Opera booking, proceeding with basket creation.",
            findBookingRequest.getResNo());
        isCreateBasketFlow = true;
      }
    } else if (isOtaBooking(bookingChannel, idContext, otaBookingProperties.getProvidersExcluded(),
        otaBookingProperties.getSubchannel(), isOtaFlag)) {
      if (requestMatchesResDetails) {
        log.info(
            "resNo = {} is an OTA booking, proceeding with basket creation.",
            findBookingRequest.getResNo());
        isCreateBasketFlow = true;
        isIdContext = true;
      } else {
        throw new GenericBadRequestException(ErrorCode.DIGITAL_NOT_MATCH_OTA_EXCEPTION,
            THIRD_PARTY_RESERVATION_COULD_NOT_MATCH);
      }
    } else {
      throw new GenericBadRequestException(ErrorCode.DIGITAL_INVALID_OTA_EXCEPTION,
          THIRD_PARTY_RESERVATION_IS_NOT_ALLOWED);
    }

    if (isCreateBasketFlow) {
      var basket = createBasketForMigratedReservations(operaExternalRes,
          findBookingRequest.getResNo(),
          bookingChannel.getChannel(), bookingChannel.getSubchannel(), isIdContext);
      var resultExt = manageBookingLogic.buildReservationFindBookingResponse(findBookingRequest, basket);
      if (resultExt.isPresent()) {
        return resultExt.get();
      }
    }
    return null;
  }

  private BasketDto createBasketForMigratedReservations(
      final ReservationsDetailsEnhancedResponse operaReservation,
      final String reservationNumber, final String channel, final String subChannel,
      final boolean isIdContext) {
    String hotelId = operaReservation.getReservations().getReservationInfo().get(0).getHotelId();
    PaymentOptionEnum paymentOption = ManageBookingUtils.getPaymentOption((operaReservation));
    String paymentId = null;

    if (paymentOption == PaymentOptionEnum.PAY_NOW) {
      Optional<UniqueIDType> optionalUniqueIDType = operaReservation.getReservations()
          .getReservationInfo()
          .stream()
          .map(ReservationInfo::getReservationIdList)
          .flatMap(List::stream)
          .filter(id -> id.getType().equals("Reservation"))
          .findAny();

      if (optionalUniqueIDType.isPresent()) {
        var depositsResponse = hotelReservationOhipOutPort.getDepositsForReservationId(hotelId,
            optionalUniqueIDType.get().getId());
        var depositOpt = Optional.ofNullable(depositsResponse.getDeposits()).stream()
            .flatMap(Collection::stream).findFirst();
        if (depositOpt.isPresent()) {
          paymentId = depositOpt.get().getPaymentReference();
        }
      }
    }

    var basketStatus = BasketStatusEnum.COMPLETED;
    if (operaReservation.getReservations().getReservationInfo().get(0)
        .getReservationStatus().equals(StatusEnum.CANCELLED.name())) {
      basketStatus = BasketStatusEnum.CANCELLED;
    }
    var createBasketRequestReservationDto = new CreateBasketRequestReservationDto();
    createBasketRequestReservationDto.setHotelId(hotelId);
    createBasketRequestReservationDto
        .setBasketStatus(CreateBasketRequestReservationDto.BasketStatusEnum.valueOf(basketStatus.name()));
    ReservationBasketInfoDto reservationInfo = new ReservationBasketInfoDto();
    reservationInfo.setOriginalBasketId(null);
    reservationInfo.setMigratedResNo(reservationNumber);
    createBasketRequestReservationDto.setReservationBasketInfoDto(reservationInfo);
    PaymentInfoDto paymentInfo = new PaymentInfoDto();
    paymentInfo.setPaymentOption(PaymentInfoDto.PaymentOptionEnum.valueOf(paymentOption.name()));
    paymentInfo.setPaymentId(paymentId);
    createBasketRequestReservationDto.setPaymentInfoDto(paymentInfo);
    ChannelInfoDto channelInfo = new ChannelInfoDto();
    channelInfo.setChannel(channel);
    channelInfo.setSubChannel(subChannel);
    createBasketRequestReservationDto.setChannelInfoDto(channelInfo);
    Pair<List<BasketItemDto>, List<AddBasketItemTypeDto>> basketItemsPair =
        basketOutPort.addReservationsBasketItemsAndTypes(operaReservation);
    BasketItemInfoDto basketItemInfoDto = new BasketItemInfoDto();
    basketItemInfoDto.setBasketItems(basketItemsPair.getFirst());
    basketItemInfoDto.setBasketItemTypes(basketItemsPair.getSecond());
    createBasketRequestReservationDto.setBasketItemInfoDto(basketItemInfoDto);

    if (isIdContext) {
      UserInfoDto userInfo = new UserInfoDto();
      userInfo.setIdContext(ID_CONTEXT);
      createBasketRequestReservationDto.setUserInfoDto(userInfo);
    }
    var basket = basketOutPort.createBasketReservation(createBasketRequestReservationDto);
    if (basket != null && basket.getHotelId() == null) {
      basket.setHotelId(createBasketRequestReservationDto.getHotelId());
    }

    if (shouldSaveChargesForMigratedReservations(basket)) {
      getAndSaveCharges(basket.getHotelId(),
          basket.getItems().stream().map(BasketItemDto::getSourceId)
              .collect(Collectors.toSet()));
    }

    return basket;
  }

  private boolean shouldSaveChargesForMigratedReservations(BasketDto basket) {
    return PaymentOption.PAY_NOW.name().equals(basket.getPaymentOption()) && basket.getItems().stream()
        .noneMatch(item -> hasCharges(item.getSourceId()))
        &&
        BigDecimal.ZERO.compareTo(getTotal(amendLogic.getAmountFromReservationRateInfo(basket).getGuestPay()))
            == 0;
  }

  private Optional<FindBookingResponse> findBookingByOperaConfirmationId(FindBookingRequest findBookingRequest,
                                                                         BookingChannel bookingChannel) {
    final var cdhSearchBookingsResponse = cdhSearchBookingOutPort.searchBookingsFromCdh(
        CdhSearchBookingsRequest.builder().bookingReference(findBookingRequest.getResNo()).bookingsDatabaseSearch(true)
            .pageNumber(1).pageSize(10).build());

    if (cdhSearchBookingsResponse == null
        || cdhSearchBookingsResponse.getResults().stream().filter(Objects::nonNull).findFirst().isEmpty()) {
      return Optional.empty();
    }
    var firstCdhResult = cdhSearchBookingsResponse.getResults().stream()
        .filter(Objects::nonNull)
        .findFirst()
        .orElse(null);
    var hotelId = firstCdhResult != null ? firstCdhResult.getHotelId() : null;
    List<Rooms> rooms =
        cdhSearchBookingsResponse.getResults().stream().map(CdhResults::getRooms).filter(Objects::nonNull).toList()
            .get(0);
    var reservationId = rooms.stream().map(Rooms::getReservationId).toList().get(0);
    var rsvDetails = hotelReservationOhipOutPort.getReservationsByIds(hotelId,
            List.of(reservationId), false, true);
    var idContext = rsvDetails.getIdContext();
    var sourceCode = getSourceCode(rsvDetails);
    var byPass = manageBookingLogic.shouldBypassMatchesOpera(bookingChannel);
    if (!ManageBookingUtils.requestMatchesOperaResDetails(findBookingRequest, rsvDetails, byPass)) {
      return Optional.empty();
    }

    var isOta = manageBookingLogic.is3rdPartyBookingFlow(idContext, sourceCode, bookingChannel);

    var basket = manageBookingLogic.createBasketForOperaUiCreatedReservations(hotelId,
            findBookingRequest.getResNo(), rsvDetails, isOta);

    hotelReservationOhipOutPort.updateReservationExternalReference(hotelId, List.of(reservationId),
        basket.getBookingReference());

    return isOta
        ? manageBookingLogic.buildFindBookingResponse(findBookingRequest, basket, ID_CONTEXT) :
        manageBookingLogic.buildFindBookingResponse(findBookingRequest, basket);
  }

  private void setCiolAndCoolFlags(ManageBookingResponse manageBookingResponse,
      Pair<Boolean, String> ciolResult, boolean isCoolAvailable) {
    manageBookingResponse.setCheckInOnlineAvailable(ciolResult.getFirst());
    manageBookingResponse.setCheckOutOnlineAvailable(isCoolAvailable);
    manageBookingResponse.setCiolErrorLabelKey(ciolResult.getFirst() ? null : ciolResult.getSecond());
  }

  private void setDigitalKeyFlag(ManageBookingResponse manageBookingResponse,
                                   boolean isDigitalKey) {
    manageBookingResponse.setDigitalKey(isDigitalKey);
  }


  /**
   * Finds the external booking or the eligible confirmation number in Opera.
   *
   * @param findBookingRequest    the findBooking request
   * @param bookingChannel        the booking channel
   * @param isOperaConfirmAllowed the status of the 'release_pi_search_opera_conf_number' feature
   *                              flag
   * @return the FindBookingResponse with the created basket, or null if not created
   */
  private FindBookingResponse findBookingInOpera(final FindBookingRequest findBookingRequest,
      final BookingChannel bookingChannel, final boolean isOperaConfirmAllowed) {
    ReservationsDetailsEnhancedResponse operaExternalRef = null;
    try {
      operaExternalRef = hotelReservationOhipOutPort.getReservationsByExternalId(
          findBookingRequest.getResNo());
    } catch (HotelReservationOhipException exception) {
      log.debug(String.format("Opera is unreachable for resNo=%s", findBookingRequest.getResNo()));
      throw exception;
    } catch (HotelReservationNotFoundException exception) {
      ExceptionLogger.log(log, exception);
    } catch (RuntimeException exception) {
      ExceptionLogger.log(log, exception, "Failed to find the booking by reference id.");
    }
    return operaExternalRef == null
        ? getFindBookingByOperaConfirmationId(findBookingRequest, isOperaConfirmAllowed,
        bookingChannel) :
        findExternalBookingInOpera(findBookingRequest, bookingChannel, operaExternalRef);
  }


  /**
   * Searching in the basket is allowed only for digital bookings. When the
   * 'release_pi_search_opera_conf_number' feature flag is enabled, searching in the basket is
   * also allowed using the external booking reference or confirmation number.
   *
   * @param findBookingRequest    the findBooking request
   * @param isOperaConfirmAllowed the feature flag status
   * @return true if searching in the basket is allowed
   */
  private boolean isEligibleForBasketSearch(final FindBookingRequest findBookingRequest,
      final boolean isOperaConfirmAllowed) {
    return manageBookingLogic.isDigitalReference(findBookingRequest.getResNo()) || isOperaConfirmAllowed;
  }


  /**
   * Searching by confirmation number is allowed when the 'release_pi_search_opera_conf_number' feature flag
   * is enabled and the confirmation number is obtained from the findBooking request.
   *
   * @param findBookingRequest    the findBooking request
   * @param isOperaConfirmAllowed the feature flag status
   * @return true if searching by confirmation number is allowed
   */
  private boolean isEligibleForConfirmationSearch(FindBookingRequest findBookingRequest,
      boolean isOperaConfirmAllowed) {
    return isOperaConfirmAllowed
        && (manageBookingLogic.isOperaConfirmationNumber(findBookingRequest.getResNo())
        || Boolean.TRUE.equals(findBookingRequest.getIsOldBooking()));
  }

  @Nullable
  private static String getSourceCode(ReservationByBasketRefResponse operaRes) {
    String sourceCode = null;
    if (operaRes.getReservationByIdList().get(0) != null
        && operaRes.getReservationByIdList().get(0).getRoomStay() != null) {
      sourceCode = operaRes.getReservationByIdList().get(0).getRoomStay().getSourceCode();
    }
    return sourceCode;
  }

  /**
   * Checks if the details in the findBooking request match those in the Opera reservation details.
   * In case of a match, it returns the result. Otherwise, for third-party bookings (except for
   * distribution bookings), if third-party import is not allowed, it throws a digital exception.
   *
   * @param findBookingRequest request
   * @param operaRes           the opera reservation details
   * @param basket             basket
   * @param result             the default result
   * @param idContextOpera     idContext from opera
   * @param sourceCode         the sourcecode
   * @return the default result if the details match
   */
  private Optional<FindBookingResponse> getResponseForMatchesOperaResDetails(
      FindBookingRequest findBookingRequest, ReservationByBasketRefResponse operaRes,
      BasketResponse basket, Optional<FindBookingResponse> result, String idContextOpera,
      String sourceCode, BookingChannel bookingChannel) {
    var shouldBypassMatchesOpera = manageBookingLogic.shouldBypassMatchesOpera(bookingChannel);
    if (ManageBookingUtils.requestMatchesOperaResDetails(findBookingRequest, operaRes,
        shouldBypassMatchesOpera)) {
      if (shouldSaveChargesForExistingReservations(basket, operaRes)) {
        getAndSaveCharges(basket.getHotelId(),
            basket.getItems().stream().map(BasketItemResponse::getSourceId)
                .collect(Collectors.toSet()));
      }
      return result;
    } else {
      var exception = new GenericBadRequestException(ErrorCode.DIGITAL_RESERVATION_BASKET_EXCEPTION,
          "Basket could not match reservation with what it found.");
      if (isOtaBooking(bookingChannel, idContextOpera,
          otaBookingProperties.getProvidersExcluded(), otaBookingProperties.getSubchannel(),
          manageBookingLogic.isOtaFlowEnabled()) && manageBookingLogic.is3rdPartyBooking(
          idContextOpera, sourceCode)) {
        exception = new GenericBadRequestException(ErrorCode.DIGITAL_NOT_MATCH_OTA_EXCEPTION,
            THIRD_PARTY_RESERVATION_COULD_NOT_MATCH);
      }
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private FindBookingResponse getFindBookingByOperaConfirmationId(
      FindBookingRequest findBookingRequest, boolean isOperaConfirmAllowed,
      BookingChannel bookingChannel) {
    var isConfirmationFlow = isEligibleForConfirmationSearch(findBookingRequest,
        isOperaConfirmAllowed);
    if (isConfirmationFlow) {
      return findBookingByOperaConfirmationId(findBookingRequest, bookingChannel).orElse(null);
    }
    return null;
  }

  @Override
  public void updateUdfc20(UpdateReservationUdfsRequest updateReservationUdfsRequest) {
    hotelReservationOhipOutPort.updateUdfc20(updateReservationUdfsRequest);
  }

}
