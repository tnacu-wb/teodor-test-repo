package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice;

import static java.util.Optional.ofNullable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.BestPricedHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendar;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPriceResponse;

@Slf4j
@Component
public final class BestPricedHotelDtoMapper {

  private BestPricedHotelDtoMapper() {
  }

  public static List<BestPricedHotelDto> mapHotelToBestPricedHotelDto(final List<Hotel> bestPricedHotels) {
    log.trace("Mapping hotel to bestPricedHotelDto");

    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(hotel -> {
          BestPricedHotelDto bestPricedHotelDto = new BestPricedHotelDto();
          bestPricedHotelDto.setHotelCode(hotel.getHotelCode());
          bestPricedHotelDto.setHotelBrand("PI");
          BestRoomPrice bestRoomPrice = ofNullable(hotel.getBestRoomPrice()).orElse(
              new BestRoomPrice(null, new Price()));
          bestPricedHotelDto.setBestPrice(getBestPrice(bestRoomPrice));
          return bestPricedHotelDto;
        }).collect(Collectors.toList());
  }

  private static PriceDto getBestPrice(BestRoomPrice bestRoomPrice) {
    return new PriceDto(bestRoomPrice.getPrice().getAmount(), bestRoomPrice.getPrice().getCurrency());
  }

  public static List<HotelPriceCalendar> mapHotelToHotelPriceCalendar(final List<Hotel> bestPricedHotels) {
    log.trace("Mapping hotel to HotelPriceCalendarResponse");

    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(BestPricedHotelDtoMapper::buildHotelPriceCalendarResponse).collect(Collectors.toList());
  }

  private static HotelPriceCalendar buildHotelPriceCalendarResponse(final Hotel hotel) {

    final BestRoomPrice bestRoomPrice =
        ofNullable(hotel.getBestRoomPrice()).orElse(new BestRoomPrice(null, new Price()));
    final String amount = bestRoomPrice.getPrice().getAmount().toString();

    return HotelPriceCalendar.builder()
        .date(hotel.getDate())
        .price(new BigDecimal(amount))
        .build();
  }

  //location start

  public static LocationPriceResponse buildLocationPriceResponse(final List<Hotel> bestPricedHotels) {

    List<LocationPrice> locationPriceList = mapHotelToHotelPriceLocation(bestPricedHotels);

    return LocationPriceResponse
        .builder()
        .bestPricedHotels(locationPriceList)
        .build();
  }

  private static List<LocationPrice> mapHotelToHotelPriceLocation(final List<Hotel> bestPricedHotels) {
    log.trace("Mapping hotel to HotelPriceCalendarResponse");

    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(BestPricedHotelDtoMapper::buildLocationPrice).collect(Collectors.toList());
  }

  private static LocationPrice buildLocationPrice(final Hotel hotel) {

    final BestRoomPrice bestRoomPrice =
        ofNullable(hotel.getBestRoomPrice()).orElse(new BestRoomPrice(null, new Price()));

    return LocationPrice.builder()
        .placeId(hotel.getPlaceId())
        .hotelCode(hotel.getHotelCode())
        .bestPrice(getPriceDto(bestRoomPrice))
        .build();
  }

  private static PriceDto getPriceDto(final BestRoomPrice bestRoomPrice) {
    return PriceDto.builder()
        .amount(bestRoomPrice.getPrice().getAmount())
        .currency(bestRoomPrice.getPrice().getCurrency())
        .build();
  }

  //location end

}
