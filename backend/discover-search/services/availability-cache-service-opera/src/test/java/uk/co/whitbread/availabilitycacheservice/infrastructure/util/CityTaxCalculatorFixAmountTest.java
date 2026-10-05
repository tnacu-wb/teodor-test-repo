package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CityTaxCalculatorFixAmountTest {

  private final CityTaxCalculatorFixAmount calculator = new CityTaxCalculatorFixAmount();

  private HotelCityTax createHotelCityTax(double amount, double vat) {
    return HotelCityTax.builder()
        .amount(BigDecimal.valueOf(amount))
        .vat(BigDecimal.valueOf(vat))
        .build();
  }

  @Test
  void testCalculateCityTax() {
    HotelCityTax tax = createHotelCityTax(2.0, 20.0); // £2 per room per night, 20% VAT
    BigDecimal result = calculator.calculateCityTax(100.0, 3, 2, tax);
    // (2 + 0.4) * 3 * 2 = 2.4 * 6 = 14.4
    assertThat(result).isEqualByComparingTo("14.4");
  }

  @Test
  void testCalculateTotalWithCityTax() {
    HotelCityTax tax = createHotelCityTax(2.0, 20.0);
    BigDecimal result = calculator.calculateTotalWithCityTax(100.0, 3, 2, tax);
    // base: 100 * 3 * 2 = 600; city tax: 14.4; total: 614.4
    assertThat(result).isEqualByComparingTo("614.40");
  }

  @Test
  void testCalculateCityTax_ZeroVAT() {
    HotelCityTax tax = createHotelCityTax(5.0, 0.0);
    BigDecimal result = calculator.calculateCityTax(80.0, 1, 1, tax);
    // 5 * 1 * 1 = 5
    assertThat(result).isEqualByComparingTo("5.0");
  }

  @Test
  void testCalculateTotalWithCityTax_ZeroVAT() {
    HotelCityTax tax = createHotelCityTax(5.0, 0.0);
    BigDecimal result = calculator.calculateTotalWithCityTax(80.0, 1, 1, tax);
    // base: 80; city tax: 5; total: 85
    assertThat(result).isEqualByComparingTo("85.00");
  }

  @Test
  void testValidateInput_invalidFixedAmount_negative() {
    HotelCityTax tax = createHotelCityTax(-1.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid fixedAmount");
  }

  @Test
  void testValidateInput_invalidFixedAmount_tooLarge() {
    HotelCityTax tax = createHotelCityTax(10001.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid fixedAmount");
  }

  @Test
  void testValidateInput_invalidVat_negative() {
    HotelCityTax tax = createHotelCityTax(2.0, -1.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid VAT percentage");
  }

  @Test
  void testValidateInput_invalidVat_tooLarge() {
    HotelCityTax tax = createHotelCityTax(2.0, 101.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid VAT percentage");
  }

  @Test
  void testValidateInput_invalidNights_tooLow() {
    HotelCityTax tax = createHotelCityTax(2.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 0, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Number of nights out of bounds");
  }

  @Test
  void testValidateInput_invalidNights_tooHigh() {
    HotelCityTax tax = createHotelCityTax(2.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 366, 1, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Number of nights out of bounds");
  }

  @Test
  void testValidateInput_invalidRooms_tooLow() {
    HotelCityTax tax = createHotelCityTax(2.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 0, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Number of rooms out of bounds");
  }

  @Test
  void testValidateInput_invalidRooms_tooHigh() {
    HotelCityTax tax = createHotelCityTax(2.0, 10.0);
    assertThatThrownBy(() -> calculator.calculateCityTax(100.0, 2, 101, tax))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Number of rooms out of bounds");
  }
}
