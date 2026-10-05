package uk.co.whitbread.payment.orchestrator.infrastructure.rest.client.reservation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * Rate summary containing cost breakdown for a reservation.
 *
 * @param totalCostOfStay      total cost of the stay in major currency units (payment amount)
 * @param currencyCode         ISO 4217 currency code (e.g. GBP, EUR)
 * @param gross                gross amount
 * @param net                  net amount (excluding tax)
 * @param deposit              deposit amount
 * @param outStandingCostOfStay outstanding cost of stay
 * @param guestPay             amount the guest pays
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RateInfoSummaryDto(
    BigDecimal totalCostOfStay,
    String currencyCode,
    BigDecimal gross,
    BigDecimal net,
    BigDecimal deposit,
    BigDecimal outStandingCostOfStay,
    BigDecimal guestPay
) {}
