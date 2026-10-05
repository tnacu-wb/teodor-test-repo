package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

@Component
public class CityTaxCalculatorFixAmount implements CityTaxCalculator {

  private static final double MAX_FIXED_AMOUNT = 10000.0;
  private static final double MAX_VAT_PERCENT = 100.0;
  private static final int MAX_NIGHTS = 365;
  private static final int MAX_ROOMS = 100;

  @Override
  public BigDecimal calculateCityTax(double nightlyAmount, int nights, int rooms, HotelCityTax hotelCityTax) {
    var fixedAmount = Optional.ofNullable(hotelCityTax.getAmount()).orElse(BigDecimal.ZERO).doubleValue();
    var vat = Optional.ofNullable(hotelCityTax.getVat()).orElse(BigDecimal.ZERO).doubleValue();
    validateInput(nights, rooms, fixedAmount, vat);
    return BigDecimal.valueOf((fixedAmount + (fixedAmount * vat / 100)) * nights * rooms)
        .setScale(2, RoundingMode.HALF_EVEN);
  }

  @Override
  public BigDecimal calculateTotalWithCityTax(double nightlyAmount, int nights, int rooms,
      HotelCityTax hotelCityTax) {
    BigDecimal cityTax = calculateCityTax(nightlyAmount, nights, rooms, hotelCityTax);
    BigDecimal totalBase = BigDecimal.valueOf(nightlyAmount * nights * rooms);
    return cityTax.add(totalBase).setScale(2, RoundingMode.HALF_EVEN);
  }

  private void validateInput(int nights, int rooms, double fixedAmount, double vat) {
    if (fixedAmount < 0.0 || fixedAmount > MAX_FIXED_AMOUNT) {
      throw new IllegalArgumentException("Invalid fixedAmount for city tax: " + fixedAmount);
    }
    if (vat < 0.0 || vat > MAX_VAT_PERCENT) {
      throw new IllegalArgumentException("Invalid VAT percentage: " + vat);
    }
    if (nights < 1 || nights > MAX_NIGHTS) {
      throw new IllegalArgumentException("Number of nights out of bounds: " + nights);
    }
    if (rooms < 1 || rooms > MAX_ROOMS) {
      throw new IllegalArgumentException("Number of rooms out of bounds: " + rooms);
    }
  }
}
