package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

import static org.assertj.core.api.Assertions.assertThat;

class CityTaxCalculatorFactoryTest {

  @Test
  void testGetCityTaxCalculator_EdinburghHotel() {
    CityTaxCalculator calculator = CityTaxCalculatorFactory.getCityTaxCalculator(
        HotelCityTax.builder().percentage(BigDecimal.valueOf(5.00)).build());
    assertThat(calculator).isInstanceOf(CityTaxCalculatorPercentage.class);
  }

  @Test
  void testGetCityTaxCalculator_NonEdinburghHotel() {
    CityTaxCalculator calculator =
        CityTaxCalculatorFactory.getCityTaxCalculator(HotelCityTax.builder().amount(BigDecimal.valueOf(2.00)).build());
    assertThat(calculator).isInstanceOf(CityTaxCalculatorFixAmount.class);
  }
}
