package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

/**
 * Top-level response from the Hotel Reservation Entity Service for
 * {@code GET /v1/reservations/basket/{basketReference}}.
 *
 * @param reservationByIdList list of reservations in the basket
 * @param bookingReference    the booking reference (used as refno for Datatrans)
 * @param basketReference     the basket/reservation identifier
 * @param hotelId             the hotel identifier
 * @param currencyCode        ISO 4217 currency code (e.g. GBP, EUR)
 * @param totalCost           total cost of the basket
 * @param basketStatus        current status of the basket (e.g. RESERVED)
 * @param paymentOption       payment option (e.g. FULL_PAYMENT)
 * @param channel             the booking channel (e.g. PI, BB, APPS_IOS, APPS_ANDROID)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationByBasketResponse(
    List<ReservationByIdDto> reservationByIdList,
    String bookingReference,
    String basketReference,
    String hotelId,
    String currencyCode,
    BigDecimal totalCost,
    String basketStatus,
    String paymentOption,
    String channel
) {}
