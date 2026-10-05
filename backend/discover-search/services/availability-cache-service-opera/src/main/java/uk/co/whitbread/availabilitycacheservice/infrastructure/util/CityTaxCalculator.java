package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.math.BigDecimal;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

public interface CityTaxCalculator {

  BigDecimal calculateCityTax(double nightlyAmount, int nights, int rooms, HotelCityTax hotelCityTax);

  BigDecimal calculateTotalWithCityTax(double nightlyAmount, int nights, int rooms,
      HotelCityTax hotelCityTax);
}
