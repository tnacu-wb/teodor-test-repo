package uk.co.whitbread.availabilitycacheservice.infrastructure.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.whitbread.availabilitycacheservice.domain.model.content.HotelCityTax;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;

@Slf4j
public class CityTaxUtil {

  private CityTaxUtil() {
  }

  public static boolean shouldApplyCityTax(boolean isFeatureEnabled, String hotelCode,
      HotelsCityTaxInfo hotelsCityTaxInfo, LocalDate availabilityDate) {
    return shouldApplyCityTax(isFeatureEnabled, hotelCode, hotelsCityTaxInfo, availabilityDate, null);
  }

  public static boolean shouldApplyCityTax(boolean isFeatureEnabled, String hotelCode,
      HotelsCityTaxInfo hotelsCityTaxInfo, LocalDate availabilityDate, LocalDate arrivalDate) {
    return isFeatureEnabled && shouldApplyCityTax(hotelCode, hotelsCityTaxInfo, availabilityDate, arrivalDate);
  }

  private static boolean shouldApplyCityTax(String hotelCode, HotelsCityTaxInfo hotelCityTaxInfo,
      LocalDate availabilityDate, LocalDate arrivalDate) {
    boolean isCityTaxConfigured = hotelCityTaxInfo != null && hotelCityTaxInfo.getHotelsWithCityTax() != null
        && hotelCityTaxInfo.getHotelsCityTaxes().containsKey(hotelCode);

    if (!isCityTaxConfigured) {
      return false;
    }

    HotelCityTax hotelCityTax = hotelCityTaxInfo.getHotelsCityTaxes().get(hotelCode);

    return isCityTaxEffective(arrivalDate == null ? availabilityDate : arrivalDate, hotelCityTax)
        && isMaxNightsNotExceeded(availabilityDate, arrivalDate, hotelCityTax);
  }

  // City tax is effective if the current date is after the effective from date
  // and the availabilityDate is after the booking date from (if specified)
  private static boolean isCityTaxEffective(LocalDate availabilityDate, HotelCityTax hotelCityTax) {
    Optional<LocalDate> effectiveFrom = StringUtils.isNotBlank(hotelCityTax.getEffectiveFrom()) ? Optional.of(
        LocalDate.parse(hotelCityTax.getEffectiveFrom())) : Optional.empty();
    Optional<LocalDate> bookingDateFrom = StringUtils.isNotBlank(hotelCityTax.getBookingDateFrom()) ? Optional.of(
        LocalDate.parse(hotelCityTax.getBookingDateFrom())) : Optional.empty();

    log.debug("Parsed effectiveFrom={}, bookingDateFrom={}", effectiveFrom, bookingDateFrom);

    boolean isEffective = effectiveFrom.isPresent() && !LocalDate.now().isBefore(effectiveFrom.get())
        && (bookingDateFrom.isEmpty() || !bookingDateFrom.get().isAfter(availabilityDate));

    log.debug("City tax effective result: {}", isEffective);
    return isEffective;
  }

  private static boolean isMaxNightsNotExceeded(LocalDate availabilityDate, LocalDate arrivalDate,
      HotelCityTax hotelCityTax) {
    return Optional.ofNullable(hotelCityTax.getMaxNights())
        .map(maxNights -> maxNights <= 0 || arrivalDate == null
            || availabilityDate.isBefore(arrivalDate.plusDays(maxNights)))
        .orElse(true);
  }

  public static BigDecimal getAmountWithCityTax(HotelCityTax hotelCityTax, String hotelCode, BigDecimal originalAmount,
      int nights, int rooms) {
    var cityTaxCalculator = CityTaxCalculatorFactory.getCityTaxCalculator(hotelCityTax);
    var finalAmount = cityTaxCalculator.calculateTotalWithCityTax(originalAmount.doubleValue(),
        nights, rooms, hotelCityTax);

    log.debug("Applied city tax to hotel {}: original={}, final={}", hotelCode, originalAmount, finalAmount);
    return finalAmount;
  }

  public static boolean shouldCalculateCityTax(boolean isFallbackEnabled, BigDecimal dbPriceWithCityTax) {
    return isFallbackEnabled || dbPriceWithCityTax == null || dbPriceWithCityTax.compareTo(BigDecimal.ZERO) <= 0;
  }
}
