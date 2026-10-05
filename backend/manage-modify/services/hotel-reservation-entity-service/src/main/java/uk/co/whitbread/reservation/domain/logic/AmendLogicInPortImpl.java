package uk.co.whitbread.reservation.domain.logic;

import static org.apache.commons.lang3.compare.ComparableUtils.is;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.A2C_GUARANTEE_OPERA_CODE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.CASH_PAYMENT_METHOD_OPERA_CODE;
import static uk.co.whitbread.reservation.domain.constants.HotelReservationConstants.DIRECT_SETTLEMENT_PAYMENT_METHOD_OPERA_CODE;
import static uk.co.whitbread.reservation.domain.model.amend.in.PaymentOption.PAY_NOW;
import static uk.co.whitbread.reservation.domain.model.in.BookingChannel.CCUI_BOOKING_CHANNEL;
import static uk.co.whitbread.reservation.domain.model.payment.out.PaymentType.RESERVE_WITHOUT_CARD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.ErrorCode;
import uk.co.whitbread.reservation.domain.constants.HotelReservationConstants;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.domain.logic.utils.TokenUtils;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendConfirmationPricesRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendOnHoldInterval;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendPaymentPageRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendConfirmationPricesResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendPaymentPageResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.ConfirmAmendLogicResponse;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlag;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryAmountRequest;
import uk.co.whitbread.reservation.domain.model.in.AmendSummaryRequest;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationRequest;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.AcceptedCreditCard;
import uk.co.whitbread.reservation.domain.model.index.header.data.out.HotelPaymentInformation;
import uk.co.whitbread.reservation.domain.model.out.AccountCompanyItems;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryAmountResponse;
import uk.co.whitbread.reservation.domain.model.out.AmendSummaryDetails;
import uk.co.whitbread.reservation.domain.model.out.BasketItemResponse;
import uk.co.whitbread.reservation.domain.model.out.BasketResponse;
import uk.co.whitbread.reservation.domain.model.out.BillingResponse;
import uk.co.whitbread.reservation.domain.model.out.BookingAllowancesResponse;
import uk.co.whitbread.reservation.domain.model.out.CcuiExtraItems;
import uk.co.whitbread.reservation.domain.model.out.HotelInfoResponse;
import uk.co.whitbread.reservation.domain.model.out.NavigationOptions;
import uk.co.whitbread.reservation.domain.model.out.PackagesSelection;
import uk.co.whitbread.reservation.domain.model.out.PaymentCardDetails;
import uk.co.whitbread.reservation.domain.model.out.PaymentOptions;
import uk.co.whitbread.reservation.domain.model.out.ReservationBookerAddress;
import uk.co.whitbread.reservation.domain.model.out.ReservationByBasketRefResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationByIdResponse;
import uk.co.whitbread.reservation.domain.model.out.ReservationCompany;
import uk.co.whitbread.reservation.domain.model.out.ReservationPaymentCardType;
import uk.co.whitbread.reservation.domain.model.out.RoomStayByIdResponse;
import uk.co.whitbread.reservation.domain.model.payment.in.Address;
import uk.co.whitbread.reservation.domain.model.payment.in.Amount;
import uk.co.whitbread.reservation.domain.model.payment.in.Billing;
import uk.co.whitbread.reservation.domain.model.payment.in.Booking;
import uk.co.whitbread.reservation.domain.model.payment.in.BusinessSite;
import uk.co.whitbread.reservation.domain.model.payment.in.Payment;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentRequest;
import uk.co.whitbread.reservation.domain.model.payment.in.PaymentsConfirmation;
import uk.co.whitbread.reservation.domain.model.payment.in.RoomType;
import uk.co.whitbread.reservation.domain.model.payment.out.A2cDetails;
import uk.co.whitbread.reservation.domain.model.payment.out.Allowances;
import uk.co.whitbread.reservation.domain.model.payment.out.AvailablePaymentType;
import uk.co.whitbread.reservation.domain.model.payment.out.BillingAddress;
import uk.co.whitbread.reservation.domain.model.payment.out.CardHolderName;
import uk.co.whitbread.reservation.domain.model.payment.out.CardPresent;
import uk.co.whitbread.reservation.domain.model.payment.out.CompanyRef;
import uk.co.whitbread.reservation.domain.model.payment.out.Eckoh;
import uk.co.whitbread.reservation.domain.model.payment.out.EmailPreference;
import uk.co.whitbread.reservation.domain.model.payment.out.InitiatePaymentResponse;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentOption;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentStatus;
import uk.co.whitbread.reservation.domain.model.payment.out.PaymentType;
import uk.co.whitbread.reservation.domain.model.payment.out.PreAuthCharges;
import uk.co.whitbread.reservation.domain.model.payment.out.PurchaseOrder;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.reservation.domain.ports.secondary.HotelReservationOhipOutPort;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RequiredArgsConstructor
public class AmendLogicInPortImpl implements AmendLogicInPort {

  public static final String CARD = "CARD";
  public static final String ECOMM = "ECOMM";
  public static final String AMEND = "AMEND";
  public static final String HOTEL = "HOTEL";
  public static final String DEFAULT_LANGUAGE_EN = "en";
  public static final String LANGUAGE_DE = "de";
  public static final String COUNTRY_DE = "de";
  public static final String DEFAULT_PAYMENY_STATUS = "SUCCESSFUL";
  private static final int BASKET_NOT_FOUND_ERROR_CODE = 707;
  private final BasketOutPort basketOutPort;
  private final HotelReservationOhipOutPort reservationOhipOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final ContentOutPort contentOutPort;
  private final ReservationCleanup reservationCleanUp;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  public static final String CITYTAX = "CITYTAX";


  @Override
  public boolean checkCreateOnHoldReservation(String arrivalDate, String departureDate,
      String newArrivalDate,
      String newDepartureDate) {

    var arrivalDateLd = getLocalDateFromString(arrivalDate);
    var departureDateLd = getLocalDateFromString(departureDate);
    var newArrivalDateLd = getLocalDateFromString(newArrivalDate);
    var newDepartureDateLd = getLocalDateFromString(newDepartureDate);

    int arrivalReqArrival = newArrivalDateLd.compareTo(arrivalDateLd);
    int departureReqDeparture = newDepartureDateLd.compareTo(departureDateLd);

    // Case 3 : the only situation where no onHoldReservation is needed
    return arrivalReqArrival >= 0 && departureReqDeparture <= 0 ? false : true;
  }

  @Override
  public List<AmendOnHoldInterval> getAmendOnHoldReservationInterval(String arrivalDate,
      String departureDate,
      String newArrivalDate,
      String newDepartureDate) {

    var arrivalDateLd = getLocalDateFromString(arrivalDate);
    var departureDateLd = getLocalDateFromString(departureDate);
    var newArrivalDateLd = getLocalDateFromString(newArrivalDate);
    var newDepartureDateLd = getLocalDateFromString(newDepartureDate);

    int arrival = newArrivalDateLd.compareTo(arrivalDateLd);
    int departure = newDepartureDateLd.compareTo(departureDateLd);

    if (arrival >= 0 && departure <= 0) {
      // within
      return List.of();
    } else {
      // right side situations
      if (arrival >= 0 && departure > 0 && newArrivalDateLd.compareTo(departureDateLd) <= 0) {
        // Case 1,8
        return List.of(createAmendOnHoldInterval(departureDate, newDepartureDate));
      } else if (newArrivalDateLd.compareTo(departureDateLd) > 0 && departure > 0) {
        // Case 5
        return List.of(createAmendOnHoldInterval(newArrivalDate, newDepartureDate));

        // left situations
      } else if (departure <= 0 && arrival < 0
          && newDepartureDateLd.compareTo(arrivalDateLd) >= 0) {
        // Case 2,7
        return List.of(createAmendOnHoldInterval(newArrivalDate, arrivalDate));

      } else if (newDepartureDateLd.compareTo(arrivalDateLd) < 0 && arrival < 0) {
        // Case 6
        return List.of(createAmendOnHoldInterval(newArrivalDate, newDepartureDate));

        // Case 4
      } else if (arrival < 0 && departure > 0) {
        return List.of(createAmendOnHoldInterval(newArrivalDate, arrivalDate),
            createAmendOnHoldInterval(departureDate, newDepartureDate));
      }
    }

    return List.of();
  }

  @Override
  public LocalDate getLocalDateFromString(String stringDate) {
    return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
  }

  @Override
  public AmendOnHoldInterval createAmendOnHoldInterval(String arrivalDate,
      String departureDate) {
    return AmendOnHoldInterval.builder()
        .amendArrivalDate(arrivalDate)
        .amendDepartureDate(departureDate)
        .build();
  }

  @Override
  public AmendSummaryDetails getAmendSummaryDetails(AmendSummaryRequest amendSummaryRequest) {
    var originalBasket = basketOutPort.getBasketById(amendSummaryRequest.getOriginalBasketRef());

    if (!authenticatedUserService.isUserAuthenticated()) {
      validateToken(amendSummaryRequest.getToken(), amendSummaryRequest.getOriginalBasketRef());
    }

    var tempBasket = basketOutPort.getBasketById(amendSummaryRequest.getCopyBasketRef());

    AmendSummaryAmountResponse originalAmendSummary = getAmountFromRateInfo(originalBasket);
    AmendSummaryAmountResponse tempAmendSummary = getAmountFromRateInfo(tempBasket);

    List<String> originalResIds = originalBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId)
        .toList();

    Set<String> tempResIds = tempBasket.getItems().stream()
        .map(BasketItemResponse::getSourceId)
        .collect(Collectors.toSet());

    BigDecimal charitable = calculateCharitable(originalBasket.getHotelId(), originalResIds,
        originalBasket.getItems().get(0).getSourceId(), tempResIds,
        tempBasket.getLinkAmendReservations());

    var firstReservation = reservationOhipOutPort
        .getReservationsByIds(originalBasket.getHotelId(), originalResIds,
            false, false, false)
        .getReservationByIdList()
        .get(0);

    PaymentOptions paymentOptions = null;
    BigDecimal payOnArrival = calculatePayOnArrival(originalAmendSummary, tempAmendSummary,
        tempBasket.getLinkAmendReservations());

    var bookingChannel = Optional.of(amendSummaryRequest)
        .map(AmendSummaryRequest::getBookingChannel)
        .map(BookingChannel::getChannel)
        .orElse(StringUtils.EMPTY);

    if (payOnArrival.compareTo(BigDecimal.ZERO) > 0) {
      var tempReservation = reservationOhipOutPort
          .getReservationsByIds(tempBasket.getHotelId(), List.copyOf(tempResIds),
              false, false, false)
          .getReservationByIdList()
          .get(0);
      paymentOptions = createPaymentOptions(originalAmendSummary, bookingChannel, tempReservation);
    }

    HotelPaymentInformation hotelPaymentInformation = contentOutPort.getHotelPaymentInformation(
        originalBasket.getHotelId(),
        amendSummaryRequest.getBookingChannel().getLanguage().toLowerCase(),
        amendSummaryRequest.getCountry().toLowerCase());

    var pibaCardTypeCode = hotelPaymentInformation
        .getAcceptedCreditCards().stream()
        .filter(c -> isPiba(c.getCodeOpera())).findFirst();

    if (pibaCardTypeCode.isPresent() && firstReservation.getPaymentCard() != null
        && pibaCardTypeCode.get().getCodeOperaCardType() != null
        && pibaCardTypeCode.get().getCodeOperaCardType()
        .equalsIgnoreCase(firstReservation.getPaymentCard().getCardType())) {
      return AmendSummaryDetails.builder()
          .charitable(charitable)
          .previousTotal(BigDecimal.ZERO)
          .balancePaid(BigDecimal.ZERO)
          .payOnArrival(BigDecimal.ZERO)
          .refund(BigDecimal.ZERO)
          .nonRefundable(BigDecimal.ZERO)
          .totalCost(BigDecimal.ZERO)
          .balanceAuthorised(tempAmendSummary.getTotalCostOfStay())
          .paymentOptions(null)
          .paymentCardDetails(getPaymentCardDetails(firstReservation, hotelPaymentInformation))
          .build();
    }

    return AmendSummaryDetails.builder()
        .charitable(charitable)
        .previousTotal(originalAmendSummary.getNet())
        .balancePaid(getTotal(originalAmendSummary.getDeposit()).abs())
        .payOnArrival(calculatePayOnArrival(originalAmendSummary, tempAmendSummary,
            tempBasket.getLinkAmendReservations()))
        .refund(calculateRefund(originalAmendSummary, tempAmendSummary,
            tempBasket.getLinkAmendReservations()))
        .nonRefundable(BigDecimal.ZERO)
        .totalCost(tempAmendSummary.getTotalCostOfStay())
        .balanceAuthorised(BigDecimal.ZERO)
        .paymentOptions(paymentOptions)
        .navigationOptions(CCUI_BOOKING_CHANNEL.equalsIgnoreCase(bookingChannel)
            ? NavigationOptions.builder()
                .amendPaymentPage(isAmendPaymentPageVisible(firstReservation))
                .build() : null)
        .paymentCardDetails(getPaymentCardDetails(firstReservation, hotelPaymentInformation))
        .build();
  }

  private boolean isAmendPaymentPageVisible(ReservationByIdResponse firstReservation) {
    var reservationPaymentMethod = Optional.ofNullable(firstReservation)
        .map(ReservationByIdResponse::getPaymentCard)
        .map(ReservationPaymentCardType::getPaymentMethod)
        .orElse(StringUtils.EMPTY);
    var reservationGuaranteeCode = Optional.ofNullable(firstReservation)
        .map(ReservationByIdResponse::getGuaranteeCode)
        .orElse(StringUtils.EMPTY);

    var isPaymentMethodPiba = isPiba(reservationPaymentMethod);
    var isPaymentMethodA2c = CASH_PAYMENT_METHOD_OPERA_CODE.equalsIgnoreCase(reservationPaymentMethod)
        && A2C_GUARANTEE_OPERA_CODE.equalsIgnoreCase(reservationGuaranteeCode);

    return (isPaymentMethodPiba && unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getCcuiAmendPiba()))
        || isPaymentMethodA2c;
  }

  protected static BigDecimal getTotal(Map<String, BigDecimal> amount) {
    BigDecimal total = new BigDecimal(0);
    for (BigDecimal value : amount.values()) {
      total = total.add(value);
    }
    return total;
  }

  private PaymentCardDetails getPaymentCardDetails(ReservationByIdResponse firstReservation,
      HotelPaymentInformation hotelPaymentInformation) {
    if (Objects.nonNull(firstReservation.getPaymentCard().getExpirationDate())
        && firstReservation.getPaymentCard().getExpirationDate().isAfter(LocalDate.now())) {
      return PaymentCardDetails.builder()
          .cardNumberMasked(
              getPaymentCardDetailsValue(firstReservation.getPaymentCard().getCardNumberMasked()))
          .cardHolderName(
              getPaymentCardDetailsValue(firstReservation.getPaymentCard().getCardHolderName()))
          .expirationDate(firstReservation.getPaymentCard().getExpirationDate())
          .cardType(getPaymentCardDetailsValue(firstReservation.getPaymentCard().getCardType()))
          .token(getPaymentCardDetailsValue(firstReservation.getPaymentCard().getToken()))
          .cardNumberLast4Digits(
              getPaymentCardDetailsValue(firstReservation.getPaymentCard().getCardNumberMasked()
                  .substring(firstReservation.getPaymentCard().getCardNumberMasked().length() - 4)))
          .cardName(getCardNameValue(firstReservation.getPaymentCard().getCardType(),
              hotelPaymentInformation))
          .cardLogoSrc(getCardSrcLogo(firstReservation.getPaymentCard().getCardType(),
              hotelPaymentInformation))
          .build();
    } else {
      return PaymentCardDetails.builder().build();
    }
  }

  private String getCardSrcLogo(String cardType, HotelPaymentInformation hotelPaymentInformation) {
    if (Objects.nonNull(cardType)) {
      Optional<String> cardSrcLogoValue = getAcceptedCreditCard(cardType, hotelPaymentInformation)
          .map(AcceptedCreditCard::getSchemeLogo);
      if (cardSrcLogoValue.isPresent()) {
        return cardSrcLogoValue.get();
      }
    }
    return Strings.EMPTY;
  }

  private String getCardNameValue(String cardType,
      HotelPaymentInformation hotelPaymentInformation) {
    if (Objects.nonNull(cardType)) {
      Optional<String> cardNameValue = getAcceptedCreditCard(cardType, hotelPaymentInformation)
          .map(AcceptedCreditCard::getName);
      if (cardNameValue.isPresent()) {
        return cardNameValue.get();
      }
    }
    return Strings.EMPTY;
  }

  private static Optional<AcceptedCreditCard> getAcceptedCreditCard(String cardType,
      HotelPaymentInformation hotelPaymentInformation) {
    return hotelPaymentInformation.getAcceptedCreditCards()
        .stream()
        .filter(acceptedCreditCard -> acceptedCreditCard.getCodeOperaCardType()
            .equals(cardType.toUpperCase()))
        .findFirst();
  }

  private static String getPaymentCardDetailsValue(String value) {
    if (Objects.nonNull(value)) {
      return value;
    }
    return Strings.EMPTY;
  }

  private PaymentOptions createPaymentOptions(AmendSummaryAmountResponse originalAmendSummary,
      String bookingChannel, ReservationByIdResponse temporaryReservation) {

    var payNow = false;
    var payOnArrival = false;

    //reservation has a deposit folio
    var hasDF = getTotal(originalAmendSummary.getDeposit()).compareTo(BigDecimal.ZERO) < 0;
    //reservation's pay on arrival amount
    var guestPay = originalAmendSummary.getGuestPay();

    if (hasDF) {
      //  only DF amount (when we have only deposit, not a pay on arrival amount already on the reservation)
      //  --> the user can choose between pay now and pay on arrival
      //  (originalAmendSummary.deposit < 0 && originalAmendSummary.guestPay = 0)
      if (getTotal(guestPay).compareTo(BigDecimal.ZERO) == 0) {
        payNow = true;
      }
      // DF & pay on arrival amount on the original reservation (rateInfo endpoint from Opera)
      // --> the user can only choose pay on arrival
      // (originalAmendSummary.deposit < 0 && originalAmendSummary.guestPay > 0)
      payOnArrival = true;
    }

    var paymentCard = temporaryReservation.getPaymentCard();
    var tempRoomStay = temporaryReservation.getRoomStay();

    //for CCUI amend should be allowed only pay on arrival
    //for a payment card that has expiration date before the tempRes arrivalDate
    // only pay on arrival should be allowed
    boolean isBookingReservedWithoutCreditCard = Optional.ofNullable(paymentCard)
        .map(ReservationPaymentCardType::getPaymentMethod)
        .map("CA"::equalsIgnoreCase)
        .orElse(false);
    if (CCUI_BOOKING_CHANNEL.equals(bookingChannel) || isBookingReservedWithoutCreditCard || paymentCard == null
        || paymentCard.getExpirationDate().isBefore(getLocalDateFromString(tempRoomStay.getArrivalDate()))
        || !unleashWrapper.isEnabled(unleashWrapper.featureFlag().getEnableAmendPayNow())) {
      payNow = false;
    }

    return PaymentOptions.builder()
        .payNow(payNow)
        .payOnArrival(payOnArrival)
        .build();
  }

  @Override
  public boolean isLeadGuestUpdated(
      ReservationByIdResponse original,
      UpdateReservationRequest temp) {
    final var originalLeadGuest = original.getReservationGuestList().get(0);
    final var tempLeadGuest =
        temp.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer()
            .getPersonName().get(0);

    String emailAddress = null;
    if (temp.getReservationGuests().get(0).getProfileInfo().getProfile().getEmails() != null) {
      emailAddress =
          temp.getReservationGuests().get(0).getProfileInfo().getProfile().getEmails()
              .getEmailInfo().get(0).getEmail()
              .getEmailAddress();
    }

    String originalEmailAddress = null;
    if (originalLeadGuest.getEmail() != null) {
      originalEmailAddress = originalLeadGuest.getEmail();
    }

    String updatedNameTitle = null;
    String nameTitle = temp.getReservationGuests().get(0).getProfileInfo().getProfile()
        .getCustomer().getPersonName().get(0).getNameTitle();
    if (Objects.nonNull(nameTitle)) {
      updatedNameTitle =
          temp.getReservationGuests().get(0).getProfileInfo().getProfile().getCustomer()
              .getPersonName().get(0).getNameTitle();
    }

    String originalNameTitle = null;
    if (Objects.nonNull(originalLeadGuest.getNameTitle())) {
      originalNameTitle = originalLeadGuest.getNameTitle();
    }

    return !originalLeadGuest.getSurName().equals(tempLeadGuest.getSurname())
        || !originalLeadGuest.getGivenName().equals(tempLeadGuest.getGivenName())
        || !Objects.equals(originalNameTitle, updatedNameTitle)
        || !Objects.equals(originalEmailAddress, emailAddress);
  }

  private BigDecimal calculateCharitable(
      String hotelId, List<String> originalResIds,
      String firstRoomResId, Set<String> tempReservations,
      Map<String, String> linkAmendReservations) {
    var reservationsPackagesResponse = reservationOhipOutPort
        .getReservationsPackagesByIds(hotelId, originalResIds);

    var firstRoom = reservationsPackagesResponse.getRoomsSelections().get(0);

    if (isFirstRoomDeleted(firstRoomResId, tempReservations, linkAmendReservations)) {
      return BigDecimal.ZERO;
    }

    List<String> packageCodes = null;
    if (firstRoom != null && firstRoom.getPackagesSelection() != null) {
      packageCodes = firstRoom.getPackagesSelection().stream()
          .map(PackagesSelection::getId)
          .filter(id -> id.contains("CHRY"))
          .toList();
    }

    var donationDetails = Optional
        .ofNullable(reservationOhipOutPort.getCharityPackagesDetails(hotelId, packageCodes));

    if (donationDetails.isPresent()) {
      return donationDetails.get().getDonationPackages().get(0).getUnitPrice();
    }
    return BigDecimal.ZERO;
  }

  private BigDecimal calculatePayOnArrival(AmendSummaryAmountResponse original,
      AmendSummaryAmountResponse temp, Map<String, String> linkAmendReservations) {
    BigDecimal payOnArrival = new BigDecimal(0);
    for (String value : temp.getGuestPay().keySet()) {
      Optional<Map.Entry<String, String>> optOriginalReservationId = linkAmendReservations.entrySet()
          .stream()
          .filter(entry -> value.equals(entry.getValue())).findFirst();
      if (optOriginalReservationId.isPresent()) {
        BigDecimal payOnArrivalByReservationId = original.getDeposit()
            .get(optOriginalReservationId.get().getKey())
            .add(temp.getGuestPay().get(value));
        if (is(payOnArrivalByReservationId).greaterThan(BigDecimal.ZERO)) {
          payOnArrival = payOnArrival.add(payOnArrivalByReservationId);
        }
      } else {
        payOnArrival = payOnArrival.add(temp.getGuestPay().get(value));
      }
    }
    return payOnArrival;
  }

  private BigDecimal calculateRefund(AmendSummaryAmountResponse original,
      AmendSummaryAmountResponse temp, Map<String, String> linkAmendReservations) {
    BigDecimal refund = new BigDecimal(0);
    for (String value : linkAmendReservations.keySet()) {
      BigDecimal tempReservationGuestPay = temp.getGuestPay().get(linkAmendReservations.get(value));
      if (Objects.nonNull(tempReservationGuestPay)) {
        BigDecimal payOnArrivalByReservationId = original.getDeposit().get(value)
            .add(tempReservationGuestPay);
        if (is(payOnArrivalByReservationId).lessThan(BigDecimal.ZERO)) {
          refund = refund.add(payOnArrivalByReservationId);
        }
      } else {
        refund = refund.add(original.getDeposit().get(value));
      }
    }
    return refund;
  }

  public AmendSummaryAmountResponse getAmountFromRateInfo(BasketResponse basket) {
    var reservationsIds = getReservationIds(basket);
    return reservationOhipOutPort.getAmendSummaryDetails(AmendSummaryAmountRequest
        .builder()
        .hotelId(basket.getHotelId())
        .reservationIds(reservationsIds)
        .build());
  }

  @Override
  public ConfirmAmendLogicResponse confirmAmendLogic(
      ConfirmAmendLogicRequest request) {

    String ccAgentId = null;
    if (!authenticatedUserService.isUserAuthenticated()) {
      validateToken(request.getToken(), request.getOriginalBookingRef());
    } else if (request.getBookingChannel().isCcui() && unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getCcuiAgentIdLog())) {
      ccAgentId = authenticatedUserService.getAuthenticatedUser().getAccount().getEmail();
    }

    if (CollectionUtils.isNotEmpty(request.getPreCheckIn())) {
      deleteRegCardPdfAndPreCheckInStatus(request.getTempBookingRef(), request.getPreCheckIn());
    }

    var originalBasket = basketOutPort.getBasketById(request.getOriginalBookingRef());
    if (PAY_NOW.equals(request.getPaymentOptionSelected())) {
      var tempBasket = basketOutPort.getBasketById(request.getTempBookingRef());
      var tempReservations = reservationOhipOutPort.getReservationsByIds(tempBasket.getHotelId(),
          getReservationIds(tempBasket), false, false,
          false);
      var paymentRequest = buildPaymentRequest(request, originalBasket, tempBasket,
          tempReservations);
      var initiatePaymentResponse = basketOutPort.initiatePayment(
          request.getTempBookingRef(), paymentRequest);

      return ConfirmAmendLogicResponse
          .builder()
          .payment(initiatePaymentResponse)
          .build();
    } else {
      //if the payment option selected is PAY_ON_ARRIVAL, the channel is CCUI and the payment option is
      //PIBA|ACCOUNT_COMPANY|RESERVE_WITHOUT_CARD
      //then call basket’s endpoint POST /v1/baskets/ccui/{basket-reference}/pay

      if (isCcuiPaymentProcess(request)) {
        basketOutPort.initiateCcuiPaymentProcess(request, originalBasket.getHotelId());
        return ConfirmAmendLogicResponse.builder()
            .payment(InitiatePaymentResponse.builder().status(PaymentStatus.NOT_REQUIRED).build())
            .build();
      }

      basketOutPort.processAmend(request.getTempBookingRef(),
          buildPaymentsConfirmation(request, originalBasket), ccAgentId);
    }

    return ConfirmAmendLogicResponse.builder()
        .payment(InitiatePaymentResponse.builder()
            .status(PaymentStatus.NOT_REQUIRED)
            .build())
        .build();
  }

  private void deleteRegCardPdfAndPreCheckInStatus(String bookingRef,
      List<String> preCheckedInReservationIds) {
    Optional.ofNullable(basketOutPort.getBasketById(bookingRef))
        .ifPresent(basket -> preCheckedInReservationIds.forEach(reservationId -> {
          reservationOhipOutPort.deleteRegCardAttachment(basket.getHotelId(), reservationId);
          reservationOhipOutPort.deleteReservationPreCheckIn(basket.getHotelId(), reservationId);
        }));
  }

  @Override
  public AmendConfirmationPricesResponse getAmendConfirmationPrices(
      AmendConfirmationPricesRequest amendConfirmationPricesRequest) {

    if (!authenticatedUserService.isUserAuthenticated()) {
      validateToken(amendConfirmationPricesRequest.getToken(),
          amendConfirmationPricesRequest.getOriginalBookingRef());
    }

    BasketResponse temporaryBasket;
    BasketResponse originalBasket;

    try {
      temporaryBasket = basketOutPort.getBasketById(
          amendConfirmationPricesRequest.getTempBookingRef());

    } catch (BasketNotFoundException exception) {
      if (BASKET_NOT_FOUND_ERROR_CODE == exception.getErrorCode()) {
        log.info(String.format("Temporary basket reference %s has expired or is empty.",
            amendConfirmationPricesRequest.getTempBookingRef()));
      } else {
        ExceptionLogger.log(log, exception);
      }
      throw exception;
    }

    try {
      originalBasket = basketOutPort.getBasketById(
          amendConfirmationPricesRequest.getOriginalBookingRef());
    } catch (BasketNotFoundException exception) {
      ExceptionLogger.log(log, exception, "Original basket reference not found");
      throw exception;
    }

    var originalResByBasket = getAllReservationsJustByBasketReference(
        originalBasket.getReference());
    // delete temporary reservations from opera and from basket and delete copied basket
    reservationCleanUp.cleanupTempBasket(temporaryBasket);

    return AmendConfirmationPricesResponse.builder()
        .previousTotal(originalResByBasket.getPreviousTotal())
        .newTotalCost(originalResByBasket.getNewTotal())
        .outstandingBalance(originalResByBasket.getBalanceOutstanding())
        .build();
  }

  @Override
  public ReservationByBasketRefResponse getAllReservationsJustByBasketReference(
      String basketReference) {
    log.info("Entered get all reservations by basket reference with basketReference={}",
        basketReference);

    var basket = basketOutPort.getBasketById(basketReference);
    var reservationsIds = basket.getItems().stream().map(BasketItemResponse::getSourceId).toList();
    var reservations = reservationOhipOutPort
        .getReservationsByIds(basket.getHotelId(), reservationsIds, false);

    reservations.setHotelId(basket.getHotelId());
    for (int i = 0; i < reservationsIds.size(); i++) {
      reservations.getReservationByIdList().get(i).setReservationId(reservationsIds.get(i));
    }
    reservations.setBookingReference(basket.getBookingReference());
    reservations.setHasCityTax(hasCityTaxIncluded(reservations.getReservationByIdList()));

    return reservations;
  }

  @Override
  public AmendSummaryAmountResponse getAmountFromReservationRateInfo(BasketDto basket) {
    var reservationsIds = getReservationIdsfromBasketDto(basket);
    return reservationOhipOutPort.getAmendSummaryDetails(AmendSummaryAmountRequest
        .builder()
        .hotelId(basket.getHotelId())
        .reservationIds(reservationsIds)
        .build());
  }

  private boolean hasCityTaxIncluded(List<ReservationByIdResponse> reservationByIdList) {
    return Optional.of(reservationByIdList).stream()
        .flatMap(Collection::stream)
        .map(ReservationByIdResponse::getReservationPackageList)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .anyMatch(packageItem -> CITYTAX.equals(packageItem.getPackageCode())
            && BigDecimal.ZERO.compareTo(packageItem.getUnitPrice()) < 0);
  }

  private PaymentRequest buildPaymentRequest(ConfirmAmendLogicRequest request,
      BasketResponse originalBasket, BasketResponse tempBasket,
      ReservationByBasketRefResponse tempReservations) {
    return PaymentRequest
        .builder()
        .tmpBasketRef(request.getTempBookingRef())
        .payment(buildPayment(request, originalBasket, tempBasket, tempReservations))
        .booking(buildBooking(request, tempBasket, tempReservations))
        .requestId(UUID.randomUUID().toString())
        .build();
  }

  private PaymentsConfirmation buildPaymentsConfirmation(ConfirmAmendLogicRequest request,
      BasketResponse basket) {
    return PaymentsConfirmation.builder()
        .reference(basket.getReference())
        .paymentId(basket.getPaymentID())
        .token(request.getToken())
        .countryCode("GB")
        .paymentStatus(DEFAULT_PAYMENY_STATUS)
        .bookingReference(basket.getBookingReference())
        .language(request.getBookingChannel().getLanguage() != null
            ? request.getBookingChannel().getLanguage().toLowerCase() :
            DEFAULT_LANGUAGE_EN)
        .channel(request.getBookingChannel().getChannel())
        .paymentOptionSelected(request.getPaymentOptionSelected().toString())
        .emailAddress(request.getEmailAddress())
        .build();
  }

  private Payment buildPayment(ConfirmAmendLogicRequest request, BasketResponse originalBasket,
      BasketResponse tempBasket,
      ReservationByBasketRefResponse tempReservations) {
    return Payment.builder()
        .type(CARD)
        .subType(ECOMM)
        .environment(request.getEnvironment())
        .billing(buildBilling(tempReservations))
        .amount(buildAmount(originalBasket, tempBasket, tempReservations))
        .build();
  }

  private static Booking buildBooking(ConfirmAmendLogicRequest request,
      BasketResponse tempBasket, ReservationByBasketRefResponse tempReservations) {
    var firstRoomStayOptional = getRoomStayByIdResponse(
        tempReservations);

    return Booking.builder()
        .channel(request.getBookingChannel().getChannel())
        .journey(AMEND)
        .type(request.getPaymentOptionSelected().toString())
        .businessSite(buildBusinessSite(tempBasket))
        .reference(tempBasket.getReference())
        .bookingReference(tempBasket.getBookingReference())
        .language(request.getBookingChannel().getLanguage() != null
            ? request.getBookingChannel().getLanguage().toLowerCase() :
            DEFAULT_LANGUAGE_EN)
        .rooms(buildRoomTypes(tempReservations))
        .arrivalDate(
            firstRoomStayOptional.map(RoomStayByIdResponse::getArrivalDate).orElse(null))
        .departureDate(
            firstRoomStayOptional.map(RoomStayByIdResponse::getDepartureDate).orElse(null))
        .build();
  }

  private Amount buildAmount(BasketResponse originalBasket, BasketResponse tempBasket,
      ReservationByBasketRefResponse tempReservations) {
    var originalAmendSummary = getAmountFromRateInfo(originalBasket);
    var tempAmendSummary = getAmountFromRateInfo(tempBasket);
    var payOnArrival = this.calculatePayOnArrival(originalAmendSummary, tempAmendSummary,
        tempBasket.getLinkAmendReservations());
    return Amount.builder()
        .minorUnits(payOnArrival.multiply(BigDecimal.valueOf(100)))
        .currency(buildCurrency(tempReservations))
        .build();
  }

  private String buildCurrency(ReservationByBasketRefResponse tempReservations) {
    return tempReservations.getCurrencyCode();
  }

  private static BusinessSite buildBusinessSite(BasketResponse basket) {
    return BusinessSite.builder()
        .identifier(basket.getHotelId())
        .type(HOTEL)
        .build();
  }

  private static Billing buildBilling(ReservationByBasketRefResponse tempReservations) {
    var billing = getBillingResponse(tempReservations);

    return Billing.builder()
        .title(billing.getTitle())
        .lastName(billing.getLastName())
        .firstName(billing.getFirstName())
        .telephone(billing.getTelephone())
        .email(billing.getEmail())
        .address(buildAddress(billing))
        .build();
  }

  private static BillingResponse getBillingResponse(
      ReservationByBasketRefResponse tempReservations) {
    return tempReservations.getReservationByIdList()
        .stream().findFirst()
        .map(ReservationByIdResponse::getBilling)
        .orElse(new BillingResponse());
  }

  private static Address buildAddress(BillingResponse billingResponse) {
    var address = billingResponse.getAddress();
    if (address == null) {
      return null;
    }
    return Address
        .builder()
        .addressLine1(address.getLine1())
        .addressLine2(address.getLine2())
        .addressLine3(address.getLine3())
        .addressLine4(address.getLine4())
        .country(address.getCountryCode())
        .postalCode(address.getPostalCode())
        .build();
  }

  private String buildAddress(ReservationBookerAddress address) {
    return Stream.of(address.getAddressLine1(), address.getAddressLine2(),
            address.getAddressLine3(), address.getAddressLine4(), address.getCityName())
        .filter(s -> s != null && !s.isEmpty())
        .collect(Collectors.collectingAndThen(
            Collectors.joining(","),
            result -> result.isEmpty() ? null : result
        ));
  }

  private static List<RoomType> buildRoomTypes(ReservationByBasketRefResponse tempReservations) {
    var roomStays = getRoomStays(tempReservations);

    return roomStays.stream()
        .map(roomStay -> RoomType.builder()
            .type(roomStay.getRoomType())
            .rate(roomStay.getRatePlanCode())
            .adultsNumber(roomStay.getAdultsNumber())
            .build())
        .toList();
  }

  private static List<String> getReservationIds(BasketResponse basket) {
    return basket.getItems()
        .stream()
        .map(BasketItemResponse::getSourceId)
        .toList();
  }

  private static List<RoomStayByIdResponse> getRoomStays(
      ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList()
        .stream()
        .map(ReservationByIdResponse::getRoomStay)
        .toList();
  }

  private static Optional<RoomStayByIdResponse> getRoomStayByIdResponse(
      ReservationByBasketRefResponse reservations) {
    return reservations.getReservationByIdList()
        .stream()
        .findFirst()
        .map(ReservationByIdResponse::getRoomStay);
  }

  private void validateToken(String token, String basketReference) {
    if (StringUtils.isEmpty(token) || !TokenUtils.isValid(token, basketReference)) {
      var ex = new InvalidTokenException(ErrorCode.DIGITAL_INVALID_TOKEN1, "Invalid token");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private boolean isFirstRoomDeleted(
      String firstRoom, Set<String> tempReservations,
      Map<String, String> linkAmendReservations) {
    return !tempReservations.contains(linkAmendReservations.get(firstRoom));
  }

  @Override
  public AmendPaymentPageResponse amendPaymentPage(
      AmendPaymentPageRequest amendPaymentPageRequest) {

    if (!authenticatedUserService.isUserAuthenticated()) {
      validateToken(amendPaymentPageRequest.getToken(),
          amendPaymentPageRequest.getOriginalBookingRef());
    }
    var basket = basketOutPort.getBasketById(amendPaymentPageRequest.getOriginalBookingRef());
    var reservations = reservationOhipOutPort.getReservationsByIds(basket.getHotelId(),
        getReservationIds(basket), false);

    var reservationPaymentMethod = reservations.getReservationByIdList()
        .stream()
        .findFirst()
        .map(ReservationByIdResponse::getPaymentCard)
        .map(ReservationPaymentCardType::getPaymentMethod)
        .orElse(null);

    if (isPiba(reservationPaymentMethod)) {
      return prefillPibaFields(reservations, basket,
          buildAmendSummaryRequest(amendPaymentPageRequest), amendPaymentPageRequest);
    } else if (CASH_PAYMENT_METHOD_OPERA_CODE.equalsIgnoreCase(reservationPaymentMethod)
            || (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSetDefaultPaymentMethodDs())
            && DIRECT_SETTLEMENT_PAYMENT_METHOD_OPERA_CODE.equalsIgnoreCase(reservationPaymentMethod))) {
      return RESERVE_WITHOUT_CARD.name().equals(basket.getPaymentOption().name())
          ? prefillRsvWithoutCardFields(reservations, basket, amendPaymentPageRequest)
          : prefillA2cFields(reservations, basket, amendPaymentPageRequest);
    }

    return new AmendPaymentPageResponse();
  }

  private AmendSummaryRequest buildAmendSummaryRequest(
      AmendPaymentPageRequest amendPaymentPageRequest) {
    return AmendSummaryRequest.builder()
        .originalBasketRef(amendPaymentPageRequest.getOriginalBookingRef())
        .copyBasketRef(amendPaymentPageRequest.getTempBookingRef())
        .token(amendPaymentPageRequest.getToken())
        .bookingChannel(amendPaymentPageRequest.getBookingChannel())
        .country(amendPaymentPageRequest.getCountry())
        .build();
  }

  private AmendPaymentPageResponse prefillPibaFields(ReservationByBasketRefResponse reservation,
      BasketResponse basket, AmendSummaryRequest amendSummaryRequest,
      AmendPaymentPageRequest amendPaymentPageRequest) {
    var hotelInformation = getHotelInformation(basket.getHotelId(),
        amendPaymentPageRequest);

    return AmendPaymentPageResponse.builder()
        .discount(reservation.getDiscount())
        .paymentOption(buildPaymentOption(basket))
        .paymentType(PaymentType.PIBA)
        .cardPresent(buildCardPresent(reservation))
        .eckoh(buildEckoh(reservation, amendSummaryRequest))
        .cardHolderName(buildCardHolderName(reservation))
        .billingAddress(BillingAddress.builder().display(Boolean.TRUE).build())
        .emailPreference(buildEmailPreference(reservation))
        .a2cDetails(A2cDetails.builder().display(Boolean.FALSE).build())
        .preAuthCharges(PreAuthCharges.builder().display(Boolean.FALSE).build())
        .allowances(buildPibaAllowances(reservation))
        .purchaseOrder(buildPurchaseOrder(reservation))
        .companyRef(buildCompanyRef(reservation))
        .hotelCode(basket.getHotelId())
        .hotelName(hotelInformation.getName())
        .brand(hotelInformation.getBrand())
        .companyId(reservation.getCompanyId())
        .build();
  }

  private AmendPaymentPageResponse prefillA2cFields(ReservationByBasketRefResponse reservation,
      BasketResponse basket, AmendPaymentPageRequest amendPaymentPageRequest) {
    var hotelInformation = getHotelInformation(basket.getHotelId(),
        amendPaymentPageRequest);

    return AmendPaymentPageResponse.builder()
        .discount(reservation.getDiscount())
        .paymentOption(PaymentOption.builder()
            .payNow(Boolean.FALSE)
            .payOnArrival(Boolean.TRUE)
            .build())
        .paymentType(PaymentType.ACCOUNT_COMPANY)
        .cardPresent(CardPresent.builder().display(Boolean.FALSE).build())
        .eckoh(Eckoh.builder().display(Boolean.FALSE).build())
        .cardHolderName(CardHolderName.builder().display(Boolean.FALSE).build())
        .billingAddress(BillingAddress.builder().display(Boolean.FALSE).build())
        .emailPreference(buildEmailPreference(reservation))
        .allowances(buildA2cAllowances(basket))
        .purchaseOrder(PurchaseOrder.builder().display(Boolean.FALSE).build())
        .companyRef(buildCompanyRef(reservation))
        .a2cDetails(buildA2cDetails(reservation, basket))
        .preAuthCharges(buildPreAuthCharges(basket))
        .hotelCode(basket.getHotelId())
        .hotelName(hotelInformation.getName())
        .brand(hotelInformation.getBrand())
        .companyId(reservation.getCompanyId())
        .build();
  }

  private AmendPaymentPageResponse prefillRsvWithoutCardFields(ReservationByBasketRefResponse reservation,
      BasketResponse basket, AmendPaymentPageRequest amendPaymentPageRequest) {
    var hotelInformation = getHotelInformation(basket.getHotelId(),
        amendPaymentPageRequest);

    return AmendPaymentPageResponse.builder()
        .discount(reservation.getDiscount())
        .paymentOption(PaymentOption.builder()
            .payNow(Boolean.FALSE)
            .payOnArrival(Boolean.TRUE)
            .build())
        .paymentType(RESERVE_WITHOUT_CARD)
        .cardPresent(CardPresent.builder().display(Boolean.FALSE).build())
        .eckoh(Eckoh.builder().display(Boolean.FALSE).enabled(Boolean.TRUE).build())
        .cardHolderName(CardHolderName.builder()
            .display(Boolean.FALSE)
            .name(reservation.getReservationByIdList().get(0).getBilling().getFirstName())
            .surname(reservation.getReservationByIdList().get(0).getBilling().getLastName())
            .build())
        .billingAddress(BillingAddress.builder().display(Boolean.TRUE).build())
        .emailPreference(buildEmailPreference(reservation))
        .allowances(buildRsvWithoutCardAllowances(basket))
        .purchaseOrder(PurchaseOrder.builder()
            .display(Boolean.FALSE)
            .value(reservation.getPurchaseOrderNumber())
            .build())
        .companyRef(CompanyRef.builder()
            .display(Boolean.FALSE)
            .value(reservation.getCustomReferenceNumber())
            .build())
        .a2cDetails(buildA2cDetails(reservation, basket))
        .preAuthCharges(buildPreAuthCharges(basket))
        .hotelCode(basket.getHotelId())
        .hotelName(hotelInformation.getName())
        .brand(hotelInformation.getBrand())
        .companyId(reservation.getCompanyId())
        .availablePaymentType(getAvailablePaymentType(amendPaymentPageRequest.getBookingChannel().getLanguage()))
        .build();
  }

  /*

   */
  private HotelInfoResponse getHotelInformation(String hotelId,
      AmendPaymentPageRequest amendPaymentPageRequest) {
    var language = Optional.ofNullable(amendPaymentPageRequest.getBookingChannel().getLanguage())
        .map(String::toLowerCase)
        .orElse(LANGUAGE_DE);
    var country = Optional.ofNullable(amendPaymentPageRequest.getCountry())
        .map(String::toLowerCase)
        .orElse(COUNTRY_DE);

    return contentOutPort.getHotelInformation(hotelId, country, language);
  }

  private static PreAuthCharges buildPreAuthCharges(BasketResponse basketResponse) {
    List<String> charges = getCharges(basketResponse);

    return PreAuthCharges.builder()
        .display(Boolean.FALSE)
        .charges(charges)
        .build();
  }

  private static List<String> getCharges(BasketResponse basketResponse) {
    return Optional.ofNullable(basketResponse.getCcuiExtraItems())
        .map(CcuiExtraItems::getAccountCompanyItems)
        .map(AccountCompanyItems::getCharges)
        .stream()
        .filter(charges -> !charges.isEmpty())
        .flatMap(charge -> Arrays.stream(charge.split(",")))
        .toList();
  }

  private A2cDetails buildA2cDetails(ReservationByBasketRefResponse reservation,
      BasketResponse basketResponse) {
    var address = getReservationCompanyAddress(reservation);
    var companyNumber = getCompanyNumber(basketResponse);

    return A2cDetails.builder()
        .display(Boolean.TRUE)
        .name(address.getCompanyName())
        .address(buildAddress(address))
        .postcode(address.getPostalCode())
        .number(companyNumber)
        .build();
  }

  private static String getCompanyNumber(BasketResponse basketResponse) {
    return Optional.ofNullable(basketResponse.getCcuiExtraItems())
        .map(CcuiExtraItems::getAccountCompanyItems)
        .map(AccountCompanyItems::getCompanyNumber)
        .orElse(null);
  }

  private static ReservationBookerAddress getReservationCompanyAddress(
      ReservationByBasketRefResponse reservation) {
    return reservation.getReservationByIdList()
        .stream()
        .findFirst()
        .map(ReservationByIdResponse::getReservationCompany)
        .map(ReservationCompany::getAddress)
        .orElse(ReservationBookerAddress.builder().build());
  }

  private CardHolderName buildCardHolderName(ReservationByBasketRefResponse reservation) {
    return CardHolderName.builder()
        .display(Boolean.TRUE)
        .name(reservation.getReservationByIdList().get(0).getBilling().getFirstName())
        .surname(reservation.getReservationByIdList().get(0).getBilling().getLastName())
        .build();
  }

  private EmailPreference buildEmailPreference(ReservationByBasketRefResponse reservation) {
    return EmailPreference.builder()
        .display(Boolean.TRUE)
        .send(reservation.getReservationByIdList().get(0)
            .getReservationEmailNotifications().isSendEmailConfirmation())
        .emailAddress(reservation.getReservationByIdList().get(0).getReservationBooker().getEmail())
        .build();
  }

  private PaymentOption buildPaymentOption(BasketResponse basketResponse) {
    return PaymentOption.builder()
        .payNow(basketResponse.getPaymentOption()
            == uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_NOW)
        .payOnArrival(basketResponse.getPaymentOption()
            == uk.co.whitbread.reservation.domain.model.in.PaymentOption.PAY_ON_ARRIVAL)
        .build();
  }

  private CardPresent buildCardPresent(ReservationByBasketRefResponse reservation) {
    return CardPresent.builder()
        .display(Boolean.TRUE)
        .value(!reservation.getIsCnp())
        .build();
  }

  private Eckoh buildEckoh(ReservationByBasketRefResponse reservation,
      AmendSummaryRequest amendSummaryRequest) {
    return Eckoh.builder()
        .display(Boolean.TRUE)
        .enabled(reservation.getIsCnp() && eckohRequiredForCnp(amendSummaryRequest))
        .build();
  }

  private Allowances buildPibaAllowances(ReservationByBasketRefResponse reservation) {
    return Allowances.builder()
        .display(reservation.getIsCnp())
        .values(Boolean.TRUE.equals(reservation.getIsCnp())
            ? reservationOhipOutPort.getBookingAllowances(reservation.getHotelId(),
            reservation.getReservationByIdList().get(0).getReservationId(), List.of()).getBookingAllowances()
            : null)
        .build();
  }

  private Allowances buildA2cAllowances(BasketResponse basketResponse) {
    return Allowances.builder()
        .display(unleashWrapper.isEnabled(unleashWrapper.featureFlag().getUseBasketAllowances()))
        .values(getA2cBookingAllowances(basketResponse).getBookingAllowances())
        .build();
  }

  private Allowances buildRsvWithoutCardAllowances(BasketResponse basket) {
    return Allowances.builder()
        .display(Boolean.FALSE)
        .values(basket.getBookingAllowances())
        .build();
  }

  private BookingAllowancesResponse getA2cBookingAllowances(BasketResponse basket) {
    if (uk.co.whitbread.reservation.domain.model.in.PaymentOption.ACCOUNT_COMPANY
        .equals(basket.getPaymentOption())
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getUseBasketAllowances())
        && basket.getBookingAllowances() != null) {

      return BookingAllowancesResponse.builder()
          .bookingAllowances(basket.getBookingAllowances())
          .build();
    }

    return BookingAllowancesResponse.builder()
            .bookingAllowances(Collections.emptyList())
            .build();
  }

  private PurchaseOrder buildPurchaseOrder(ReservationByBasketRefResponse reservation) {
    return PurchaseOrder.builder()
        .display(reservation.getIsCnp())
        .value(reservation.getPurchaseOrderNumber())
        .build();
  }

  private CompanyRef buildCompanyRef(ReservationByBasketRefResponse reservation) {
    return CompanyRef.builder()
        .display(reservation.getIsCnp())
        .value(reservation.getCustomReferenceNumber())
        .build();
  }

  private boolean eckohRequiredForCnp(AmendSummaryRequest amendSummaryRequest) {
    var amendSummaryDetails = getAmendSummaryDetails(amendSummaryRequest);
    return (amendSummaryDetails.getTotalCost()
        .subtract(amendSummaryDetails.getPreviousTotal())).compareTo(BigDecimal.ZERO) < 0;
  }

  private boolean isCcuiPaymentProcess(ConfirmAmendLogicRequest request) {
    var bookingChannel = Optional.of(request).map(ConfirmAmendLogicRequest::getBookingChannel)
        .map(BookingChannel::getChannel).orElse(StringUtils.EMPTY);
    var isCcui = CCUI_BOOKING_CHANNEL.equalsIgnoreCase(bookingChannel);

    var isPaymentOptionPiba = "NEW_PIBA".equals(request.getPaymentOption())
        && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCcuiAmendPiba());

    var isPaymentOptionA2c = "ACCOUNT_COMPANY".equals(request.getPaymentOption());

    var isPaymentOptionCNP = "RESERVE_WITHOUT_CARD".equals(request.getPaymentOption());

    return isCcui && (isPaymentOptionCNP || isPaymentOptionPiba || isPaymentOptionA2c);
  }

  private AvailablePaymentType getAvailablePaymentType(String language) {
    var availablePaymentType = AvailablePaymentType.builder().payOnArrival(true).build();
    if (LANGUAGE_DE.equalsIgnoreCase(language)) {
      availablePaymentType.setAccountCompany(true);
    }
    return availablePaymentType;
  }

  private static boolean isPiba(String paymentType) {
    return HotelReservationConstants.PIBA_UK_CARD_TYPE.equalsIgnoreCase(paymentType)
        || HotelReservationConstants.PIBA_EURO_CARD_TYPE.equalsIgnoreCase(paymentType);
  }

  private static List<String> getReservationIdsfromBasketDto(BasketDto basketDto) {
    return basketDto.getItems()
        .stream()
        .map(BasketItemDto::getSourceId)
        .toList();
  }
}
