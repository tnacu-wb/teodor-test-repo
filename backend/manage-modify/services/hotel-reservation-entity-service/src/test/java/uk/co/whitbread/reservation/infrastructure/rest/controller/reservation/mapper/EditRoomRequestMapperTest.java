package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.EditRoomRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GuestAddressDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LeadGuestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomOccupancyDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EditRoomRequestMapper.class)
public class EditRoomRequestMapperTest {
  @Spy
  private EditRoomRequestMapper editRoomRequestMapper = Mappers.getMapper(EditRoomRequestMapper.class);

  @Test
  void toModel_shouldMapOk() {
    //Arrange
    EditRoomRequestDto editRoomRequestDto = buildEditRoomRequestDto();

    //Act
    var updateReservationsRequest = editRoomRequestMapper.toModel(editRoomRequestDto);

    //Assert
    assertEquals("12345", updateReservationsRequest.getReservations().get(0).getReservationId());
  }

  @Test
  void toModel_shouldMapNoAddressOk() {
    //Arrange
    EditRoomRequestDto editRoomRequestDto = buildEditRoomRequestDtoNoAddress();

    //Act
    var updateReservationsRequest = editRoomRequestMapper.toModel(editRoomRequestDto);

    //Assert
    assertEquals("12345", updateReservationsRequest.getReservations().get(0).getReservationId());
  }

  private EditRoomRequestDto buildEditRoomRequestDtoNoAddress() {
    return EditRoomRequestDto.builder()
        .reservationId("12345")
        .token("TOKEN")
        .roomType("DOUBLE")
        .leadGuest(buildLeadGuestNoAddress())
        .roomOccupancy(buildRoomOccupancy())
        .build();
  }

  private LeadGuestDto buildLeadGuestNoAddress() {
    return LeadGuestDto.builder()
        .title("Lady")
        .firstName("Prima")
        .lastName("Donna")
        .language("E")
        .emailAddress("prima.donna@mailinator.com")
        .build();
  }

  private EditRoomRequestDto buildEditRoomRequestDto() {
    return EditRoomRequestDto.builder()
        .reservationId("12345")
        .token("TOKEN")
        .roomType("DOUBLE")
        .leadGuest(buildLeadGuest())
        .roomOccupancy(buildRoomOccupancy())
        .build();
  }

  private RoomOccupancyDto buildRoomOccupancy() {
    return RoomOccupancyDto.builder()
        .adultsNumber(2)
        .childrenNumber(1)
        .build();
  }

  private LeadGuestDto buildLeadGuest() {
    return LeadGuestDto.builder()
        .title("Lady")
        .firstName("Prima")
        .lastName("Donna")
        .language("E")
        .emailAddress("prima.donna@mailinator.com")
        .address(buildGuestAddress())
        .build();
  }

  private GuestAddressDto buildGuestAddress() {
    return GuestAddressDto.builder()
        .addressLine1("First Line")
        .addressLine2("Line 2")
        .cityName("Big Smoke")
        .postalCode("PO5 TA1")
        .countryCode("GB")
        .build();
  }
}
