package uk.co.whitbread.payment.orchestrator.domain.logic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.whitbread.payment.orchestrator.domain.exceptions.InvalidPaymentAmountException;

class AmountCalculatorTest {

  private AmountCalculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new AmountCalculator();
  }

  @Nested
  class SupportedCurrencies {

    @Test
    void gbpConversion_typicalAmount() {
      long result = calculator.toMinorUnits(new BigDecimal("86.00"), "GBP");
      assertThat(result).isEqualTo(8600L);
    }

    @Test
    void gbpConversion_zeroAmount() {
      long result = calculator.toMinorUnits(BigDecimal.ZERO, "GBP");
      assertThat(result).isZero();
    }

    @Test
    void eurConversion_typicalAmount() {
      long result = calculator.toMinorUnits(new BigDecimal("49.99"), "EUR");
      assertThat(result).isEqualTo(4999L);
    }

    @Test
    void caseInsensitivity_lowercaseCurrencyCode() {
      long result = calculator.toMinorUnits(new BigDecimal("86.00"), "gbp");
      assertThat(result).isEqualTo(8600L);
    }

    @Test
    void largeAmount_convertsCorrectly() {
      long result = calculator.toMinorUnits(new BigDecimal("999999.99"), "GBP");
      assertThat(result).isEqualTo(99999999L);
    }

    @Test
    void wholeNumberWithoutDecimals_convertsCorrectly() {
      long result = calculator.toMinorUnits(new BigDecimal("120"), "EUR");
      assertThat(result).isEqualTo(12000L);
    }
  }

  @Nested
  class UnsupportedCurrencies {

    /**
     * Unsupported currencies must be rejected, never guessed: a 0-decimal currency like JPY
     * given the old exponent-2 default would be overcharged 100x, and a 3-decimal currency
     * like BHD undercharged 10x.
     */
    @ParameterizedTest
    @ValueSource(strings = {"USD", "CHF", "JPY", "KRW", "BHD", "SEK", "HUF"})
    void unsupportedCurrency_isRejected(String currency) {
      assertThatThrownBy(() -> calculator.toMinorUnits(new BigDecimal("10.00"), currency))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining(currency);
    }

    @Test
    void nullCurrency_isRejected() {
      assertThatThrownBy(() -> calculator.toMinorUnits(new BigDecimal("10.00"), null))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining("Currency code is missing");
    }

    @Test
    void blankCurrency_isRejected() {
      assertThatThrownBy(() -> calculator.toMinorUnits(new BigDecimal("10.00"), "  "))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining("Currency code is missing");
    }
  }

  @Nested
  class InvalidAmounts {

    @Test
    void nullAmount_isRejected() {
      assertThatThrownBy(() -> calculator.toMinorUnits(null, "GBP"))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining("Amount is missing");
    }

    @Test
    void negativeAmount_isRejected() {
      assertThatThrownBy(() -> calculator.toMinorUnits(new BigDecimal("-1.00"), "GBP"))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining("negative");
    }

    @Test
    void fractionalPennies_areRejectedNotRounded() {
      assertThatThrownBy(() -> calculator.toMinorUnits(new BigDecimal("10.999"), "GBP"))
          .isInstanceOf(InvalidPaymentAmountException.class)
          .hasMessageContaining("precision");
    }

    @Test
    void amountOverflowingLong_isRejected() {
      BigDecimal tooLarge = new BigDecimal(Long.MAX_VALUE).add(BigDecimal.ONE);
      assertThatThrownBy(() -> calculator.toMinorUnits(tooLarge, "GBP"))
          .isInstanceOf(InvalidPaymentAmountException.class);
    }
  }
}
