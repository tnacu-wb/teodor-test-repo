package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice;

import static java.util.Optional.ofNullable;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.buildLocationPriceResponse;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.mapHotelToBestPricedHotelDto;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelDtoMapper.mapHotelToHotelPriceCalendar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.BestPricedHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendar;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.LocationPriceResponse;


class BestPricedHotelDtoMapperTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();

  @Test
  void mapBestPricedHotelToHotelTest() {
    Hotel hotel1 = Hotel.builder().hotelCode("PLYPTI").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(50.25), "G"))).build();
    Hotel hotel2 = Hotel.builder().hotelCode("PLYLOC").hotelBrand("PI").
        bestRoomPrice(null).build();

    List<Hotel> hotels = Arrays.asList(hotel1, hotel2);
    List<BestPricedHotelDto> bestPricedHotelDtos = mapHotelToBestPricedHotelDto(hotels);

    BestPricedHotelDto bestPricedHotelDto1 = BestPricedHotelDto.builder()
        .hotelCode("PLYPTI")
        .hotelBrand("PI")
        .bestPrice(new PriceDto(new BigDecimal("50.25"), "G"))
        .build();

    BestPricedHotelDto bestPricedHotelDto2 = BestPricedHotelDto.builder()
        .hotelCode("PLYLOC")
        .hotelBrand("PI")
        .bestPrice(new PriceDto())
        .build();

    assertThat(bestPricedHotelDtos).isEqualToComparingFieldByFieldRecursively(
        Arrays.asList(bestPricedHotelDto1, bestPricedHotelDto2));
  }

  @Test
  void mapBestPricedHotelToHotelEmptyTest() {
    List<BestPricedHotelDto> bestPricedHotelDtos = mapHotelToBestPricedHotelDto(Collections.emptyList());
    Assertions.assertThat(bestPricedHotelDtos).isEmpty();
  }

  @Test
  void mapBestPricedHotelToHotelNullTest() {
    List<BestPricedHotelDto> bestPricedHotelDtos = mapHotelToBestPricedHotelDto(null);
    Assertions.assertThat(bestPricedHotelDtos).isEmpty();
  }

  @Test
  void mapHotelToHotelPriceCalendarTest() {
    Hotel hotel1 = getHotel(50.0, ARRIVAL);
    Hotel hotel2 = getHotel(45.0, DEPARTURE);

    List<Hotel> hotels = Arrays.asList(hotel1, hotel2);
    List<HotelPriceCalendar> hotelPriceCalendarList = mapHotelToHotelPriceCalendar(hotels);

    HotelPriceCalendar bestPricedHotelDto1 = HotelPriceCalendar.builder()
        .date(ARRIVAL).price(new BigDecimal("50")).build();

    HotelPriceCalendar bestPricedHotelDto2 = HotelPriceCalendar.builder()
        .date(DEPARTURE).price(new BigDecimal("45")).build();

    assertThat(hotelPriceCalendarList).isEqualToComparingFieldByFieldRecursively
        (Arrays.asList(bestPricedHotelDto1, bestPricedHotelDto2));
  }

  @Test
  void mapHotelToHotelPriceCalendarEmptyTest() {
    List<HotelPriceCalendar> hotelPriceCalendarList = mapHotelToHotelPriceCalendar(Collections.emptyList());
    Assertions.assertThat(hotelPriceCalendarList).isEmpty();
  }

  @Test
  void mapHotelToHotelPriceCalendarNullTest() {
    List<HotelPriceCalendar> hotelPriceCalendarList = mapHotelToHotelPriceCalendar(null);
    Assertions.assertThat(hotelPriceCalendarList).isEmpty();
  }

  //location start

  void buildLocationPriceResponseTest() {

    Hotel hotel1 = getHotelWithPlaceId(50.0, ARRIVAL, "placeId1");
    Hotel hotel2 = getHotelWithPlaceId(45.0, DEPARTURE, "placeId2");

    List<Hotel> hotels = Arrays.asList(hotel1, hotel2);

    final LocationPriceResponse actualResponse = buildLocationPriceResponse(hotels);

    final LocationPriceResponse expectedResponse = buildLocationPriceResponseLocally(hotels);

    assertThat(actualResponse).isEqualTo(expectedResponse);

  }

  void buildLocationPriceResponseEmptyTest() {

    List<Hotel> hotels = Collections.emptyList();

    final LocationPriceResponse actualResponse = buildLocationPriceResponse(hotels);

    final LocationPriceResponse expectedResponse = buildLocationPriceResponseLocally(hotels);

    assertThat(actualResponse).isEqualTo(expectedResponse);

  }

  private Hotel getHotel(double v, String arrival) {
    return Hotel.builder().hotelCode("PLYPTI").hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(v), "G")))
        .date(arrival).build();
  }

  private Hotel getHotelWithPlaceId(final double v, final String arrival, final String placeId) {
    return Hotel.builder()
        .hotelCode("PLYPTI")
        .placeId(placeId)
        .hotelBrand("PI")
        .bestRoomPrice(new BestRoomPrice("DB", new Price(new BigDecimal(v), "G")))
        .date(arrival).build();
  }

  LocationPriceResponse buildLocationPriceResponseLocally(final List<Hotel> bestPricedHotels) {

    List<LocationPrice> locationPriceList = mapHotelToHotelPriceLocation(bestPricedHotels);

    return LocationPriceResponse
        .builder()
        .bestPricedHotels(locationPriceList)
        .build();
  }

  private List<LocationPrice> mapHotelToHotelPriceLocation(final List<Hotel> bestPricedHotels) {

    return ofNullable(bestPricedHotels)
        .orElse(Collections.emptyList()).stream()
        .map(hotel -> buildLocationPrice(hotel)
        ).collect(Collectors.toList());
  }

  private LocationPrice buildLocationPrice(final Hotel hotel) {

    final BestRoomPrice bestRoomPrice =
        ofNullable(hotel.getBestRoomPrice()).orElse(new BestRoomPrice(null, new Price()));

    return LocationPrice.builder()
        .placeId(hotel.getPlaceId())
        .hotelCode(hotel.getHotelCode())
        .bestPrice(getPriceDto(bestRoomPrice))
        .build();
  }

  private PriceDto getPriceDto(final BestRoomPrice bestRoomPrice) {
    return PriceDto.builder()
        .amount(bestRoomPrice.getPrice().getAmount())
        .currency(bestRoomPrice.getPrice().getCurrency())
        .build();
  }

  //location end

}
