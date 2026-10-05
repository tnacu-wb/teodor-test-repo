package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CityTaxCalculatorPercentageTest {

  private final CityTaxCalculatorPercentage calculator = new CityTaxCalculatorPercentage();

  private HotelCityTax createHotelCityTax(int maxNights, double percentage) {
    return HotelCityTax.builder()
        .maxNights(maxNights)
        .percentage(BigDecimal.valueOf(percentage))
        .build();
  }

  @Test
  void testCalculateCityTax_NormalCase() {
    HotelCityTax tax = createHotelCityTax(5, 10.0);
    BigDecimal result = calculator.calculateCityTax(100.0, 3, 2, tax);
    assertThat(result).isEqualByComparingTo("60.00");
  }

  @Test
  void testCalculateCityTax_MaxNightsLimit() {
    HotelCityTax tax = createHotelCityTax(2, 15.0);
    BigDecimal result = calculator.calculateCityTax(80.0, 4, 1, tax);
    assertThat(result).isEqualByComparingTo("24.00");
  }

  @Test
  void testCalculateCityTax_ZeroPercentage() {
    HotelCityTax tax = createHotelCityTax(10, 0.0);
    BigDecimal result = calculator.calculateCityTax(50.0, 2, 2, tax);
    assertThat(result).isEqualByComparingTo("0.00");
  }

  @Test
  void testCalculateTotalWithCityTax_NormalCase() {
    HotelCityTax tax = createHotelCityTax(3, 20.0);
    BigDecimal result = calculator.calculateTotalWithCityTax(120.0, 2, 2, tax);
    assertThat(result).isEqualByComparingTo("576.00");
  }

  @Test
  void testCalculateTotalWithCityTax_MaxNightsLimit() {
    HotelCityTax tax = createHotelCityTax(1, 5.0);
    BigDecimal result = calculator.calculateTotalWithCityTax(200.0, 3, 1, tax);
    assertThat(result).isEqualByComparingTo("610.00");
  }
}
