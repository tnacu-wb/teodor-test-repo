package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.OperaHotelAvailabilitiesDto;

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
class OperaHotelAvailabilitiesControllerIT {

  private static final String ARRIVAL = LocalDate
      .parse(LocalDate.now().plusDays(0).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();
  private static final String DEPARTURE = LocalDate
      .parse(LocalDate.now().plusDays(1).toString(), DateTimeFormatter.ISO_LOCAL_DATE).toString();

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  @Autowired
  private HotelJpaRepository hotelJpaRepository;

  @Autowired
  private RateJpaRepository rateJpaRepository;

  @Autowired
  private RoomJpaRepository roomJpaRepository;

  @Test
  void badRequestWhenEmptyRoomTypesTest() {

    final String emptyRoomTypes = "";

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/availabilities/v1")
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("roomTypes", emptyRoomTypes)
        .queryParam("rooms", "1")
        .queryParam("roomQty", "1")
        .queryParam("sort", "DISTANCE").build().toUriString();

    ResponseEntity<OperaHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, OperaHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenArrivalIsBeforeTodayTest() {

    final LocalDate beforeTodayLocalDate = LocalDate.now().minusDays(2);
    final String beforeTodayLocalString = beforeTodayLocalDate.toString();

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/availabilities/v1")
        .queryParam("arrival", beforeTodayLocalString)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("roomTypes", "DB")
        .queryParam("rooms", "1")
        .queryParam("roomQty", "1")
        .queryParam("sort", "DISTANCE").build().toUriString();

    ResponseEntity<OperaHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, OperaHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void getOperaHotelAvailabilitiesWithZeroTest() {

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/availabilities/v1")
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "LONLEI,BASQUA,COVCRO")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("roomTypes", "DB")
        .queryParam("rooms", "1")
        .queryParam("roomQty", "1")
        .queryParam("sort", "DISTANCE").build().toUriString();

    ResponseEntity<OperaHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, OperaHotelAvailabilitiesDto.class);

    final OperaHotelAvailabilitiesDto responseBody = responseEntity.getBody();

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertThat(responseBody.getOperaHotelAvailabilities().size()).isEqualByComparingTo(0);
  }

  @Test
  void whenOperaHotelsAreInDbTest() {

    hotelJpaRepository.deleteAll();
    roomJpaRepository.deleteAll();
    rateJpaRepository.deleteAll();

    final String pmsSourceOpera = "OPERA";
    final LocalDate arrivalToday = LocalDate.now();
    final String arrivalTodayString = arrivalToday.toString();

    final LocalDate departureDate = arrivalToday.plusDays(3);
    final String departureString = departureDate.toString();

    final String roomTypeDouble = "DB";

    //make sure that the records are in DB
    final HotelEntity hotelEntity1 =
        buildHotelEntity("testId1", "DKINPT", arrivalToday.plusDays(1), pmsSourceOpera);

    final HotelEntity hotelEntity2 =
        buildHotelEntity("testId2", "JANOLD", arrivalToday.plusDays(2), pmsSourceOpera);

    final RoomEntity roomEntity1 = buildRoomEntity("roomId1", roomTypeDouble, hotelEntity1);
    final RoomEntity roomEntity2 = buildRoomEntity("roomId2", roomTypeDouble, hotelEntity2);

    final RatePlanEntity ratePlanEntity1 =
        buildRatePlanEntity(
            "rateId1", new BigDecimal("15.5"), "G", hotelEntity1, roomEntity1);

    final RatePlanEntity ratePlanEntity2 =
        buildRatePlanEntity(
            "rateId2", new BigDecimal("12"), "G", hotelEntity2, roomEntity2);

    hotelJpaRepository.save(hotelEntity1);
    hotelJpaRepository.save(hotelEntity2);
    roomJpaRepository.save(roomEntity1);
    roomJpaRepository.save(roomEntity2);
    rateJpaRepository.save(ratePlanEntity1);
    rateJpaRepository.save(ratePlanEntity2);

    assertThat(hotelJpaRepository.findAll().size()).isEqualByComparingTo(2);
    assertThat(roomJpaRepository.findAll().size()).isEqualByComparingTo(2);
    assertThat(rateJpaRepository.findAll().size()).isEqualByComparingTo(2);

    final int expectedTotal = 2;

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/search/hotels/availabilities/v1")
        .queryParam("arrival", arrivalTodayString)
        .queryParam("departure", departureString)
        .queryParam("country", "gb")
        .queryParam("language", "en")
        .queryParam("hotelCodes", "DKINPT,JANOLD")
        .queryParam("page", 1)
        .queryParam("size", 40)
        .queryParam("adults", "2")
        .queryParam("children", "0")
        .queryParam("cot", "false")
        .queryParam("roomTypes", "DB")
        .queryParam("rooms", "1")
        .queryParam("roomQty", "1")
        .queryParam("sort", "DISTANCE").build().toUriString();

    ResponseEntity<OperaHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, OperaHotelAvailabilitiesDto.class);

    final OperaHotelAvailabilitiesDto responseBody = responseEntity.getBody();

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

    assertThat(responseBody.getTotal()).isEqualByComparingTo(expectedTotal);

    assertThat(responseBody.getOperaHotelAvailabilities().size())
        .isEqualByComparingTo(expectedTotal);

  }

  private HotelEntity buildHotelEntity(
      final String id, final String hotelCode, final LocalDate date, final String pmsSource) {
    return HotelEntity.builder()
        .id(id)
        .hotelCode(hotelCode)
        .date(date)
        .pmsSource(pmsSource)
        .build();
  }

  private RatePlanEntity buildRatePlanEntity(
      final String rateId,
      final BigDecimal amount,
      final String currency, final HotelEntity hotelEntity, final RoomEntity roomEntity) {
    return RatePlanEntity.builder()
        .id(rateId)
        .amount(amount)
        .currency(currency)
        .hotel(hotelEntity)
        .room(roomEntity)
        .availability(true)
        .rateClassification("S")
        .maxNights(3)
        .minNights(1)
        .build();
  }

  private RoomEntity buildRoomEntity(
      final String roomId, final String roomType, final HotelEntity hotelEntity) {
    return RoomEntity.builder()
        .id(roomId)
        .roomType(roomType)
        .hotel(hotelEntity)
        .quantity(3)
        .build();
  }

}
