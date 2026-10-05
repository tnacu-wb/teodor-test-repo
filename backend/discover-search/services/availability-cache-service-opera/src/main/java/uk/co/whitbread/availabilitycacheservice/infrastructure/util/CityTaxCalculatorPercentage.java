package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

public class CityTaxCalculatorPercentage implements CityTaxCalculator {

  @Override
  public BigDecimal calculateCityTax(double nightlyAmount, int nights, int rooms, HotelCityTax hotelCityTax) {
    var maxNights = Optional.ofNullable(hotelCityTax.getMaxNights()).orElse(365);
    var percentage = hotelCityTax.getPercentage().doubleValue();
    int taxableNights = Math.min(nights, maxNights);
    double taxableBase = nightlyAmount * taxableNights * rooms;
    return BigDecimal.valueOf(taxableBase * (percentage / 100.0))
        .setScale(2, RoundingMode.HALF_UP);
  }

  @Override
  public BigDecimal calculateTotalWithCityTax(double nightlyAmount, int nights, int rooms,
      HotelCityTax hotelCityTax) {
    BigDecimal cityTax = calculateCityTax(nightlyAmount, nights, rooms, hotelCityTax);
    BigDecimal totalBase = BigDecimal.valueOf(nightlyAmount * nights * rooms);
    return cityTax.add(totalBase).setScale(2, RoundingMode.HALF_UP);
  }
}
