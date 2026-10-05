package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response from the Basket Service for {@code GET /v1/baskets/{basket-reference}}.
 *
 * <p>The Basket Service returns its full {@code BasketDto} here — hotel, items, allowances,
 * payment details and more. This record deliberately declares only the three fields the payment
 * workflow reads, and ignores the rest: binding the whole basket would couple payment
 * orchestration to every field the basket team adds.
 *
 * @param reference        the basket identifier (the Basket Service's {@code reference} field)
 * @param bookingReference the booking reference
 * @param status           the basket lifecycle status, e.g. {@code PAY_PENDING},
 *                         {@code COMPLETED}, {@code FAILED}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BasketResponse(
    String reference,
    String bookingReference,
    String status
) {}
