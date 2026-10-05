package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.ReservationIdList;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.exception.RoomAllocationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip.properties.RoomAllocationOhipProperties;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockOhipRoomAllocationClientTest {


  @Mock
  private RoomAllocationOhipProperties roomAllocationOhipProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.stubFor(post(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipRoomAllocationVacantRooms_shouldReturnException() {
    final OhipRoomAllocationClient ohipRoomAllocationClient = new OhipRoomAllocationClient(
        webClient,
        roomAllocationOhipProperties);

    when(roomAllocationOhipProperties.getVacantRoomsEndpoint()).thenReturn(
        "/fof/v1/hotels/{HotelId}/rooms");

    //Act
    assertThrows(RoomAllocationException.class,
        () -> ohipRoomAllocationClient.getVacantRoomIds("hotelId", "roomType"));
  }

  @Test
  void testOhipRoomAllocationRoom_shouldReturnException() {
    final OhipRoomAllocationClient ohipRoomAllocationClient = new OhipRoomAllocationClient(
        webClient,
        roomAllocationOhipProperties);
    final RoomAllocationRequest request = mockRoomAllocationRequest();
    when(roomAllocationOhipProperties.getRoomsAssignmentEndpoint()).thenReturn(
        "/fof/v1/hotels/{HotelId}/reservations/{ReservationId}/roomAssignments");

    //Act
    assertThrows(RoomAllocationException.class,
        () -> ohipRoomAllocationClient.allocateRoom(request));
  }

  @Test
  void testOhipRoomAllocationHouseKeepingRoomStatus_shouldReturnException() {
    final OhipRoomAllocationClient ohipRoomAllocationClient = new OhipRoomAllocationClient(
        webClient,
        roomAllocationOhipProperties);
    when(roomAllocationOhipProperties.getGetHousekeeping()).thenReturn(
        "/hsk/v1/hotels/{HotelId}/housekeepingOverview");

    //Act
    assertThrows(RoomAllocationException.class,
        () -> ohipRoomAllocationClient.fetchHouseKeepingRoomStatus("hotelId", "roomId"));
  }

  public RoomAllocationRequest mockRoomAllocationRequest() {
    var criteria = Criteria.builder().hotelId("HOTEL_ID").roomId("ROOM_ID")
        .reservationIdList(List.of(
            ReservationIdList.builder().id("id1").type("type1").build(),
            ReservationIdList.builder().id("id2").type("type2").build())).build();
    return RoomAllocationRequest.builder().criteria(criteria).build();
  }
}
