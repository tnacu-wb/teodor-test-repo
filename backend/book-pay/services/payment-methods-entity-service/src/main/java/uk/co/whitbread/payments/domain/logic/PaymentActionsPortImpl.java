package uk.co.whitbread.payments.domain.logic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.model.out.AggregatedAmounts;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.ChargeType;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.domain.model.out.PaymentAction;
import uk.co.whitbread.payments.domain.model.out.PaymentActionsResponse;
import uk.co.whitbread.payments.domain.model.out.Price;
import uk.co.whitbread.payments.domain.model.out.RateInfoSummary;
import uk.co.whitbread.payments.domain.ports.primary.PaymentActionsPort;
import uk.co.whitbread.payments.domain.ports.secondary.HotelEntityPort;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentActionsPortImpl implements PaymentActionsPort {

  private final HotelEntityPort hotelEntityPort;
  private final ReservationsPort reservationPort;

  private static final String GERMANY_CODE = "DE";
  private static final String DEFAULT_CURRENCY = "GBP";

  @Override
  public PaymentActionsResponse determinePaymentActions(String basketReference) {
    String sanitizedBasketReference = basketReference == null
        ? null
        : basketReference.replace('\n', '_').replace('\r', '_');
    log.info("Determining payment actions for basket: {}", sanitizedBasketReference);
    final var reservation = reservationPort.findBasketReservations(basketReference);

    if (reservation == null) {
      var message = String.format("Reservation not found for basket=%s", sanitizedBasketReference);
      throw new PaymentMethodsException(
          ErrorCode.DIGITAL_RESERVATION_NOT_FOUND_EXCEPTION, message);
    }

    String countryCode = hotelEntityPort.findHotelCountry(reservation.getHotelId());
    log.debug("Hotel country code: {} for hotelId: {}", countryCode, reservation.getHotelId());

    return paymentActionsByReservation(reservation, countryCode);
  }

  private PaymentActionsResponse paymentActionsByReservation(
      BasketReservation reservation, String countryCode) {
    String currency = reservation.getCurrencyCode() != null
        ? reservation.getCurrencyCode()
        : DEFAULT_CURRENCY;

    AggregatedAmounts aggregatedAmounts = aggregateRoomAmounts(reservation.getRateInfoList());

    boolean hasRouting = aggregatedAmounts.hasRoutingAmount();
    DepositsResponse deposits = hasRouting
        ? reservationPort.getDepositFolios(reservation.getHotelId(), reservation.getReservationIds())
        : null;
    boolean noDepositFolios = deposits == null || CollectionUtils.isEmpty(deposits.getDeposits());

    BigDecimal balanceOutstanding = reservation.getOutstandingBalance() != null
        ? reservation.getOutstandingBalance()
        : BigDecimal.ZERO;
    String paymentMethod = reservation.getPaymentMethod();

    return determinePaymentActionsByRule(aggregatedAmounts, countryCode, currency, noDepositFolios,
        balanceOutstanding, paymentMethod);
  }

  private AggregatedAmounts aggregateRoomAmounts(java.util.Set<RateInfoSummary> rateInfoList) {
    java.util.Set<RateInfoSummary> safeRateInfoList =
        rateInfoList == null ? Collections.emptySet() : rateInfoList;
    BigDecimal totalGuestPay = safeRateInfoList.stream()
        .map(RateInfoSummary::getGuestPayAmount)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalRouting = safeRateInfoList.stream()
        .map(RateInfoSummary::getRoutingAmount)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return AggregatedAmounts.builder()
        .totalGuestPay(totalGuestPay)
        .totalRouting(totalRouting)
        .build();
  }


  private PaymentActionsResponse determinePaymentActionsByRule(
      AggregatedAmounts aggregatedAmounts, String countryCode, String currency,
      boolean noDepositFolios, BigDecimal balanceOutstanding, String paymentMethod) {

    boolean hasGuestPay = aggregatedAmounts.requiresGuestPayment();
    boolean hasRouting = aggregatedAmounts.hasRoutingAmount();

    log.debug("Payment rule evaluation - totalGuestPay: {}, totalRouting: {}, country: {}",
        aggregatedAmounts.getTotalGuestPay(), aggregatedAmounts.getTotalRouting(), countryCode);

    // Rule 1: no guest pay and no routing => DE: AUTHORIZE_CARD, others: NO_PAYMENT
    if (!hasGuestPay && !hasRouting) {
      if (GERMANY_CODE.equalsIgnoreCase(countryCode)) {
        log.info("Rule 2: AUTHORIZE_CARD - guestPay=0, routing=0, country=DE");
        return buildSinglePaymentResponse(ChargeType.AUTHORIZE_CARD, BigDecimal.ZERO, currency,
            true);
      }
      log.info("Rule 1: NO_PAYMENT - guestPay=0, routing=0, non-DE country");
      return buildNoPaymentResponse(currency);
    }

    // Rule 2: If routing > 0 and paymentMethod is ‘BU’ or ‘BD’ returns NO_PAYMENT
    if (hasRouting && paymentMethod != null && List.of("BU", "BD").contains(paymentMethod)) {
      return buildNoPaymentResponse(currency);
    }

    // Rule 3: If routing = 0 and guest_pay>0 returns CARD
    if (hasGuestPay && !hasRouting) {
      log.info("Rule 3: CREDIT_CARD - guestPay>0, routing=0");
      return buildSinglePaymentResponse(
          ChargeType.CREDIT_CARD, aggregatedAmounts.getTotalGuestPay(), currency, true);
    }

    // Rule 4: If routing > 0 and guest_pay > 0 and no deposit folios are created in Opera returns
    if (hasGuestPay && hasRouting && noDepositFolios) {
      log.info("Rule 4: CARD_ON_FILE + CREDIT_CARD - guestPay>0, routing>0");
      return buildCombinedPaymentResponse(aggregatedAmounts, currency);
    }

    // Rule 5: If routing > 0 and guest_pay =0 and no deposit folios are created in Opera returns CARD_ON_FILE
    if (!hasGuestPay && hasRouting && noDepositFolios) {
      log.info("Rule 5: CARD_ON_FILE - guestPay=0, routing>0");
      return buildSinglePaymentResponse(
          ChargeType.CARD_ON_FILE, aggregatedAmounts.getTotalRouting(), currency, false);
    }

    // Rule 6: If routing > 0 and  balance_outstanding > 0 and deposit(s) folios are created in Opera returns CARD
    if (hasRouting && (!noDepositFolios) && balanceOutstanding.compareTo(BigDecimal.ZERO) > 0) {
      log.info("Rule 6: CREDIT_CARD - guestPay=0, balanceOutstanding>0");
      return buildSinglePaymentResponse(
          ChargeType.CREDIT_CARD, balanceOutstanding, currency, true);
    }

    // Fallback to no payment
    log.warn("No matching rule found, defaulting to NO_PAYMENT");
    return buildNoPaymentResponse(currency);
  }

  private PaymentActionsResponse buildNoPaymentResponse(String currency) {
    return PaymentActionsResponse.builder()
        .displayPaymentPage(false)
        .paymentActions(
            Collections.singletonList(
                PaymentAction.builder()
                    .chargeType(ChargeType.NO_PAYMENT)
                    .price(Price.builder()
                        .amount(BigDecimal.ZERO)
                        .currency(currency)
                        .build())
                    .order(1)
                    .build()
            )
        )
        .build();
  }

  private PaymentActionsResponse buildSinglePaymentResponse(
      ChargeType chargeType, BigDecimal amount, String currency, boolean displayPage) {
    return PaymentActionsResponse.builder()
        .displayPaymentPage(displayPage)
        .paymentActions(
            Collections.singletonList(
                PaymentAction.builder()
                    .chargeType(chargeType)
                    .price(Price.builder()
                        .amount(amount != null ? amount : BigDecimal.ZERO)
                        .currency(currency)
                        .build())
                    .order(1)
                    .build()
            )
        )
        .build();
  }

  private PaymentActionsResponse buildCombinedPaymentResponse(
      AggregatedAmounts aggregatedAmounts, String currency) {
    List<PaymentAction> actions = new ArrayList<>();

    // Order 1: Card on file (routing/VCC) - aggregated across all rooms
    actions.add(PaymentAction.builder()
        .chargeType(ChargeType.CARD_ON_FILE)
        .price(Price.builder()
            .amount(aggregatedAmounts.getTotalRouting())
            .currency(currency)
            .build())
        .order(1)
        .build());

    // Order 2: Credit card (guest pay) - aggregated across all rooms
    actions.add(PaymentAction.builder()
        .chargeType(ChargeType.CREDIT_CARD)
        .price(Price.builder()
            .amount(aggregatedAmounts.getTotalGuestPay())
            .currency(currency)
            .build())
        .order(2)
        .build());

    return PaymentActionsResponse.builder()
        .displayPaymentPage(true)
        .paymentActions(actions)
        .build();
  }
}

