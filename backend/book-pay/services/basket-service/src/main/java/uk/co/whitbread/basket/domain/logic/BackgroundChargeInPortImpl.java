package uk.co.whitbread.basket.domain.logic;

import static uk.co.whitbread.basket.domain.exception.ErrorCode.DIGITAL_MIT_CC_DEPOSIT_FOLIOS_EXCEPTION;
import static uk.co.whitbread.basket.domain.exception.ErrorCode.DIGITAL_MIT_CC_INVALID_ROUTING_EXCEPTION;
import static uk.co.whitbread.basket.domain.exception.ErrorCode.DIGITAL_MIT_CC_NO_THIRD_PARTY_EXCEPTION;
import static uk.co.whitbread.basket.domain.logic.utils.TokenUtils.validateToken;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.COMPLETED;
import static uk.co.whitbread.basket.domain.model.basket.out.BasketStatus.FAILED;
import static uk.co.whitbread.basket.utils.SanitizingUtils.sanitize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.basket.domain.exception.PaymentException;
import uk.co.whitbread.basket.domain.logic.config.ThirdpartyBookingProperties;
import uk.co.whitbread.basket.domain.logic.mapper.DepositFolioResponseMapper;
import uk.co.whitbread.basket.domain.model.basket.in.Charge;
import uk.co.whitbread.basket.domain.model.basket.in.ChargeAmount;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDeposit;
import uk.co.whitbread.basket.domain.model.basket.in.PrepaidDepositsRequest;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.CleanUpTime;
import uk.co.whitbread.basket.domain.model.payments.in.Address;
import uk.co.whitbread.basket.domain.model.payments.in.Amount;
import uk.co.whitbread.basket.domain.model.payments.in.Billing;
import uk.co.whitbread.basket.domain.model.payments.in.Booking;
import uk.co.whitbread.basket.domain.model.payments.in.BusinessSite;
import uk.co.whitbread.basket.domain.model.payments.in.Card;
import uk.co.whitbread.basket.domain.model.payments.in.Payment;
import uk.co.whitbread.basket.domain.model.payments.in.PaymentRequest;
import uk.co.whitbread.basket.domain.model.payments.in.RoomType;
import uk.co.whitbread.basket.domain.model.payments.out.BasketPaymentStatus;
import uk.co.whitbread.basket.domain.model.payments.out.PaymentResponse;
import uk.co.whitbread.basket.domain.model.refund.in.Refund;
import uk.co.whitbread.basket.domain.model.refund.in.RefundReason;
import uk.co.whitbread.basket.domain.model.refund.in.RefundRequest;
import uk.co.whitbread.basket.domain.model.refund.in.RefundType;
import uk.co.whitbread.basket.domain.model.reservation.in.DepositFoliosRequest;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.basket.domain.model.reservation.out.DepositFolioResponse;
import uk.co.whitbread.basket.domain.model.reservation.out.Reservation;
import uk.co.whitbread.basket.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.basket.domain.model.rules.out.TransactionCode;
import uk.co.whitbread.basket.domain.model.rules.out.VatRuleResponse;
import uk.co.whitbread.basket.domain.ports.primary.BackgroundChargeInPort;
import uk.co.whitbread.basket.domain.ports.primary.BasketInPort;
import uk.co.whitbread.basket.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.PaymentOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RefundOutPort;
import uk.co.whitbread.basket.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;


@Slf4j
@AllArgsConstructor
public class BackgroundChargeInPortImpl implements BackgroundChargeInPort {

  private final HotelReservationOutPort reservationOutPort;
  private final PaymentOutPort paymentOutPort;
  private final RulesAgentOutPort rulesAgentOutPort;
  private final BasketOutPort basketOutPort;
  private final BasketInPort basketInPort;
  private final RefundOutPort refundOutPort;
  private final CleanUpTime cleanUpTime;
  private final DepositFolioResponseMapper depositsMapper;
  private final ThirdpartyBookingProperties thirdpartyBookingProperties;
  private static final String ERROR_MESSAGE =
      "Background charge completed but deposit folios could not be saved for basketId=%s";

  /**
   * Processes a background charge for a basket.
   *
   * @param basketReference basket reference used to load reservation and basket data
   * @param token           token used to validate background charge eligibility
   */
  @Override
  public void processBackgroundCharge(String basketReference, String token) {
    log.info("backgroundChargeProcess(): Received paymentRequest for basketReference={}",
        sanitize(basketReference));

    validateToken(token, basketReference);

    // Get booking details from reservationOutPort
    ReservationByBasketRefResponse reservationResponse = reservationOutPort.getReservationsByBasketReference(
        basketReference, "false", false);

    List<Reservation> reservations = reservationResponse.getReservationByIdList();

    BigDecimal routing = calculateRouting(reservations);

    //  Check if routing > 0 — derived from rateInfo.summary.routing across all reservations
    if (routing.compareTo(BigDecimal.ZERO) <= 0) {
      var paymentException = new PaymentException(
          DIGITAL_MIT_CC_INVALID_ROUTING_EXCEPTION,
          String.format("No routing for basketReference: %s", sanitize(basketReference)));

      ExceptionLogger.log(log, paymentException);
      throw paymentException;
    }

    // Check if 3rdParty booking
    final var basket = basketOutPort.getBasketById(basketReference);
    if (!is3rdParty(basket)) {
      var paymentException = new PaymentException(
          DIGITAL_MIT_CC_NO_THIRD_PARTY_EXCEPTION,
          String.format("No 3rdParty for basketReference: %s", sanitize(basketReference)));

      ExceptionLogger.log(log, paymentException);
      throw paymentException;
    }

    if (reservationResponse.getReservationByIdList() == null
        || reservationResponse.getReservationByIdList().isEmpty()) {
      return;
    }
    // Create payment request with type=CARD and subtype=MIT_CC
    PaymentRequest paymentRequest = buildPaymentRequest(routing, reservationResponse,
        basketReference, basket);

    var response = paymentOutPort.createMitCcPayment(paymentRequest);
    var paymentId = response.getPaymentId();

    try {

      var deposits = prepareDepositFolios(reservationResponse);

      if (deposits.getFirst().isEmpty() || deposits.getSecond().isEmpty()) {
        throw buildDepositFoliosPaymentException(basket.getBasketId(),
            "No deposit folios prepared");
      }

      PrepaidDepositsRequest prepaidDepositRequest = PrepaidDepositsRequest.builder()
          .prepaidDeposits(deposits.getFirst()).build();

      basketInPort.saveCharges(prepaidDepositRequest);

      updateBasket(basket, response);

      log.info("Saved {} deposits for basketReference: {} in basket ", deposits.getFirst().size(),
          sanitize(basketReference));

      DepositFoliosRequest depositsRequest = DepositFoliosRequest.builder()
          .depositFolios(depositsMapper.toDepositRequestModel(deposits.getSecond()))
          .build();
      reservationOutPort.saveDepositFolios(depositsRequest);

      log.info("Saved {} deposits for basketReference: {} in opera", deposits.getSecond().size(),
          sanitize(basketReference));

    } catch (Exception exception) {
      handleExceptionScenario(exception, basketReference, basket, routing, paymentId);
    }
  }

  /**
   * Handles exceptions raised during background charge processing.
   *
   * <p>This method logs the failure, normalizes non-payment exceptions to a payment exception,
   * triggers compensating refund, and rethrows the resulting exception.
   *
   * @param exception       original exception raised during processing
   * @param basketReference basket reference for logging context
   * @param basket          basket used for refund and status update
   * @param routing         routing amount used by refund logic
   * @param paymentId       paymentId used by refund logic
   * @throws PaymentException normalized payment exception after refund attempt
   */
  private void handleExceptionScenario(Exception exception, String basketReference, Basket basket,
      BigDecimal routing, String paymentId) {
    log.error("Background charge failed for basketReference={}",
        sanitize(basketReference), exception);

    var paymentException = exception instanceof PaymentException ex
        ? ex
        : buildDepositFoliosPaymentException(basket.getBasketId(), exception.getMessage());

    refund(basket, routing, paymentId);
    throw paymentException;
  }


  /**
   * Determines whether the basket belongs to a third-party context.
   *
   * <p>Checks if the basket's idContext is set to "3rdParty" (case-insensitive).
   * Background charge is only applicable for third-party bookings.
   *
   * @param basket basket to inspect for third-party context
   * @return true if basket is not null and idContext equals "3rdParty" (case-insensitive)
   */
  private static boolean is3rdParty(Basket basket) {
    return basket != null && basket.getIdContext() != null
        && "3rd Party".equalsIgnoreCase(basket.getIdContext());
  }


  /**
   * Calculates the total routing amount across reservations.
   *
   * <p>Aggregates routing amounts from all reservations that have valid rate info
   * and routing values. Returns ZERO if no reservations have routing data.
   *
   * @param reservations list of reservations to compute total routing for
   * @return aggregated routing amount (sum of all reservation routings)
   */
  private static BigDecimal calculateRouting(List<Reservation> reservations) {
    return reservations.stream()
        .filter(r -> r.getRateInfo() != null
            && r.getRateInfo().getSummary() != null
            && r.getRateInfo().getSummary().getRouting() != null)
        .map(r -> r.getRateInfo().getSummary().getRouting())
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Builds prepaid deposit and folio collections from preview deposits per reservation.
   *
   * <p>This method retrieves preview deposits for all provided reservations in a single
   * batch request, applies city-tax filtering logic, fetches VAT codes if needed, and returns both
   * the prepaid deposits for basket persistence and the deposit folios for Opera synchronization.
   *
   * @param reservationResponse reservation response containing hotel ID and other context, reservations
   *        to inspect for preview deposits; used to extractreservation IDs and locate city tax packages
   * @return Pair where: - first element: List of PrepaidDeposit objects for saving to basket
   *         storage - second element: List of DepositFolioResponse objects for Opera folio
   *         updates. Both lists are populated only for non-city-tax deposits
   * @throws PaymentException if preview deposits cannot be retrieved or if city tax VAT rules fail
   * @see #filterByNonCityTaxCharges(DepositFolioResponse, List)
   * @see #getCityTaxPackageCodes(Reservation)
   */
  private Pair<List<PrepaidDeposit>, List<DepositFolioResponse>> prepareDepositFolios(
      ReservationByBasketRefResponse reservationResponse) {
    //Get preview deposits for each reservation and filter by city tax
    List<PrepaidDeposit> prepaidDeposits = new ArrayList<>();
    List<DepositFolioResponse> depositFoliosOpera = new ArrayList<>();

    var reservationIds = reservationResponse.getReservationByIdList().stream()
        .map(Reservation::getReservationId)
        .collect(Collectors.toSet());

    var previewDepositsResponse = reservationOutPort.getPreviewDepositsForReservationId(
        reservationResponse.getHotelId(), reservationIds);

    if (previewDepositsResponse != null
        && (!CollectionUtils.isEmpty(previewDepositsResponse.getDepositFolios()))) {

      for (DepositFolioResponse depositFolio : previewDepositsResponse.getDepositFolios()) {

        var reservationOptional = reservationResponse.getReservationByIdList().stream().filter(
            depositFolioReservation -> depositFolioReservation.getReservationId()
                .equals(depositFolio.getReservationId())).findFirst();
        if (reservationOptional.isEmpty()) {
          log.warn("No reservation found for depositFolio reservationId={}",
              sanitize(depositFolio.getReservationId()));
          continue;
        }
        var reservation = reservationOptional.get();

        // Get city tax package codes from reservation
        List<String> cityTaxPackageCodes = getCityTaxPackageCodes(reservation);

        var depositOpera =
            filterByNonCityTaxCharges(depositFolio, cityTaxPackageCodes);

        if (depositOpera != null) {
          depositFoliosOpera.add(depositOpera);
          prepaidDeposits.add(
              prepaidDepositsPerReservation(depositFolio.getReservationId(), depositOpera));
        }
      }
    }
    return Pair.of(prepaidDeposits, depositFoliosOpera);
  }

  /**
   * Maps deposit folio records to prepaid deposits for a reservation.
   *
   * <p>Converts Opera deposit folio charge records into domain model prepaid deposits
   * that can be stored in the basket database. Each folio becomes a single PrepaidDeposit with its
   * charges extracted and mapped to the basket charge model.
   *
   * @param reservationId reservation ID associated with the deposits
   * @param deposit       deposit folios returned by Opera preview service
   * @return PrepaidDeposit object mapped from the provided folio
   */
  private PrepaidDeposit prepaidDepositsPerReservation(String reservationId,
      DepositFolioResponse deposit) {

    var prePaidDepositCharges = deposit.getCharges().stream()
        .map(charge -> Charge.builder()
            .chargeAmount(ChargeAmount.builder()
                .currencyCode(charge.getCurrencyAmount().getCurrencyCode())
                .amount(charge.getCurrencyAmount().getAmount()).build())
            .transactionCode(charge.getTransactionCode())
            .postingQuantity(charge.getQuantity())
            .postingReference(charge.getReference())
            .build())
        .toList();

    return PrepaidDeposit.builder()
        .reservationId(reservationId)
        .charges(prePaidDepositCharges)
        .build();
  }


  /**
   * Filters city-tax charges from a deposit folio, returning only non-city-tax charges.
   *
   * <p>If {@code cityTaxPackageCodes} is empty, the folio is returned unchanged.
   * Otherwise, the method resolves the VAT transaction codes for the given city-tax packages
   * via the rules agent, strips any charges whose transaction code matches those VAT codes,
   * and updates the folio's charge list with the remaining non-city-tax charges.
   *
   * <ul>
   *   <li>If no VAT rules are found for the packages, the folio is excluded ({@code null} returned)
   *       and an error is logged.</li>
   *   <li>If all charges are city-tax-related (no non-city-tax charges remain), the folio is
   *       excluded ({@code null} returned).</li>
   *   <li>If at least one non-city-tax charge remains, the folio is returned with its charge list
   *       updated to contain only those charges.</li>
   * </ul>
   *
   * @param deposit             deposit folio whose charges are to be filtered
   * @param cityTaxPackageCodes list of city-tax package codes (e.g., {@code CITYTAX}, {@code CITYEXP})
   *                            used to look up the corresponding VAT transaction codes
   * @return the deposit folio with city-tax charges removed, or {@code null} if no VAT rules are
   *         found or no non-city-tax charges remain
   */
  private DepositFolioResponse filterByNonCityTaxCharges(DepositFolioResponse deposit,
      List<String> cityTaxPackageCodes) {

    if (cityTaxPackageCodes.isEmpty()) {
      return deposit;
    }

    var vatRegion = deposit.getVatRegion();

    //Get VAT codes for city tax packages
    VatRuleResponse vatRuleResponse = rulesAgentOutPort.getVatCodes(
        vatRegion, cityTaxPackageCodes);

    if (vatRuleResponse == null || CollectionUtils.isEmpty(vatRuleResponse.getTranCodes())) {
      log.error("No VAT rules found for packages: {}", cityTaxPackageCodes);
      return null;
    }

    var transCodeList = vatRuleResponse.getTranCodes().stream()
        // Find the first matching transaction code for city tax packages
        .filter(tc -> cityTaxPackageCodes.contains(tc.getPkgCode()))
        .map(TransactionCode::getTranCode)
        .toList();

    var chargeList = chargeWithoutVatCode(deposit, transCodeList);
    if (!chargeList.isEmpty()) {
      deposit.setCharges(chargeList);
      return deposit;
    }
    return null;
  }

  /**
   * Gets city tax package codes from reservation packages.
   *
   * <p>Extracts all unique city tax-related package codes from the reservation's
   * package list. Filters for CITYEXP (city express tax) and CITYTAX package codes.
   *
   * @param reservation the reservation to inspect for package codes
   * @return list of unique city tax package codes; empty if no packages or no city tax packages
   *         found
   */
  private List<String> getCityTaxPackageCodes(Reservation reservation) {
    var cityTaxList = thirdpartyBookingProperties.getCityTaxList();
    if (CollectionUtils.isEmpty(reservation.getReservationPackageList()) || CollectionUtils.isEmpty(
        cityTaxList)) {
      return Collections.emptyList();
    }

    return reservation.getReservationPackageList().stream()
        .map(pkg -> pkg.getPackageCode() != null ? pkg.getPackageCode() : "")
        .filter(cityTaxList::contains)
        .distinct()
        .toList();
  }


  /**
   * Filters the deposit folio's charge list by excluding any charges whose transaction
   * code appears in the provided list. Used to strip city-tax-related charges from a folio,
   * retaining only the non-city-tax charges for further processing.
   *
   * @param deposit       deposit folio whose charges are to be filtered
   * @param transCodeList list of city-tax transaction codes to exclude from the result
   * @return list of {@link DepositFolioCharge} objects whose transaction codes are not in
   *         {@code transCodeList}; empty list if all charges match city-tax codes
   */
  private List<DepositFolioCharge> chargeWithoutVatCode(DepositFolioResponse deposit, List<String> transCodeList) {
    return
        deposit.getCharges().stream()
            .filter(depositFolioCharge -> !transCodeList.contains(depositFolioCharge.getTransactionCode()))
            .toList();

  }

  /**
   * Performs compensating refund flow and marks basket as failed.
   *
   * <p>Initiates a refund request, updates basket payment status to REFUNDING,
   * marks the basket as FAILED, sets the cleanup time, and persists the basket state.
   *
   * @param basket    basket to update and refund
   * @param routing   total routed amount to refund (used as refund amount)
   * @param paymentId payment ID associated with the failed payment
   */
  private void refund(Basket basket, BigDecimal routing, String paymentId) {
    var refundRequest = createRefundRequest(basket.getHotelId(), basket.getCurrency(), routing);
    refundOutPort.processRefund(basket.getBasketId(), refundRequest, paymentId);

    basket.setPaymentID(paymentId);
    basket.setPaymentStatus(BasketPaymentStatus.REFUNDING);
    basket.setStatus(FAILED);
    basket.setCleanUpTime(cleanUpTime.getCleanUpTime(basket));

    basketOutPort.updateBasket(basket);
  }

  /**
   * Updates basket state after successful payment processing.
   *
   * <p>Sets the basket to COMPLETED status with payment completed status, stores the
   * payment ID and payment details (channel, option) and persists the
   * updated state to storage.
   *
   * @param basket  basket to update with payment results
   * @param response the payment response
   * @param response payment response containing payment ID and booking details (channel, type)

   */

  private void updateBasket(Basket basket, PaymentResponse response) {
    basket.setPaymentStatus(BasketPaymentStatus.COMPLETED);
    basket.setStatus(COMPLETED);
    basket.setPaymentID(response.getPaymentId());
    basket.setTotalCost(response.getPayment().getAmount().getMinorUnits().toString());
    basket.setCurrency(response.getPayment().getAmount().getCurrency());
    basketOutPort.updateBasketWithPaymentStatus(basket);
  }

  /**
   * Creates refund request payload based on routing amount.
   *
   * <p>Constructs a RefundRequest with FULL refund type.
   * Uses ROLLBACK as the refund reason.
   *
   * @param hotelCode hotel code for refund request
   * @param currency  currency code for refund amount
   * @param routing   routing amount to refund (converted to minorUnits)
   * @return refund request ready for refund service
   */
  private RefundRequest createRefundRequest(String hotelCode, String currency, BigDecimal routing) {
    return RefundRequest.builder()
        .refundType(RefundType.FULL)
        .hotelCode(hotelCode)
        .refund(Refund
            .builder()
            .amount(
                uk.co.whitbread.basket.domain.model.refund.in.Amount.builder().currency(currency)
                    .minorUnits(routing).build())
            .reason(RefundReason.ROLLBACK)
            .build())
        .build();
  }


  /**
   * Builds payment request payload for MIT card charging.
   *
   * <p>Constructs a complete PaymentRequest with CARD/MIT_CC payment type for charging
   * the customer's stored card via Merchant Initiated Transaction flow. Includes payment amount,
   * cardholder details, billing address, and booking details extracted from the reservation
   * response and basket context.
   *
   * @param routing             routed amount used as the payment charge amount
   * @param reservationResponse reservation response containing booking reference, guest, and
   *                            payment card details
   * @param basketReference     basket reference sent to payment service for tracking
   * @param basket              basket carrying channel and idContext (3rdParty) details
   * @return payment request ready for submission to payment service
   */
  private PaymentRequest buildPaymentRequest(BigDecimal routing,
      ReservationByBasketRefResponse reservationResponse, String basketReference, Basket basket) {

    Amount amount = Amount.builder()
        .currency(reservationResponse.getCurrencyCode())
        .minorUnits(routing)
        .build();
    Payment payment = Payment.builder()
        .type("CARD")
        .subType("MIT_CC")
        .amount(amount)
        .build();
    var paymentCard = reservationResponse.getReservationByIdList().get(0).getPaymentCard();
    if (paymentCard != null) {
      var card = Card.builder()
          .cardholderName(paymentCard.getCardHolderName())
          .cardType(paymentCard.getCardType())
          .token(paymentCard.getToken())
          .expiryMonth(Optional.ofNullable(paymentCard.getExpirationDate()).isPresent()
              ? String.valueOf(LocalDate.parse(paymentCard.getExpirationDate()).getMonthValue()) : "")
          .expiryYear(Optional.ofNullable(paymentCard.getExpirationDate()).isPresent()
              ? String.format("%02d", LocalDate.parse(paymentCard.getExpirationDate()).getYear() % 100) : "")
          .build();
      payment.setCard(card);
    }

    payment.setBilling(billingAddress(reservationResponse));

    var booking = Booking.builder()
        .channel(basket.getChannel())
        .journey("BOOKING")
        .type("PAY_NOW")
        .bookingReference(reservationResponse.getBookingReference())
        .reference(basketReference)
        .businessSite(BusinessSite.builder()
            .identifier(reservationResponse.getHotelId())
            .type("HOTEL")
            .build())
        .rooms(reservationResponse.getReservationByIdList().stream()
            .map(reserv -> {
              if (reserv.getRoomStay() != null) {
                return RoomType.builder()
                    .type(reserv.getRoomStay().getRoomType())
                    .adultsNumber(reserv.getRoomStay().getAdultsNumber())
                    .rate(reserv.getRoomStay().getRatePlanCode())
                    .build();
              } else {
                return null;
              }
            }).filter(Objects::nonNull).toList())
        .language("en")
        .build();

    if (reservationResponse.getReservationByIdList().get(0).getRoomStay() != null
        && reservationResponse.getReservationByIdList().get(0).getRoomStay().getArrivalDate()
        != null) {
      booking.setArrivalDate(
          reservationResponse.getReservationByIdList().get(0).getRoomStay().getArrivalDate());
    }
    if (reservationResponse.getReservationByIdList().get(0).getRoomStay() != null
        && reservationResponse.getReservationByIdList().get(0).getRoomStay().getDepartureDate()
        != null) {
      booking.setDepartureDate(
          reservationResponse.getReservationByIdList().get(0).getRoomStay().getDepartureDate());
    }

    // Create PaymentRequest
    return PaymentRequest.builder()
        .requestId(UUID.randomUUID().toString())
        .tmpBasketRef(basketReference)
        .payment(payment)
        .booking(booking)
        .build();
  }

  private Billing billingAddress(ReservationByBasketRefResponse reservationResponse) {
    var billing = reservationResponse.getReservationByIdList().get(0).getBilling();
    if (billing != null) {
      var billingAddrs = Billing.builder()
          .title(billing.getTitle())
          .firstName(billing.getFirstName())
          .lastName(billing.getLastName())
          .email(billing.getEmail())
          .build();
      if (billing.getAddress() != null) {
        var addr = Address.builder().line1(billing.getAddress().getAddressLine1())
            .line2(billing.getAddress().getAddressLine2())
            .line3(billing.getAddress().getAddressLine3())
            .line4(billing.getAddress().getAddressLine4())
            .cityName(billing.getAddress().getCityName())
            .postalCode(billing.getAddress().getPostalCode())
            .countryCode(billing.getAddress().getCountry())
            .build();
        billingAddrs.setAddress(addr);
      }
      return billingAddrs;
    } else if (!CollectionUtils.isEmpty(
        reservationResponse.getReservationByIdList().get(0).getReservationGuestList())) {
      return Billing.builder()
          .firstName(
              reservationResponse.getReservationByIdList().get(0).getReservationGuestList().get(0)
                  .getGivenName())
          .lastName(
              reservationResponse.getReservationByIdList().get(0).getReservationGuestList().get(0)
                  .getSurName())
          .address(Address.builder().line1("UNKNOWN").build())
          .build();
    }
    return null;
  }

  /**
   * Builds a payment exception for deposit folio persistence failures.
   *
   * <p>Creates a PaymentException with DIGITAL_MIT_CC_DEPOSIT_FOLIOS_EXCEPTION error code
   * and formats a message including the affected basket ID. Logs the exception with the provided
   * diagnostic message for troubleshooting.
   *
   * @param basketId basket identifier included in the exception message (sanitized in logs)
   * @param message  additional diagnostic message for exception logging and troubleshooting
   * @return PaymentException with deposit folio failure error code and formatted message
   */
  private PaymentException buildDepositFoliosPaymentException(String basketId, String message) {

    var paymentException = new PaymentException(
        DIGITAL_MIT_CC_DEPOSIT_FOLIOS_EXCEPTION,
        String.format(ERROR_MESSAGE, basketId));

    ExceptionLogger.log(log, paymentException, message);
    return paymentException;
  }

}
