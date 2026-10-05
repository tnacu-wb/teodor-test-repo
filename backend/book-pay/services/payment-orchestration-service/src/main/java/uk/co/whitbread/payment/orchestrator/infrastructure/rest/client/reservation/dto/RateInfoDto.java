package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Rate information wrapper containing the summary.
 *
 * @param summary the rate summary with cost breakdown
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateInfoDto(
    RateInfoSummaryDto summary
) {}
