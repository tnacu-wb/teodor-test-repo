package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.AvailableCost;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRatePlan;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionRoom;
import uk.co.whitbread.availabilitycacheservice.domain.ports.primary.DistributionHotelAvailabilitiesPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.PostgresIntegrationTestConfig;
import uk.co.whitbread.availabilitycacheservice.infrastructure.mapper.dsitribution.DistributionHotelAvailabilitiesMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelAvailabilitiesDto;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.distribution.DistributionHotelDto;

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
class DistributionHotelAvailabilitiesControllerIT {

  private static final String ARRIVAL = LocalDate.now().toString();
  private static final String DEPARTURE = LocalDate.now().plusDays(1).toString();

  @MockitoBean
  private DistributionHotelAvailabilitiesPort distributionHotelAvailabilitiesPort;

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate testRestTemplate;

  //lonlei: hotel code in small letters will cause bad request
  @Test
  void badRequestWhenHotelCodesWithSmallLetters() {

    final String hotelCodes = "lonlei,BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //BASQ: hotel code less then 6 chars, will cause bad request!
  @Test
  void badRequestWhenHotelCodesLessThenSixChars() {

    final String hotelCodes = "BASQ,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //COVC01: hotel code less then 6 chars,  will cause bad request!
  @Test
  void badRequestWhenHotelCodeContainingNumber() {

    final String hotelCodes = "COVC01";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //maxNight Validation
  @Test
  void badRequestWhenMaxNightIsOutOfAllowedRange() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String today = LocalDate.now().toString();
    final String threeSixtyFifthDay = LocalDate.now().plusDays(365).toString();
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, today, threeSixtyFifthDay, rooms,
        adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //maxNight Validation, success when max night is within allowed range.
  @Test
  void successWhenMaxNightIsWithinAllowedRange() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String today = LocalDate.now().toString();
    final String threeSixtyFourthDay = LocalDate.now().plusDays(364).toString();
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, today, threeSixtyFourthDay, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);

  }

  @Test
  void badRequestWhenDepartureIsBeforeArrival() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String today = LocalDate.now().toString();
    final String beforeToday = LocalDate.now().minusDays(2).toString();
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, today, beforeToday, rooms, adults,
        children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenAdultsArraySizeIsNotEqualToNumberOfRooms() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1"; //should be like "1,1"
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults,
        children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenAdultsAIsNull() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = null; //should be like "1,1"
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults,
        children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void badRequestWhenChildrenIsNull() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = null;
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
  }

  @Test
  void badRequestWhenChildrenArraySizeIsNotEqualToNumberOfRooms() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0"; //should be like "0,0"
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenPayloadIsNull() {

    //no query params being passed
    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/dist/search/hotels/availabilities").toUriString();

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //Min room validation
  @Test
  void badRequestWhenRoomsIsLessThenOne() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "0";
    final String adults = "0";
    final String children = "0";
    final String roomQty = "0";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  //Max room validation
  @Test
  void badRequestWhenRoomsIsGreaterThenNine() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "10";
    final String adults = "1,1,1,1,1,1,1,1,1,1";
    final String children = "0,0,0,0,0,0,0,0,0,0";
    final String roomQty = "1,1,1,1,1,1,1,1,1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenRoomQtyIsNull() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = null;
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenRoomQtyIsEmpty() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1"; //should be like "1,1"
    final String children = "0,0";
    final String roomQty = "";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenSumOfRoomQtyIsNotEqualToRoomsValue() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenRoomTypesIsNull() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = null;
    final String roomTypes2 = "null";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenRoomTypesIsEmpty() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1";
    final String roomTypes1 = "";
    final String roomTypes2 = "";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }

  @Test
  void badRequestWhenRoomTypeArrayOfArraySizeIsNotEqualToRooms() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";

    final String uri = UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/dist/search/hotels/availabilities")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", ARRIVAL)
        .queryParam("departure", DEPARTURE)
        .queryParam("adults", adults)
        .queryParam("children", children)
        .queryParam("cot", "false")
        .queryParam("roomQty", roomQty)
        .queryParam("country", "GB")
        .queryParam("language", "EN")
        .queryParam("roomTypes", roomTypes1) //only one roomTypes, we need to send 2
        .queryParam("rooms", rooms)
        .build().toUriString();

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);

  }


  //Success Response when all the inputs in the payload is correct.
  @Test
  void successResponseWhenNoValidationFails() {

    final String hotelCodes = "BASQUA,COVCRO";
    final String rooms = "2";
    final String adults = "1,1";
    final String children = "0,0";
    final String roomQty = "1,1";
    final String roomTypes1 = "DBLDBL";
    final String roomTypes2 = "ZIPSB";

    final String uri = getUri(
        hotelCodes, ARRIVAL, DEPARTURE, rooms, adults, children, roomQty, roomTypes1, roomTypes2);

    final List<String> hotelCodesList = new ArrayList<>();
    hotelCodesList.add("BASQUA");
    hotelCodesList.add("COVCRO");

    final DistributionPayload distributionPayload =
        buildDistributionPayload(hotelCodesList, ARRIVAL, DEPARTURE, new int[]{1, 1},
            new int[]{0, 0}, new boolean[]{false}, new int[]{1, 1}, "GB", "EN",
            new String[][]{{roomTypes1}, {roomTypes2}}, 2);

    final List<DistributionHotel> distributionHotelList = buildDistributionHotelList();

    DistributionHotelAvailabilitiesMapper distributionHotelAvailabilitiesMapper =
        Mappers.getMapper(DistributionHotelAvailabilitiesMapper.class);

    final List<DistributionHotelDto> distributionHotelDtoList =
        distributionHotelAvailabilitiesMapper.toDistributionHotelDtoList(distributionHotelList);

    final DistributionHotelAvailabilitiesDto expectedResponse =
        buildDistributionHotelAvailabilitiesDto(distributionHotelDtoList);

    Mockito.when(distributionHotelAvailabilitiesPort.getHotelAvailabilities(distributionPayload))
        .thenReturn(distributionHotelList);

    ResponseEntity<DistributionHotelAvailabilitiesDto> responseEntity =
        testRestTemplate.getForEntity(uri, DistributionHotelAvailabilitiesDto.class);

    assertThat(responseEntity).isNotNull();
    assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    assertEquals(expectedResponse, responseEntity.getBody());

  }

  private DistributionHotelAvailabilitiesDto buildDistributionHotelAvailabilitiesDto(
      final List<DistributionHotelDto> distributionHotelDtoList) {
    final DistributionHotelAvailabilitiesDto distributionHotelAvailabilitiesDto =
        new DistributionHotelAvailabilitiesDto(
            distributionHotelDtoList.size(), distributionHotelDtoList);

    return distributionHotelAvailabilitiesDto;

  }

  private String getUri(final String hotelCodes, final String arrival, final String departure,
      final String rooms, final String adults, final String children, final String roomQty,
      final String roomTypes1, final String roomTypes2) {
    return UriComponentsBuilder
        .fromUriString("http://localhost")
        .port(port)
        .path("/dist/search/hotels/availabilities")
        .queryParam("hotelCodes", hotelCodes)
        .queryParam("arrival", arrival)
        .queryParam("departure", departure)
        .queryParam("adults", adults)
        .queryParam("children", children)
        .queryParam("cot", "false")
        .queryParam("roomQty", roomQty)
        .queryParam("country", "GB")
        .queryParam("language", "EN")
        .queryParam("roomTypes", roomTypes1)
        .queryParam("roomTypes", roomTypes2)
        .queryParam("rooms", rooms)
        .build().toUriString();
  }

  private DistributionPayload buildDistributionPayload(
      List<String> hotelCodes, String arrival, String departure, int[] adults,
      int[] children, boolean[] cot, int[] roomQty, String country, String language,
      String[][] roomTypes, int rooms) {
    return DistributionPayload.builder()
        .hotelCodes(hotelCodes)
        .arrival(arrival)
        .departure(departure)
        .adults(adults)
        .children(children)
        .cot(cot)
        .roomQty(roomQty)
        .country(country)
        .language(language)
        .roomTypes(roomTypes)
        .rooms(rooms)
        .build();
  }

  private List<DistributionHotel> buildDistributionHotelList() {

    final List<DistributionHotel> distributionHotelList = new ArrayList<>();

    final List<AvailableCost> availableCosts1 = new ArrayList<>();
    final AvailableCost availableCost1 =
        buildAvailableCost(LocalDate.now(), new BigDecimal("45.65"));
    availableCosts1.add(availableCost1);

    final List<DistributionRoom> rooms1 = new ArrayList<>();
    final DistributionRoom distributionRoom1 = buildDistributionRoom(availableCosts1);
    rooms1.add(distributionRoom1);

    final List<DistributionRatePlan> rates1 = new ArrayList<>();
    DistributionRatePlan distributionRatePlan1 =
        buildDistributionRatePlan("STANDERD", "S", rooms1);
    rates1.add(distributionRatePlan1);

    final DistributionHotel distributionHotel1 =
        buildDistributionHotel("TKINPT", false, rates1);
    distributionHotelList.add(distributionHotel1);

    return distributionHotelList;
  }

  private DistributionHotel buildDistributionHotel(
      final String hotelCode, final boolean limitedAvail, final List<DistributionRatePlan> rates) {
    return DistributionHotel.builder()
        .hotelCode(hotelCode)
        .hotelName(hotelCode)
        .hotelBrand("pi.com")
        .available(true)
        //.limitedAvailability(limitedAvail)
        .arrivalDateToday(true)
        .pmsSource("OPERA")
        .rates(rates)
        .build();
  }

  private DistributionRatePlan buildDistributionRatePlan(
      final String code, final String classification, final List<DistributionRoom> rooms) {
    return DistributionRatePlan.builder()
        .code(code)
        .classification(classification)
        .rooms(rooms)
        .build();
  }

  private DistributionRoom buildDistributionRoom(final List<AvailableCost> availableCosts) {
    return DistributionRoom.builder()
        .roomType("DBL")
        .cotRequired(false)
        .qtyRequested(1)
        //.qtyAvailable(5)
        .availableCosts(availableCosts)
        .build();
  }

  private AvailableCost buildAvailableCost(final LocalDate date, final BigDecimal amount) {

    return AvailableCost.builder()
        .date(date)
        .amount(amount)
        .currency("G")
        .qtyAvailable(5)
        .build();
  }

}
