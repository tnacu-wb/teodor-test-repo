package uk.co.whitbread.booking.infrastructure.rest.client.ohip;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import uk.co.whitbread.booking.domain.model.history.out.Booking;
import uk.co.whitbread.booking.domain.ports.secondary.BasketOutPort;
import uk.co.whitbread.booking.domain.ports.secondary.PaymentInfoOutPort;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.ReservationInfoPaymentTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out.UniqueIdTypeDto;
import uk.co.whitbread.booking.infrastructure.rest.client.ohip.service.OhipAdapterClient;

@Slf4j
@RequiredArgsConstructor
public class PaymentInfoOutPortImpl implements PaymentInfoOutPort {

  private static final Integer FOLIO_WINDOW_ONE = 1;
  private static final Integer FOLIO_WINDOW_TWO = 2;
  private static final String PIBA_UK_CARD_TYPE = "BU";
  private static final String PIBA_EURO_CARD_TYPE = "BD";

  private final OhipAdapterClient ohipAdapterClient;
  private final BasketOutPort basketOutPort;

  /**
   * Retrieves a list of booking references for PIBA CP reservations based on exclusion flags.
   * This method processes the provided bookings and applies the following logic:
   * <ul>
   *   <li>If the input bookings are null or empty, returns an empty list.</li>
   *   <li>If both {@code excludePibaCP} and {@code excludePibaCNP} are false,
   *   returns an empty list (no filtering is performed).</li>
   *   <li>Otherwise, fetches basket source IDs, collects payment types that match PIBA CP/CNP criteria,
   *   and returns a distinct list of booking references associated with valid PIBA CP reservations.</li>
   *   <li>If no valid PIBA CP payment types are found, returns an empty list.</li>
   * </ul>
   *
   * @param bookings       List of Booking objects to process. May be null or empty.
   * @param excludePibaCP  Boolean flag to exclude PIBA CP reservations. If both this and excludePibaCNP are false,
   *                       returns an empty list.
   * @param excludePibaCNP Boolean flag to exclude PIBA CNP reservations. If both this and excludePibaCP are false,
   *                       returns an empty list.
   * @return List of booking references for PIBA CP reservations,
   *         or an empty list if none found or if input is null/empty.
   */
  @Override
  public List<String> getPibaCpReservations(List<Booking> bookings, boolean excludePibaCP,
      boolean excludePibaCNP) {

    if (bookings == null || bookings.isEmpty()) {
      return Collections.emptyList();
    }

    if (!excludePibaCP && !excludePibaCNP) {
      return Collections.emptyList();
    }

    Map<String, Set<String>> baskets = basketOutPort.getBasketSourceIdsForBookingRefs(
        bookings.stream()
            .map(Booking::getBookingReference)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet()));

    List<ReservationInfoPaymentTypeDto> pibaPaymentTypes = getPibaReservationPaymentType(
        bookings, baskets, excludePibaCP, excludePibaCNP);

    if (pibaPaymentTypes == null || pibaPaymentTypes.isEmpty()) {
      return Collections.emptyList();
    }

    Map<String, Set<String>> invertedBaskets = invertBasketMap(baskets);

    return pibaPaymentTypes.stream()
        .map(ReservationInfoPaymentTypeDto::getIds)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .filter(Objects::nonNull)
        .filter(uniqueIdTypeDto -> "Reservation".equals(uniqueIdTypeDto.getType()))
        .map(UniqueIdTypeDto::getId)
        .distinct()
        .filter(Objects::nonNull)
        .map(invertedBaskets::get)
        .filter(Objects::nonNull)
        .flatMap(Set::stream)
        .distinct()
        .toList();
  }

  /**
   * Retrieves all PIBA reservation payment types for the given bookings.
   * Groups bookings by hotel code, then asynchronously fetches payment types for each hotel.
   * Returns a list of valid PIBA payment type DTOs.
   *
   * @param bookings       List of Booking objects to process
   * @param baskets        Map of booking reference to basket source IDs
   * @param excludePibaCP  Boolean flag to exclude PIBA CP reservations
   * @param excludePibaCNP Boolean flag to exclude PIBA CNP reservations
   * @return List of ReservationInfoPaymentTypeDto for PIBA CP reservations
   */
  private List<ReservationInfoPaymentTypeDto> getPibaReservationPaymentType(
      List<Booking> bookings, Map<String, Set<String>> baskets, boolean excludePibaCP,
      boolean excludePibaCNP) {
    var bookingReferencesByHotel = bookings.stream()
        .filter(booking -> booking != null
            && booking.getHotelCode() != null
            && booking.getBookingReference() != null)
        .collect(Collectors.groupingBy(Booking::getHotelCode,
            Collectors.mapping(Booking::getBookingReference, Collectors.toSet())));
    if (bookingReferencesByHotel.isEmpty()) {
      return Collections.emptyList();
    }
    return getPibaPaymentTypes(bookingReferencesByHotel, baskets, excludePibaCP, excludePibaCNP)
        .collectList()
        .block();
  }

  /**
   * Asynchronously fetches PIBA CP reservation payment types for each hotel.
   * For each hotel code, fetches basket source IDs and payment types, filters for PIBA CP or CNP,
   * and emits a Flux of valid payment type DTOs.
   *
   * @param bookingReferencesByHotel Map of hotel code to booking references
   * @param baskets                  Map of booking reference to basket source IDs
   * @param excludePibaCP            Boolean flag to exclude PIBA CP reservations
   * @param excludePibaCNP           Boolean flag to exclude PIBA CNP reservations
   * @return Flux emitting ReservationInfoPaymentTypeDto for PIBA CP/CNP reservations
   */
  private Flux<ReservationInfoPaymentTypeDto> getPibaPaymentTypes(
      Map<String, Set<String>> bookingReferencesByHotel, Map<String, Set<String>> baskets,
      boolean excludePibaCP, boolean excludePibaCNP) {
    return Flux.fromIterable(bookingReferencesByHotel.entrySet())
        .flatMap(entry -> {
          String hotelCode = entry.getKey();
          Set<String> bookingRefs = entry.getValue();
          return Flux.fromIterable(bookingRefs)
              .flatMap(bookingRefId -> {
                Set<String> ids = baskets.get(bookingRefId);
                if (ids == null || ids.isEmpty()) {
                  return Flux.empty();
                }
                List<ReservationInfoPaymentTypeDto> paymentTypes =
                    ohipAdapterClient.getReservationsPaymentTypeByReservationIds(hotelCode, ids);
                if (paymentTypes == null || paymentTypes.isEmpty()) {
                  return Flux.empty();
                }
                return Flux.fromIterable(paymentTypes)
                    .filter(paymentType -> isPiba(paymentType, excludePibaCP, excludePibaCNP));
              });
        });
  }

  /**
   * Determines if a payment type matches PIBA CP or PIBA CNP criteria.
   * Checks if the payment method is PIBA UK or PIBA EURO and applies exclusion logic for CP/CNP.
   *
   * @param paymentType ReservationInfoPaymentTypeDto to check
   * @param excludePibaCP Boolean flag to exclude PIBA CP reservations
   * @param excludePibaCNP Boolean flag to exclude PIBA CNP reservations
   * @return true if payment type matches PIBA CP/CNP criteria and is not excluded, false otherwise
   */
  private static boolean isPiba(ReservationInfoPaymentTypeDto paymentType, boolean excludePibaCP,
      boolean excludePibaCNP) {
    List<String> pibaTypes = List.of(PIBA_UK_CARD_TYPE, PIBA_EURO_CARD_TYPE);
    return paymentType != null && paymentType.getPaymentCardType() != null
        && paymentType.getPaymentCardType().getPaymentMethod() != null
        && (pibaTypes.contains(paymentType.getPaymentCardType().getPaymentMethod()))
        && isPibaExcluded(paymentType, excludePibaCP, excludePibaCNP);
  }

  /**
   * Determines if a payment type should be excluded based on PIBA CP or PIBA CNP exclusion flags.
   * Excludes the payment type if:
   * <ul>
   *   <li>{@code excludePibaCP} is true and the payment type's folio view is 1 (PIBA CP)</li>
   *   <li>{@code excludePibaCNP} is true and the payment type's folio view is 2 (PIBA CNP)</li>
   * </ul>
   *
   * @param paymentType The payment type DTO to check for exclusion.
   * @param excludePibaCP If true, exclude PIBA CP payment types (folio view 1).
   * @param excludePibaCNP If true, exclude PIBA CNP payment types (folio view 2).
   * @return {@code true} if the payment type should be excluded, {@code false} otherwise.
   */
  private static boolean isPibaExcluded(ReservationInfoPaymentTypeDto paymentType,
      boolean excludePibaCP, boolean excludePibaCNP) {

    if (FOLIO_WINDOW_ONE.equals(paymentType.getPaymentCardType().getFolioView())) {
      return excludePibaCP;
    }
    if (FOLIO_WINDOW_TWO.equals(paymentType.getPaymentCardType().getFolioView())) {
      return excludePibaCNP;
    }
    return false;
  }

  /**
   * Inverts a Map so that each value in the set becomes a key, and the original key becomes the
   * value in a set.
   * Example: {bookingRef1: [sourceId1, sourceId2]} => {sourceId1: [bookingRef1], sourceId2:
   * [bookingRef1]}
   *
   * @param baskets Map to be inverted
   * @return Inverted Map where each sourceId maps to a set of booking references
   */
  private Map<String, Set<String>> invertBasketMap(Map<String, Set<String>> baskets) {
    Map<String, Set<String>> inverted = new java.util.HashMap<>();
    for (Map.Entry<String, Set<String>> entry : baskets.entrySet()) {
      String bookingRef = entry.getKey();
      Set<String> sourceIds = entry.getValue();
      if (sourceIds != null) {
        for (String sourceId : sourceIds) {
          inverted.computeIfAbsent(sourceId, k -> new java.util.HashSet<>()).add(bookingRef);
        }
      }
    }
    return inverted;
  }
}
