package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;

public class CityTaxCalculatorFactory {

  private CityTaxCalculatorFactory() {
    // private constructor to prevent instantiation
  }

  public static CityTaxCalculator getCityTaxCalculator(HotelCityTax hotelCityTax) {

    if (isTaxPercentage(hotelCityTax)) {
      return new CityTaxCalculatorPercentage();
    } else {
      return new CityTaxCalculatorFixAmount();
    }
  }

  private static boolean isTaxPercentage(HotelCityTax hotelCityTax) {
    return hotelCityTax.getPercentage() != null && hotelCityTax.getPercentage().doubleValue() > 0;
  }
}
