package uk.co.whitbread.ohip.infrastructure.rest.client.roomallocation.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.Criteria;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.ReservationIdList;
import uk.co.whitbread.ohip.domain.model.roomallocation.in.RoomAllocationRequest;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HotelRoomsDetails;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoom;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HouseKeepingRoomStatusResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.Housekeeping;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRoomInfo;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRoomStatus;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.HousekeepingRooms;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomLinks;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipRoomAllocationClientTest {

  @InjectMocks
  OhipRoomAllocationClient ohipRoomAllocationClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getVacantRoomIds__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VacantRoomResponse.class)).thenReturn(mockVacantRoom());

    //Act
    VacantRoomResponse vacantRoomResponse =
        ohipRoomAllocationClient.getVacantRoomIds("TestHotelId", "roomType");

    //Assert
    assertThat(vacantRoomResponse, notNullValue());
  }

  @Test
  void allocateRoom__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>)any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomAllocationResponse.class)).thenReturn(
        mockRoomAllocationResponse());

    //Act
    RoomAllocationResponse roomAllocationResponse = ohipRoomAllocationClient.allocateRoom(
        mockRoomAllocationRequest());

    //Assert
    assertThat(roomAllocationResponse, notNullValue());
  }

  @Test
  void fetchHouseKeepingRoomStatus__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(HouseKeepingRoomStatusResponse.class)).thenReturn(
        mockFetchHouseKeepingRoomStatus());

    //Act
    HouseKeepingRoomStatusResponse houseKeepingRoomStatusResponse = ohipRoomAllocationClient.fetchHouseKeepingRoomStatus(
        "MANOLD", "007");

    //Assert
    assertThat(houseKeepingRoomStatusResponse, notNullValue());
  }

  private Mono<HouseKeepingRoomStatusResponse> mockFetchHouseKeepingRoomStatus() {
    var houseKeepingRoomStatusResponse = HouseKeepingRoomStatusResponse.builder()
        .housekeepingRoomInfo(
            HousekeepingRoomInfo.builder()
                .housekeepingRooms(
                    HousekeepingRooms.builder().hotelId("MANOLD").room(Collections.singletonList(
                        HouseKeepingRoom.builder().roomId("007")
                            .housekeeping(Housekeeping.builder().housekeepingRoomStatus(
                                HousekeepingRoomStatus.builder().housekeepingRoomStatusText("Clean")
                                    .build()).build()).build())).build())
                .build()).build();

    return Mono.just(houseKeepingRoomStatusResponse);
  }


  private Mono<VacantRoomResponse> mockVacantRoom() {
    var vacantRoom = VacantRoomResponse.builder()
        .hotelRoomsDetails(HotelRoomsDetails.builder().hotelId("HOTEL_ID").build()).build();
    return Mono.just(vacantRoom);
  }

  private Mono<RoomAllocationResponse> mockRoomAllocationResponse() {
    var roomAllocation = RoomAllocationResponse.builder()
        .links(List.of(RoomLinks.builder().operationId("OPERATION_ID").build())).build();
    return Mono.just(roomAllocation);
  }

  private RoomAllocationRequest mockRoomAllocationRequest() {
    var criteria = Criteria.builder().hotelId("HOTEL_ID").roomId("ROOM_ID")
        .reservationIdList(List.of(
            ReservationIdList.builder().id("id1").type("type1").build(),
            ReservationIdList.builder().id("id2").type("type2").build())).build();
    return RoomAllocationRequest.builder().criteria(criteria).build();
  }

}
