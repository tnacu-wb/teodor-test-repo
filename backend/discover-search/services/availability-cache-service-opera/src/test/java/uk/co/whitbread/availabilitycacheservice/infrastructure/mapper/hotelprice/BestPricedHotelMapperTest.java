package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper.mapBestPricedHotelToHotel;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.hotelprice.BestPricedHotelToHotelMapper.mapLocationBestPricedHotelsToHotels;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.Price;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.hotelprice.BestRoomPrice;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.BestPricedHotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice.LocationBestPrice;

public class BestPricedHotelMapperTest {

  @Test
  public void mapBestPricedHotelToHotelTest() {
    BestPricedHotel bestPricedHotel = BestPricedHotel.builder().hotelCode("ALTGEO").bestPrice(new BigDecimal(25.00))
        .currency("G").build();
    List<Hotel> hotels = mapBestPricedHotelToHotel("DB", Arrays.asList(bestPricedHotel));

    Hotel expectedHotel = Hotel.builder()
        .hotelCode("ALTGEO")
        .bestRoomPrice(BestRoomPrice
            .builder().roomType("DB").price(new Price(new BigDecimal(25.00), "G"))
            .build())
        .build();

    assertThat(hotels).isEqualToComparingFieldByFieldRecursively(Arrays.asList(expectedHotel));
  }

  @Test
  public void mapBestPricedHotelToHotelEmptyTest() {
    List<Hotel> hotels = mapBestPricedHotelToHotel(null, Collections.emptyList());
    Assertions.assertThat(hotels).isEmpty();
  }

  @Test
  public void mapLocationBestPricedHotelsToHotelsTest() {

    final LocationBestPrice locationBestPrice = buildLocationBestPrice();

    Hotel expectedHotel = Hotel.builder()
        .hotelCode("ALTGEO")
        .placeId("placeId1")
        .bestRoomPrice(BestRoomPrice
            .builder().roomType("").price(new Price(new BigDecimal("30.0"), "G"))
            .build())
        .build();

    List<Hotel> hotels = mapLocationBestPricedHotelsToHotels(Arrays.asList(locationBestPrice));

    assertThat(hotels).isEqualToComparingFieldByFieldRecursively(Arrays.asList(expectedHotel));

  }

  @Test
  public void mapLocationBestPricedHotelsToHotelsEmptyTest() {

    List<Hotel> hotels = mapLocationBestPricedHotelsToHotels(Collections.emptyList());

    Assertions.assertThat(hotels).isEmpty();

  }

  private LocationBestPrice buildLocationBestPrice() {
    return LocationBestPrice.builder()
        .hotelCode("ALTGEO")
        .placeId("placeId1")
        .price(new BigDecimal("30.0"))
        .currency("G")
        .build();
  }

}
