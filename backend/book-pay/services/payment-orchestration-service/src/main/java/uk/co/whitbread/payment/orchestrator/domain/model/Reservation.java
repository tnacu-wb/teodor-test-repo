package uk.co.whitbread.payment.orchestrator.domain.model;

import java.math.BigDecimal;

/**
 * Reservation data retrieved from the Hotel Reservation Entity Service.
 *
 * @param basketId         the basket identifier this reservation was looked up by (echoed
 *                         from the request — NOT the reservation service's own reservationId)
 * @param hotelId          the hotel identifier
 * @param totalCostOfStay  the total cost of stay in major currency units (payment amount)
 * @param currencyCode     ISO 4217 currency code (e.g. GBP, EUR, USD)
 * @param bookingReference the booking reference from the reservation service
 * @param refno            reference number for Datatrans (set to bookingReference)
 * @param channel          the booking channel (e.g. PI, BB, APPS_IOS, APPS_ANDROID)
 */
public record Reservation(
    String basketId,
    String hotelId,
    BigDecimal totalCostOfStay,
    String currencyCode,
    String bookingReference,
    String refno,
    String channel
) {}
