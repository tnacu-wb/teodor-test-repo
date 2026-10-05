package uk.co.whitbread.payment.orchestrator.domain.logic;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.InvalidPaymentAmountException;

/**
 * Converts monetary amounts from major units to minor units based on currency.
 *
 * <p>Only the currencies the business actually takes payments in are supported (GBP and EUR,
 * both exponent 2). Any other currency is rejected rather than guessed: a wrong exponent
 * silently charges the wrong amount (a 0-decimal currency like JPY would be overcharged 100x),
 * so an unknown currency must fail loudly before any gateway call.
 *
 * <p>Amounts with more precision than the currency's minor unit (e.g. 86.005 GBP) are rejected
 * rather than rounded — sub-minor-unit precision means upstream reservation data is broken,
 * and rounding money is a business decision this class must not make silently.
 *
 * <p>This class has no Spring annotations; it is wired via
 * {@link uk.co.whitbread.payment.orchestrator.infrastructure.config.InfrastructureBeanConfig}.
 */
public class AmountCalculator {

  private static final Map<String, Integer> SUPPORTED_CURRENCY_EXPONENTS = Map.of(
      "GBP", 2,
      "EUR", 2
  );

  /**
   * Converts a monetary amount from major units to minor units for the given currency.
   *
   * @param amount       the amount in major units (e.g. 86.00 for GBP)
   * @param currencyCode ISO 4217 currency code; only GBP and EUR are supported
   * @return the amount in minor units (e.g. 8600 for 86.00 GBP)
   * @throws InvalidPaymentAmountException if the currency is missing or unsupported, the amount
   *                                       is missing or negative, or the amount has more
   *                                       precision than the currency's minor unit allows
   */
  public long toMinorUnits(BigDecimal amount, String currencyCode) {
    if (currencyCode == null || currencyCode.isBlank()) {
      throw new InvalidPaymentAmountException("Currency code is missing on the reservation");
    }
    Integer exponent = SUPPORTED_CURRENCY_EXPONENTS.get(currencyCode.toUpperCase(Locale.ROOT));
    if (exponent == null) {
      throw new InvalidPaymentAmountException(
          "Unsupported currency for payment: " + currencyCode);
    }
    if (amount == null) {
      throw new InvalidPaymentAmountException("Amount is missing on the reservation");
    }
    if (amount.signum() < 0) {
      throw new InvalidPaymentAmountException("Amount must not be negative: " + amount);
    }
    try {
      return amount.movePointRight(exponent).longValueExact();
    } catch (ArithmeticException e) {
      throw new InvalidPaymentAmountException(
          "Amount " + amount + " " + currencyCode
              + " has more precision than the currency's minor unit allows", e);
    }
  }
}
