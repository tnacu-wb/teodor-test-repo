package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice;

import static java.util.Optional.ofNullable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.CalendarBestPriceHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice;

@Slf4j
@Component
public final class BestPricedHotelToHotelMapper {

  private BestPricedHotelToHotelMapper() {
  }

  public static List<Hotel> mapBestPricedHotelToHotel(final String roomType,
      final List<BestPricedHotel> bestPricedHotels) {
    log.trace("Mapping best price hotel to hotel");
    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(bestHotelPrice -> {
          Hotel hotel = new Hotel();
          hotel.setHotelCode(bestHotelPrice.getHotelCode());
          hotel.setBestRoomPrice(new BestRoomPrice(roomType,
              new Price(bestHotelPrice.getBestPrice(), bestHotelPrice.getCurrency())));
          return hotel;
        }).toList();
  }

  public static List<Hotel> mapCalendarBestPricedHotelsToHotels(final String roomType,
      final List<CalendarBestPriceHotel> bestPricedHotels) {
    log.trace("Mapping calendar best price hotels to hotels");
    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(bestHotelPrice -> {
          Hotel hotel = new Hotel();
          hotel.setDate(bestHotelPrice.getDate());
          hotel.setHotelCode(bestHotelPrice.getHotelCode());
          hotel.setBestRoomPrice(new BestRoomPrice(roomType,
              new Price(bestHotelPrice.getBestPrice(), bestHotelPrice.getCurrency())));
          return hotel;
        }).toList();
  }

  public static List<Hotel> mapLocationBestPricedHotelsToHotels(final List<LocationBestPrice> bestPricedHotels) {
    log.trace("Mapping location best price hotels to hotels");
    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(bestHotelPrice -> {
          Hotel hotel = new Hotel();
          hotel.setPlaceId(bestHotelPrice.getPlaceId());
          hotel.setHotelCode(bestHotelPrice.getHotelCode());
          hotel.setBestRoomPrice(new BestRoomPrice("",
              new Price(bestHotelPrice.getPrice(), bestHotelPrice.getCurrency())));
          return hotel;
        }).toList();
  }

}
