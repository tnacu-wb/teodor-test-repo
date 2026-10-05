package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static java.util.Optional.ofNullable;
import static uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils.sanitize;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper.mapCalendarBestPricedHotelsToHotels;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldApplyCityTax;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil.shouldCalculateCityTax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.HotelPricePersistencePort;
import uk.co.whitbread.availabilitycacheservice.domain.utils.SanitizingUtils;
import uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.ContentClientLookUpService;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.CalendarBestPriceHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.HotelsCityTaxInfo;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceCalendarRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.hotelprice.HotelPriceRequest;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxFeatureUtil;
import uk.co.whitbread.availabilitycacheservice.infrastructure.util.CityTaxUtil;

@Slf4j
@AllArgsConstructor
public class HotelPricePersistenceAdapter implements HotelPricePersistencePort {

  public static final String INCORRECT_DATE_FORMAT =
      "Format of given date is not correct, please try with yyyy-mm-dd";

  private final HotelJpaRepository hotelJpaRepository;
  private final CityTaxFeatureUtil cityTaxFeatureUtil;
  private final ContentClientLookUpService contentClientLookUpService;

  @Override
  public List<Hotel> getBestPriceForHotels(final HotelPriceRequest hotelPriceRequest) {
    final HotelPriceRequest bestPriceRequest = ofNullable(hotelPriceRequest).orElse(new HotelPriceRequest());

    final LocalDate arrival = ofNullable(bestPriceRequest.getArrival()).map(LocalDate::parse).orElse(null);
    final LocalDate departure = ofNullable(bestPriceRequest.getDeparture()).map(LocalDate::parse).orElse(null);
    final List<String> hotelCodes = bestPriceRequest.getHotelCodes();
    final String roomType = bestPriceRequest.getRoomType();
    try {
      log.info("Requesting best price for hotels by hotelCodes={}, roomType={}, arrival={} and departure={}",
          hotelCodes == null ? null :
              hotelCodes.stream()
                  .map(SanitizingUtils::sanitize)
                  .toList(),
          sanitize(roomType), sanitize(arrival), sanitize(departure));
      final List<BestPricedHotel> bestPricedHotels = hotelJpaRepository.findBestPriceForHotel(hotelCodes, arrival,
          departure, roomType);
      applyCityTaxIfNeeded(hotelCodes, bestPricedHotels, arrival);
      return mapBestPricedHotelToHotel(roomType, bestPricedHotels);
    } catch (Exception ex) {
      log.error("Error while trying to fetch best prices for hotelCodes={}, roomType={}, arrival={} and departure={}",
          hotelCodes == null ? null :
              hotelCodes.stream()
                  .map(SanitizingUtils::sanitize)
                  .toList(),
          sanitize(roomType), sanitize(arrival), sanitize(departure), ex);
      throw ex;
    }

  }

  @Override
  public List<Hotel> getBestPricesForGivenHotelCode(final String hotelCode,
      final HotelPriceCalendarRequest hotelPriceCalendarRequest) {
    final HotelPriceCalendarRequest priceCalendarRequest = ofNullable(hotelPriceCalendarRequest)
        .orElse(new HotelPriceCalendarRequest());

    final LocalDate arrival = ofNullable(priceCalendarRequest.getArrival()).map(LocalDate::parse)
        .orElseThrow(getResponseStatusExceptionSupplier());
    final LocalDate departure = ofNullable(priceCalendarRequest.getDeparture()).map(LocalDate::parse)
        .orElseThrow(getResponseStatusExceptionSupplier());

    final String roomType = priceCalendarRequest.getRoomType();
    final String sanitizedRoomType = sanitize(roomType);
    final String sanitizedHotelCode = sanitize(hotelCode);
    try {
      log.info("Requesting best price for hotel by hotelCode={}, roomType={}, arrival={} and departure={}",
          sanitizedHotelCode, sanitizedRoomType, arrival, departure);

      final List<CalendarBestPriceHotel> bestPricedHotels = hotelJpaRepository
          .findBestPricesForGivenHotelCode(hotelCode, arrival, departure, roomType);
      log.info("bestPricedHotels: {}", bestPricedHotels);

      bestPricedHotels.forEach(hotel ->
          applyCityTaxIfNeeded(hotelPriceCalendarRequest.getHotelCityTaxInfo(), hotel));

      return mapCalendarBestPricedHotelsToHotels(roomType, bestPricedHotels);
    } catch (Exception ex) {
      log.error("Error while trying to fetch best prices for hotelCodes={}, roomType={}, arrival={} and departure={}",
          sanitizedHotelCode, sanitizedRoomType, arrival, departure, ex);
      throw ex;
    }
  }

  private void applyCityTaxIfNeeded(HotelsCityTaxInfo hotelsCityTaxInfo, CalendarBestPriceHotel hotel) {
    if (shouldApplyCityTax(cityTaxFeatureUtil.isFeatureEnabled(), hotel.getHotelCode(), hotelsCityTaxInfo,
        LocalDate.parse(hotel.getDate()))) {
      if (shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), hotel.getBestPriceWithCityTax())) {
        var hotelCityTaxInfo = hotelsCityTaxInfo.getHotelsCityTaxes().get(hotel.getHotelCode());
        BigDecimal calculatedAmountWithCityTax =
            CityTaxUtil.getAmountWithCityTax(hotelCityTaxInfo, hotel.getHotelCode(),
                hotel.getBestPrice(), 1, 1);
        hotel.setBestPrice(calculatedAmountWithCityTax);
      } else {
        hotel.setBestPrice(hotel.getBestPriceWithCityTax());
      }
    }
  }

  private void applyCityTaxIfNeeded(List<String> hotelCodes, List<BestPricedHotel> bestPricedHotels,
      LocalDate arrival) {
    if (Objects.isNull(bestPricedHotels) || bestPricedHotels.isEmpty()) {
      return;
    }
    if (cityTaxFeatureUtil.isFeatureEnabled()) {
      var hotelsCityTaxInfo = contentClientLookUpService.getHotelsCityTaxInfo("gb", "en", hotelCodes);
      bestPricedHotels.forEach(hotel -> {
        if (shouldApplyCityTax(true, hotel.getHotelCode(), hotelsCityTaxInfo, arrival)) {
          var updatedPrice = calculateUpdatedPrice(hotelsCityTaxInfo, hotel);
          hotel.setBestPrice(updatedPrice);
        }
      });
    }
  }

  private BigDecimal calculateUpdatedPrice(HotelsCityTaxInfo hotelsCityTaxInfo, BestPricedHotel hotel) {
    if (shouldCalculateCityTax(cityTaxFeatureUtil.isFallbackEnabled(), hotel.getPriceWithCityTax())) {
      var hotelCityTaxInfo = hotelsCityTaxInfo.getHotelsCityTaxes().get(hotel.getHotelCode());
      return CityTaxUtil.getAmountWithCityTax(hotelCityTaxInfo, hotel.getHotelCode(), hotel.getBestPrice(), 1, 1);
    }
    return hotel.getPriceWithCityTax();
  }

  private Supplier<ResponseStatusException> getResponseStatusExceptionSupplier() {
    return () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, INCORRECT_DATE_FORMAT);
  }

}
