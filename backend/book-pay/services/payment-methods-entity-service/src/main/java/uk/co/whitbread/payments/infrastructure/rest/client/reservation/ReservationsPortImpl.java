package uk.co.whitbread.payments.infrastructure.rest.client.reservation;

import static java.util.Objects.requireNonNull;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_NOW;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.PAY_ON_ARRIVAL;
import static uk.co.whitbread.payments.domain.model.out.PaymentPolicy.RESERVE_WITHOUT_CARD;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.domain.model.out.BasketReservation;
import uk.co.whitbread.payments.domain.model.out.DepositsResponse;
import uk.co.whitbread.payments.domain.model.out.PaymentPolicy;
import uk.co.whitbread.payments.domain.model.out.RateInfoSummary;
import uk.co.whitbread.payments.domain.model.out.Reservation;
import uk.co.whitbread.payments.domain.ports.secondary.ReservationsPort;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.mapper.DepositFolioMapper;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.DepositsResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.ReservationListDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out.RoomStayDto;
import uk.co.whitbread.payments.infrastructure.rest.client.reservation.service.ReservationClient;

@Slf4j
public class ReservationsPortImpl implements ReservationsPort {

  private final ReservationClient reservationClient;
  private final DepositFolioMapper depositFolioMapper;

  public ReservationsPortImpl(final ReservationClient reservationClient,
      DepositFolioMapper depositFolioMapper) {
    this.reservationClient = reservationClient;
    this.depositFolioMapper = depositFolioMapper;
  }

  @Override
  public Reservation findReservations(final String basketReference, final boolean useCache) {
    log.debug("Entered find reservations for basketReference={}, useCache={}", basketReference, useCache);
    final var reservationsDto = useCache
        ? reservationClient.getCachedReservations(basketReference)
        : reservationClient.findReservations(basketReference);
    return mapToReservation(reservationsDto);
  }

  @Override
  public BasketReservation findBasketReservations(final String basketReference) {
    final var reservationsDto = reservationClient.findReservations(basketReference);
    return mapToBasketReservation(reservationsDto);
  }

  @Override
  public DepositsResponse getDepositFolios(String hotelId, List<String> reservationIds) {
    if (reservationIds == null || reservationIds.isEmpty()) {
      return depositFolioMapper.toDepositFolioModel(new DepositsResponseDto(List.of()));
    }

    List<DepositsDto> allDeposits = reservationIds.stream()
        .filter(Objects::nonNull)
        .map(reservationId -> reservationClient.getDepositFolios(hotelId, reservationId))
        .filter(Objects::nonNull)
        .filter(resp -> resp.getDeposits() != null)
        .flatMap(resp -> resp.getDeposits().stream())
        .toList();

    DepositsResponseDto merged = new DepositsResponseDto(allDeposits);
    return depositFolioMapper.toDepositFolioModel(merged);
  }

  private Reservation mapToReservation(ReservationListDto reservations) {
    return Reservation.builder()
        .hotelId(reservations.getHotelId())
        .hotelPaymentPolicies(paymentPolicies(reservations.getPolicyCode()))
        .departureDate(departureDate(reservations))
        .arrivalDate(arrivalDate(reservations))
        .ratePlanCode(reservations.getReservationByIdList().get(0).getRoomStay().getRatePlanCode())
        .channel(reservations.getChannel())
        .build();
  }

  private BasketReservation mapToBasketReservation(ReservationListDto reservations) {
    var rateInfoSummaryList = reservations.getReservationByIdList().stream()
        .filter(reservation ->
            reservation.getRateInfo() != null
                && reservation.getRateInfo().getSummary() != null)
        .map(reservationInfo -> RateInfoSummary.builder()
            .reservationId(reservationInfo.getReservationId())
            .guestPayAmount(reservationInfo.getRateInfo().getSummary().getGuestPay())
            .routingAmount(reservationInfo.getRateInfo().getSummary().getRouting())
            .currency(reservationInfo.getRateInfo().getSummary().getCurrencyCode())
            .build()
        ).collect(Collectors.toUnmodifiableSet());

    var paymentCard = reservations.getReservationByIdList().stream()
        .map(ReservationDto::getPaymentCard)
        .filter(Objects::nonNull)
        .filter(card -> card.getPaymentMethod() != null)
        .findFirst()
        .orElse(null);

    return BasketReservation.builder()
        .reservationIds(reservations.getReservationByIdList() == null
            ? List.of() : reservations.getReservationByIdList().stream()
            .map(ReservationDto::getReservationId)
            .filter(Objects::nonNull)
            .toList())
        .hotelId(reservations.getHotelId())
        .departureDate(departureDate(reservations))
        .outstandingBalance(reservations.getBalanceOutstanding())
        .totalCost(reservations.getTotalCost())
        .currencyCode(reservations.getCurrencyCode())
        .rateInfoList(rateInfoSummaryList)
        .paymentMethod(paymentCard != null ? paymentCard.getPaymentMethod() : null)
        .hotelPaymentPolicies(paymentPolicies(reservations.getPolicyCode()))
        .folioView(paymentCard != null ? paymentCard.getFolioView() : null)
        .build();
  }

  /**
   * Extract the departure/arrival date of the reservation.
   */
  private LocalDate departureDate(ReservationListDto reservations) {
    log.debug("Entering extracting departure date");
    try {
      return reservations.getReservationByIdList().stream()
            .map(ReservationDto::getRoomStay)
            .map(RoomStayDto::getDepartureDate)
            .map(LocalDate::parse)
            .max(LocalDate::compareTo).orElseThrow();
    } catch (RuntimeException e) {
      var message = "Error while extracting the departure date for reservation";
      var exception = new PaymentMethodsException(ErrorCode.DIGITAL_EXTRACT_DEPARTURE_DATE_EXCEPTION,
            message);
      ExceptionLogger.log(log, exception);
      throw  exception;
    }

  }

  private LocalDate arrivalDate(ReservationListDto reservations) {
    log.debug("Entering extracting arrival date");
    try {
      return reservations.getReservationByIdList().stream()
            .map(ReservationDto::getRoomStay)
            .map(RoomStayDto::getArrivalDate)
            .map(LocalDate::parse)
            .min(LocalDate::compareTo)
            .orElseThrow();
    } catch (RuntimeException e) {
      var message = "Error while extracting the arrivalDate for reservation";
      var exception = new PaymentMethodsException(ErrorCode.DIGITAL_EXTRACT_ARRIVAL_DATE_EXCEPTION,
            message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private Collection<PaymentPolicy> paymentPolicies(String policyCode) {
    return switch (requireNonNull(policyCode)) {
      case "D1" -> Set.of(PAY_NOW);
      case "OA" -> Set.of(PAY_ON_ARRIVAL);
      case "D1A", "NL" -> Set.of(PAY_NOW, PAY_ON_ARRIVAL);
      case "RWC", "DAX" -> Set.of(PAY_NOW, PAY_ON_ARRIVAL, RESERVE_WITHOUT_CARD);
      default -> {
        var message = String.format("Error while getting payment policies. Unsupported policy code %s",
            policyCode);
        var exception = new PaymentMethodsException(ErrorCode.DIGITAL_POLICY_CODE_EXCEPTION,
            message);
        ExceptionLogger.log(log, exception);
        throw  exception;
      }
    };
  }

}
