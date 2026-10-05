package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Individual reservation within the basket response.
 *
 * @param reservationId     unique identifier for this reservation
 * @param rateInfo          rate information including cost summary
 * @param reservationStatus current status of the reservation (e.g. RESERVED)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservationByIdDto(
    String reservationId,
    RateInfoDto rateInfo,
    String reservationStatus
) {}
