package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;

class CityTaxUtilTest {

  @Test
  void testShouldApplyCityTax_FeatureDisabled() {
    String hotelCode = "HOTEL1";
    LocalDate arrivalDate = LocalDate.now();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of(hotelCode))
        .hotelsCityTaxes(Map.of(hotelCode, HotelCityTax.builder().build()))
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(false, hotelCode, info, arrivalDate);
    assertThat(result).isFalse();
  }

  @Test
  void testShouldApplyCityTax_NotConfigured() {
    String hotelCode = "HOTEL1";
    LocalDate arrivalDate = LocalDate.now();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of())
        .hotelsCityTaxes(Map.of())
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(true, hotelCode, info, arrivalDate);
    assertThat(result).isFalse();
  }

  @Test
  void testShouldApplyCityTax_Effective() {
    String hotelCode = "HOTEL1";
    LocalDate now = LocalDate.now();
    HotelCityTax tax = HotelCityTax.builder()
        .effectiveFrom(now.minusDays(2).toString())
        .bookingDateFrom(now.minusDays(1).toString())
        .build();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of(hotelCode))
        .hotelsCityTaxes(Map.of(hotelCode, tax))
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(true, hotelCode, info, now);
    assertThat(result).isTrue();
  }

  @Test
  void testShouldApplyCityTaxOnBookingDateFrom_Effective() {
    String hotelCode = "HOTEL1";
    LocalDate now = LocalDate.now();
    LocalDate effectiveFrom = now.minusDays(2);
    LocalDate arrivalDate = now.plusDays(3);
    LocalDate bookingDateFrom = now.plusDays(3);

    HotelCityTax tax = HotelCityTax.builder()
        .effectiveFrom(effectiveFrom.toString())
        .bookingDateFrom(bookingDateFrom.toString())
        .build();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of(hotelCode))
        .hotelsCityTaxes(Map.of(hotelCode, tax))
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(true, hotelCode, info, arrivalDate);
    assertThat(result).isTrue();
  }

  @Test
  void testShouldApplyCityTaxBeforeBookingDateFrom_NotEffective() {
    String hotelCode = "HOTEL1";
    LocalDate now = LocalDate.now();
    LocalDate effectiveFrom = now.minusDays(2);
    LocalDate arrivalDate = now.plusDays(2);
    LocalDate bookingDateFrom = now.plusDays(3);

    HotelCityTax tax = HotelCityTax.builder()
        .effectiveFrom(effectiveFrom.toString())
        .bookingDateFrom(bookingDateFrom.toString())
        .build();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of(hotelCode))
        .hotelsCityTaxes(Map.of(hotelCode, tax))
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(true, hotelCode, info, arrivalDate);
    assertThat(result).isFalse();
  }

  @Test
  void testShouldApplyCityTax_NotEffective() {
    String hotelCode = "HOTEL1";
    LocalDate now = LocalDate.now();
    HotelCityTax tax = HotelCityTax.builder()
        .effectiveFrom(now.plusDays(1).toString())
        .bookingDateFrom(now.plusDays(2).toString())
        .build();
    HotelsCityTaxInfo info = HotelsCityTaxInfo.builder()
        .hotelsWithCityTax(List.of(hotelCode))
        .hotelsCityTaxes(Map.of(hotelCode, tax))
        .build();
    boolean result = CityTaxUtil.shouldApplyCityTax(true, hotelCode, info, now);
    assertThat(result).isFalse();
  }

  @Test
  void testShouldCalculateCityTax_Null() {
    boolean result = CityTaxUtil.shouldCalculateCityTax(false, null);
    assertThat(result).isTrue();
  }

  @Test
  void testShouldCalculateCityTax_Negative() {
    boolean result = CityTaxUtil.shouldCalculateCityTax(false, BigDecimal.valueOf(-1));
    assertThat(result).isTrue();
  }

  @Test
  void testGetAmountWithCityTax_PercentageCalculator() {
    HotelCityTax tax = HotelCityTax.builder()
        .maxNights(5)
        .percentage(BigDecimal.valueOf(10.0))
        .build();

    BigDecimal result = CityTaxUtil.getAmountWithCityTax(
        tax, "EDIPRI", BigDecimal.valueOf(100.0), 3, 2);

    // taxableBase = 100 * 3 * 2 = 600, tax = 600 * 0.10 = 60, total = 660
    assertThat(result).isEqualByComparingTo("660.00");
  }

  @Test
  void testGetAmountWithCityTax_FixAmountCalculator() {
    HotelCityTax tax = HotelCityTax.builder()
        .amount(BigDecimal.valueOf(2.0))
        .vat(BigDecimal.valueOf(20.0))
        .build();

    BigDecimal result = CityTaxUtil.getAmountWithCityTax(
        tax, "MANOXF", BigDecimal.valueOf(100.0), 3, 2);

    // city tax = (2 + 0.4) * 3 * 2 = 2.4 * 6 = 14.4, total = 600 + 14.4 = 614.4
    assertThat(result).isEqualByComparingTo("614.40");
  }

  @Test
  void testShouldCalculateCityTax_False() {

    boolean result = CityTaxUtil.shouldCalculateCityTax(false, BigDecimal.valueOf(110.0));
    assertThat(result).isFalse();
  }

  @Test
  void testShouldCalculateCityTax_True() {

    boolean result = CityTaxUtil.shouldCalculateCityTax(false, BigDecimal.ZERO);
    assertThat(result).isTrue();
  }

  @Test
  void testShouldCalculateCityTax_True_FallbackEnabled() {

    boolean result = CityTaxUtil.shouldCalculateCityTax(true, BigDecimal.valueOf(110.0));
    assertThat(result).isTrue();
  }
}
