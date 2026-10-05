package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.HotelEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.RatePlanEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.entity.RoomEntity;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.RateJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.RoomJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.BestPricedHotelDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendar;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceCalendarResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice.HotelPriceResponseDto;

@Disabled("Not compatible with Kaniko executor")

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQL95Dialect"
    })
@ContextConfiguration(initializers = {PostgresIntegrationTestConfig.class})
@ActiveProfiles(profiles = {"increase-batch-size"})
@AutoConfigureTestRestTemplate
class HotelPriceControllerIT {

  @Autowired
  private TestRestTemplate testRestTemplate;

  @LocalServerPort
  private int port;

  @Autowired
  private HotelJpaRepository hotelJpaRepository;

  @Autowired
  private RoomJpaRepository roomJpaRepository;

  @Autowired
  private RateJpaRepository rateJpaRepository;

  @Test
  void shouldReturnZeroRecordsIfNoRecordsInDb() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "db";
    final String hotelCodes = "TKINPT,MANOLD";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/prices")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    ResponseEntity<HotelPriceResponseDto> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceResponseDto.class);

    HotelPriceResponseDto hotelPriceResponse = responseEntity.getBody();

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertThat(hotelPriceResponse.getBestPricedHotels().size()).isEqualByComparingTo(0);

  }

  @Test
  void getHotelsWithPricesSuccessTest() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "db";
    final String hotelCodes = "TKINPT,MANOLD";

    //make sure that required records are available in hotel_ac, rate and room table
    final HotelEntity hotelEntity1 =
        buildHotelEntity("testId1", "TKINPT", arrivalLocalDate);

    final HotelEntity hotelEntity2 =
        buildHotelEntity("testId2", "MANOLD", arrivalLocalDate.plusDays(1));

    final RoomEntity roomEntity1 = buildRoomEntity("roomId1", roomTypeDouble, hotelEntity1);
    final RoomEntity roomEntity2 = buildRoomEntity("roomId2", roomTypeDouble, hotelEntity2);

    final RatePlanEntity ratePlanEntity1 =
        buildRatePlanEntity("rateId1", new BigDecimal("15.5"), "G", hotelEntity1);

    final RatePlanEntity ratePlanEntity2 =
        buildRatePlanEntity("rateId2", new BigDecimal("12"), "G", hotelEntity2);

    hotelJpaRepository.save(hotelEntity1);
    hotelJpaRepository.save(hotelEntity2);
    roomJpaRepository.save(roomEntity1);
    roomJpaRepository.save(roomEntity2);
    rateJpaRepository.save(ratePlanEntity1);
    rateJpaRepository.save(ratePlanEntity2);

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/prices")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    ResponseEntity<HotelPriceResponseDto> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceResponseDto.class);

    HotelPriceResponseDto hotelPriceResponse = responseEntity.getBody();

    final BestPricedHotelDto bestPricedExpected1 =
        buildBestPricedHotel("MANOLD",
            buildPriceDto(new BigDecimal("12.00"), "G"), "PI");
    final BestPricedHotelDto bestPricedExpected2 =
        buildBestPricedHotel("TKINPT",
            buildPriceDto(new BigDecimal("15.50"), "G"), "PI");

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertThat(hotelPriceResponse.getBestPricedHotels().size()).isEqualByComparingTo(2);
    assertTrue(hotelPriceResponse.getBestPricedHotels().contains(bestPricedExpected1));
    assertTrue(hotelPriceResponse.getBestPricedHotels().contains(bestPricedExpected2));

  }

  @Test
  void badRequestWhenInvalidRoomTypeTest() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "invalidRoomType";
    final String hotelCodes = "TKINPT,MANOLD";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/prices")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    ResponseEntity<HotelPriceResponseDto> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceResponseDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenArrivalIsBeforeTodayTest() {
    final LocalDate date1 = LocalDate.now().minusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeTwin = "TWIN";
    final String hotelCodes = "TKINPT,MANOLD";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/prices")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeTwin)
        .build().toUriString();

    ResponseEntity<HotelPriceResponseDto> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceResponseDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //================= getHotelPriceDetails start ======================

  @Test
  void getHotelPriceDetailsSuccessTest() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "DB";
    final String hotelCode = "TKINPT";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/" + hotelCode + "/calendars")
        .queryParam("hotelCode", hotelCode)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    ResponseEntity<HotelPriceCalendarResponse> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceCalendarResponse.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertThat(responseEntity.getBody().getBestPricedHotels().size())
        .isEqualByComparingTo(0);

  }

  @Test
  void getHotelPriceDetailsBadRequestWhenInvalidRoomTypeTest() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeInvalid = "invalidRoomType";
    final String hotelCode = "TKINPT";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/" + hotelCode + "/calendars")
        .queryParam("hotelCode", hotelCode)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeInvalid)
        .build().toUriString();

    ResponseEntity<HotelPriceCalendarResponse> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceCalendarResponse.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void getHotelPriceDetailsBadRequestWhenArrivalBeforeTodayTest() {
    final LocalDate date1 = LocalDate.now().minusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "db";
    final String hotelCode = "TKINPT";

    hotelJpaRepository.deleteAll();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/" + hotelCode + "/calendars")
        .queryParam("hotelCode", hotelCode)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    ResponseEntity<HotelPriceCalendarResponse> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceCalendarResponse.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void getHotelPriceDetailsSuccessOneRecordTest() {
    final LocalDate date1 = LocalDate.now().plusDays(3);
    final LocalDate date2 = LocalDate.now().plusDays(4);

    final LocalDate arrivalLocalDate = date1.minusDays(1);
    final String arrival = arrivalLocalDate.toString();

    final LocalDate departureLocalDate = date2.plusDays(1);
    final String departure = departureLocalDate.toString();

    final String roomTypeDouble = "DB";
    final String hotelCode = "TKINPP";

    //hotelJpaRepository.deleteAll();
    //make sure that required records are available in hotel_ac, rate and room table
    final HotelEntity hotelEntity1 =
        buildHotelEntity("testId1", "TKINPP", arrivalLocalDate);

    final RoomEntity roomEntity1 = buildRoomEntity("roomId1", roomTypeDouble, hotelEntity1);

    final RatePlanEntity ratePlanEntity1 =
        buildRatePlanEntity("rateId1", new BigDecimal("15.5"), "G", hotelEntity1);

    hotelJpaRepository.save(hotelEntity1);
    roomJpaRepository.save(roomEntity1);
    rateJpaRepository.save(ratePlanEntity1);

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/" + hotelCode + "/calendars")
        .queryParam("hotelCode", hotelCode)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("roomType", roomTypeDouble)
        .build().toUriString();

    final HotelPriceCalendar hotelPriceCalendar =
        buildHotelPriceCalendar(arrival, new BigDecimal("15.50"));

    ResponseEntity<HotelPriceCalendarResponse> responseEntity =
        testRestTemplate.getForEntity(uri, HotelPriceCalendarResponse.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
    assertThat(responseEntity.getBody().getBestPricedHotels().size())
        .isEqualByComparingTo(1);
    assertThat(responseEntity.getBody().getBestPricedHotels())
        .contains(hotelPriceCalendar);

  }

  //================= getHotelPriceDetails end ======================

  private HotelPriceCalendar buildHotelPriceCalendar(final String date, final BigDecimal price) {
    return HotelPriceCalendar.builder()
        .date(date)
        .price(price)
        .build();
  }

  private PriceDto buildPriceDto(final BigDecimal amount, final String currency) {
    return PriceDto.builder()
        .amount(amount)
        .currency(currency)
        .build();
  }

  private BestPricedHotelDto buildBestPricedHotel(
      final String hotelCode, final PriceDto bestPrice, final String hotelBrand) {
    return BestPricedHotelDto.builder()
        .hotelCode(hotelCode)
        .hotelBrand(hotelBrand)
        .bestPrice(bestPrice)
        .build();

  }

  private HotelEntity buildHotelEntity(
      final String id, final String hotelCode, final LocalDate date) {
    return HotelEntity.builder()
        .id(id)
        .hotelCode(hotelCode)
        .date(date)
        .build();
  }

  private RatePlanEntity buildRatePlanEntity(
      final String rateId,
      final BigDecimal amount, final String currency, final HotelEntity hotelEntity) {
    return RatePlanEntity.builder()
        .id(rateId)
        .amount(amount)
        .currency(currency)
        .hotel(hotelEntity)
        .build();
  }

  private RoomEntity buildRoomEntity(
      final String roomId, final String roomType, final HotelEntity hotelEntity) {
    return RoomEntity.builder()
        .id(roomId)
        .roomType(roomType)
        .hotel(hotelEntity)
        .build();
  }

}
