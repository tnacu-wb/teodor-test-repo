package uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreference;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskPreferenceCollection;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskReservationPreferences;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.KioskRoom;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomAllocationResponse;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.RoomLinks;
import uk.co.whitbread.ohip.domain.model.roomallocation.out.VacantRoomResponse;
import uk.co.whitbread.ohip.domain.ports.primary.RoomAllocationInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.HouseKeepingRoomStatusResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.ReservationPreferencesResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.RoomAllocationRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.RoomAllocationResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.mapper.VacantRoomResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.CriteriaDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.ReservationIdListDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.in.RoomAllocationRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.HotelRoomsDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.HouseKeepingResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.HousekeepingDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskPreferenceCollectionDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskPreferenceDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskReservationPreferencesDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.KioskRoomDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.RoomAllocationResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.RoomLinksDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.roomallocation.model.out.VacantRoomResponseDto;

@ExtendWith(MockitoExtension.class)
class RoomAllocationControllerTest {

  @InjectMocks
  RoomAllocationController roomAllocationController;
  @Mock
  private ReservationPreferencesResponseMapper reservationPreferencesResponseMapper;
  @Mock
  private RoomAllocationInPort roomAllocationInPort;
  @Mock
  private VacantRoomResponseMapper vacantRoomResponseMapper;
  @Mock
  private RoomAllocationRequestMapper roomAllocationRequestMapper;
  @Mock
  private RoomAllocationResponseMapper roomAllocationResponseMapper;
  @Mock
  private HouseKeepingRoomStatusResponseMapper houseKeepingRoomStatusResponseMapper;

  @Test
  void getVacantRooms__ShouldReturnOk() {
    //Arrange
    when(roomAllocationInPort.getVacantRoomIds(anyString(), anyString())).thenReturn(
        mockVacantRoom());
    when(vacantRoomResponseMapper.toVacantRoomResponseDto(any())).thenReturn(
        mockVacantRoomResponseDto());

    //act
    VacantRoomResponse vacatRoom = roomAllocationInPort.getVacantRoomIds("HOTEL_ID", "DOUBLE");
    VacantRoomResponseDto vacantRoomResponseDto = vacantRoomResponseMapper.toVacantRoomResponseDto(
        vacatRoom);
    VacantRoomResponseDto response = roomAllocationController.getVacantRooms("HOTEL_ID", "DOUBLE");

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(
        vacantRoomResponseDto.getHotelRoomsDetails().getRoom().get(0).getRoomId(),
        response.getHotelRoomsDetails().getRoom().get(0).getRoomId());
  }

  @Test
  void fetchReservationWithPreference__ShouldReturnOk() {
    //Arrange
    when(roomAllocationInPort.fetchReservationWithPreference(anyString(), anyString())).thenReturn(
        mockKioskReservationPreference());
    when(reservationPreferencesResponseMapper.toDto(any())).thenReturn(
        mockKioskReservationPreferenceDto());

    //act
    var kioskReservationPreferences = roomAllocationInPort.fetchReservationWithPreference(
        "HOTEL_ID",
        "1234");
    var kioskReservationPreferencesDto = reservationPreferencesResponseMapper.toDto(
        kioskReservationPreferences);
    var response = roomAllocationController.fetchReservationWithPreference(
        "HOTEL_ID", "1234");

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(
        kioskReservationPreferencesDto.getKioskPreferenceCollection().get(0).getKioskPreference()
            .get(0).getPreferenceValue(),
        response.getKioskPreferenceCollection().get(0).getKioskPreference()
            .get(0).getPreferenceValue());
  }

  @Test
  void fetchHouseKeepingRoomStatus__ShouldReturnOk() {
    //Arrange
    when(roomAllocationInPort.fetchHouseKeepingRoomStatus(anyString(), anyString())).thenReturn(
        mockFetchHouseKeepingRoomStatus());
    when(houseKeepingRoomStatusResponseMapper.toHouseKeepingResponseDto(any(), any())).thenReturn(
        mockHouseKeepingResponseDto());

    //act
    HouseKeepingRoomStatusResponse houseKeepingRoomStatus = roomAllocationInPort.fetchHouseKeepingRoomStatus(
        "HOTEL_ID", "007");

    HouseKeepingResponseDto response = roomAllocationController.fetchHouseKeepingRoomStatus(
        "HOTEL_ID", "007");

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(
        houseKeepingRoomStatus.getHousekeepingRoomInfo().getHousekeepingRooms().getRoom().get(0)
            .getHousekeeping().getHousekeepingRoomStatus().getHousekeepingRoomStatusText(),
        response.getStatus());
  }

  private HouseKeepingResponseDto mockHouseKeepingResponseDto() {
    return HouseKeepingResponseDto.builder().roomId("007").hotelId("MANOLD").status("Clean")
        .build();
  }

  @Test
  void allocateRooms__ShouldReturnOk() {
    //Arrange
    RoomAllocationRequestDto requestDto = mockRoomAllocationRequestDto();
    when(roomAllocationRequestMapper.toAllocateRequestModel(any())).thenReturn(
        mockRoomAllocationRequest());
    when(roomAllocationInPort.allocateRoom(any())).thenReturn(
        mockRoomAllocationResponse());
    when(roomAllocationResponseMapper.toDto(any())).thenReturn(mockRoomAllocationResponseDto());

    //act
    RoomAllocationRequest request = roomAllocationRequestMapper.toAllocateRequestModel(requestDto);
    RoomAllocationResponse allocationResponse = roomAllocationInPort.allocateRoom(request);
    RoomAllocationResponseDto allocationResponseDto = roomAllocationResponseMapper.toDto(
        allocationResponse);
    RoomAllocationResponseDto response = roomAllocationController.allocateRooms(requestDto);

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(allocationResponseDto.getLinks().get(0), response.getLinks().get(0));
  }

  private VacantRoomResponse mockVacantRoom() {
    return VacantRoomResponse.builder()
        .hotelRoomsDetails(HotelRoomsDetails.builder().hotelId("HOTEL_ID").room(List.of(
            KioskRoom.builder().floor("10").roomId("567").build())).build()).build();
  }

  private KioskReservationPreferences mockKioskReservationPreference() {
    return KioskReservationPreferences.builder()
        .kioskPreferenceCollection(Collections.singletonList(KioskPreferenceCollection.builder()
            .kioskPreference(
                Arrays.asList(
                    KioskPreference.builder().preferenceValue("COTR").description("Cot Requested")
                        .build(),
                    KioskPreference.builder().preferenceValue("QUAD").description("Booked as Quad")
                        .build()))
            .preferenceType("SPECIAL REQUEST")
            .preferenceTypeDescription("Special Request")
            .build()))
        .build();
  }

  private KioskReservationPreferencesDto mockKioskReservationPreferenceDto() {
    return KioskReservationPreferencesDto.builder()
        .kioskPreferenceCollection(Collections.singletonList(KioskPreferenceCollectionDto.builder()
            .kioskPreference(
                Arrays.asList(
                    KioskPreferenceDto.builder().preferenceValue("COTR")
                        .description("Cot Requested")
                        .build(),
                    KioskPreferenceDto.builder().preferenceValue("QUAD")
                        .description("Booked as Quad")
                        .build()))
            .preferenceType("SPECIAL REQUEST")
            .preferenceTypeDescription("Special Request")
            .build()))
        .build();
  }

  private VacantRoomResponseDto mockVacantRoomResponseDto() {
    var room = new KioskRoomDto("123", new HousekeepingDto());
    var roomDetails = new HotelRoomsDetailsDto(List.of(room), "HOTEL_ID");
    return new VacantRoomResponseDto(roomDetails);
  }

  private RoomAllocationRequestDto mockRoomAllocationRequestDto() {
    var reservationId = new ReservationIdListDto("Reservation", "12345");
    var criteria = new CriteriaDto("HOTEL_ID", List.of(reservationId), "12", false, true);
    return new RoomAllocationRequestDto(criteria);
  }

  private RoomAllocationRequest mockRoomAllocationRequest() {
    return RoomAllocationRequest.builder().criteria(Criteria.builder().roomId("12")
        .reservationIdList(List.of(ReservationIdList.builder().id("12345").build()))
        .hotelId("HOTEL_ID").updateRoomTypeCharged(false).roomNumberLocked(true).build()).build();
  }

  private RoomAllocationResponse mockRoomAllocationResponse() {
    return RoomAllocationResponse.builder()
        .links(List.of(RoomLinks.builder()
            .href("https://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com")
            .operationId("OPERATION_ID").build())).build();
  }

  private RoomAllocationResponseDto mockRoomAllocationResponseDto() {
    var link =
        new RoomLinksDto("https://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com", "",
            true,
            "", "OPERATION_ID");
    return new RoomAllocationResponseDto(List.of(link));
  }

  private HouseKeepingRoomStatusResponse mockFetchHouseKeepingRoomStatus() {
    return HouseKeepingRoomStatusResponse.builder()
        .housekeepingRoomInfo(
            HousekeepingRoomInfo.builder()
                .housekeepingRooms(
                    HousekeepingRooms.builder().hotelId("MANOLD").room(Collections.singletonList(
                        HouseKeepingRoom.builder().roomId("007")
                            .housekeeping(Housekeeping.builder().housekeepingRoomStatus(
                                HousekeepingRoomStatus.builder().housekeepingRoomStatusText("Clean")
                                    .build()).build()).build())).build())
                .build()).build();

  }

}
