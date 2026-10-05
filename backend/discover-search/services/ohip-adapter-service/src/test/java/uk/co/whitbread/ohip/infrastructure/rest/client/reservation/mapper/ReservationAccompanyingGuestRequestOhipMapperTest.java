package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertAreaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationAccompanyingGuestRequestOhipMapperImpl.class)
class ReservationAccompanyingGuestRequestOhipMapperTest {

  @Autowired
  ReservationAccompanyingGuestRequestOhipMapper mapper;

  @Test
  void givenValidInput_whenMappingToDto_thenReservationWithGuestsCreated() {
    // Given
    String reservationId = "12345";
    Map<String, Boolean> profileIds = new HashMap<>();
    profileIds.put("profile1", true);
    profileIds.put("profile2", false);

    // When
    ChangeReservation changeReservation = mapper.toDto(reservationId, profileIds);

    // Then
    assertReservationCreatedSuccessfully(changeReservation, reservationId, profileIds);
  }

  @Test
  void givenNullInput_whenMappingToDto_thenReturnsEmptyList() {
    // When
    ChangeReservation result = mapper.toDto(null, null);

    // Then
    assertNull(result);
  }

  @Test
  void givenInvalidProfileId_whenMappingToDto_thenThrowsException() {
    // Given
    String reservationId = "12345";
    Map<String, Boolean> profileIds = new HashMap<>();
    profileIds.put(null, true);

    // When
    assertThrows(IllegalArgumentException.class, () -> mapper.toDto(reservationId, profileIds));
  }

  private void assertReservationCreatedSuccessfully(ChangeReservation changeReservation,
      String reservationId,
      Map<String, Boolean> profileIds) {
    assertNotNull(changeReservation);
    List<HotelReservationInstructionType> reservations = changeReservation.getReservations();
    assertEquals(1, reservations.size());

    HotelReservationInstructionType reservation = reservations.get(0);
    assertNotNull(reservation.getReservationIdList());
    assertEquals(1, reservation.getReservationIdList().size());

    UniqueIDType reservationUniqueIdType = reservation.getReservationIdList().get(0);
    assertEquals(UniqueIdTypeEnumDto.RESERVATION_TYPE.value(), reservationUniqueIdType.getType());
    assertEquals(reservationId, reservationUniqueIdType.getId());

    List<ResGuestType> reservationGuests = reservation.getReservationGuests();
    assertEquals(profileIds.size(), reservationGuests.size());

    for (Map.Entry<String, Boolean> entry : profileIds.entrySet()) {
      String profileId = entry.getKey();
      Boolean primary = entry.getValue();

      ResGuestType guest = findGuestByProfileId(reservationGuests, profileId);
      assertNotNull(guest);
      assertEquals(primary, guest.getPrimary());
    }
  }

  @Test
  void testInjectReservationAlertDetails_Success() {
    // Given
    String hotelId = "FRAMTI";
    String reservationId = "1927762";
    String description = "Do not print registration card, pre check-in completed";

    // When
    List<HotelReservationInstructionType> result = mapper.injectReservationAlertDetails(hotelId,
        reservationId, description, "");

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());
    HotelReservationInstructionType hotelReservation = result.get(0);
    assertEquals(hotelId, hotelReservation.getHotelId());
    assertEquals(1, hotelReservation.getReservationIdList().size());
    assertEquals(reservationId, hotelReservation.getReservationIdList().get(0).getId());
    assertEquals(UniqueIdTypeEnumDto.RESERVATION_TYPE.value(),
        hotelReservation.getReservationIdList().get(0).getType());
    assertEquals(1, hotelReservation.getAlerts().size());
    AlertType alert = hotelReservation.getAlerts().get(0);
    assertEquals(AlertAreaType.CHECKIN, alert.getArea());
    assertEquals("RESERVATION", alert.getCode());
    assertEquals(description, alert.getDescription());
    assertEquals(true, alert.getScreenNotification());
    assertEquals(false, alert.getPrinterNotification());
  }

  @ParameterizedTest
  @MethodSource("invalidAlertInputs")
  void testInjectReservationAlertDetails_InvalidInputs(String hotelId, String reservationId,
      String description, String alertId) {
    // When & Then
    assertThrows(IllegalArgumentException.class, () -> {
      mapper.injectReservationAlertDetails(hotelId, reservationId, description, alertId);
    });
  }

  static Stream<Arguments> invalidAlertInputs() {
    return Stream.of(
        Arguments.of(null, "456", "Test Description", ""),
        Arguments.of("123", "", "Test Description", "")
    );
  }

  private ResGuestType findGuestByProfileId(List<ResGuestType> guests, String profileId) {
    return guests.stream()
        .filter(guest -> guest.getProfileInfo().getProfileIdList().get(0).getId().equals(profileId))
        .findFirst()
        .orElse(null);
  }
}