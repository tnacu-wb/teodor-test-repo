package uk.co.whitbread.availabilitycacheservice.infrastructure.mapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.HotelMapper.mapHotelEntityToHotel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.availabilitycacheservice.domain.model.availabilities.Hotel;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.RoomEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.SearchCriteria;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.enums.RoomType;


public class HotelMapperTest {

  private static final String ARRIVAL = LocalDate.parse(LocalDate.now().plusDays(0).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate.parse(LocalDate.now().plusDays(1).toString(),
      DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String COUNTRY = "gb";
  private static final String LANGUAGE = "en";
  private SearchCriteria searchCriteria;

  @BeforeEach
  public void setup() {
    searchCriteria = buildSearchCriteria();
  }

  @Test
  public void mapHotelEntityToHotelTest() {
    RatePlanEntity ratePlanEntity = RatePlanEntity.builder()
        .rateClassification("A")
        .availability(true)
        .build();

    RoomEntity roomEntity = RoomEntity.builder()
        .roomType("DB")
        .build();

    HotelEntity hotelEntity = HotelEntity.builder()
        .hotelCode("1")
        .rates(Arrays.asList(ratePlanEntity))
        .rooms(Arrays.asList(roomEntity))
        .build();

    List<HotelEntity> hotelEntities = Arrays.asList(hotelEntity);

    List<Hotel> availableHotels = mapHotelEntityToHotel(hotelEntities);

    assertThat(availableHotels).isNotEmpty().extracting(Hotel::getHotelCode)
        .containsExactly("1");
    assertThat(availableHotels).extracting(Hotel::getRates).isNotEmpty();
  }

  @Test
  public void mapHotelEntityToHotelEmptyTest() {
    List<Hotel> availableHotels = mapHotelEntityToHotel(Collections.emptyList());
    assertThat(availableHotels).isEmpty();
  }

  private SearchCriteria buildSearchCriteria() {
    return SearchCriteria.builder()
        .hotelCodes(Arrays.asList("1", "2", "3", "4", "5"))
        .arrival(ARRIVAL)
        .departure(DEPARTURE)
        .adults(new int[]{1})
        .children(new int[]{1})
        .cot(false)
        .rooms(1)
        .type(new String[]{RoomType.SB.name()})
        .language(LANGUAGE)
        .country(COUNTRY)
        .build();
  }

}
