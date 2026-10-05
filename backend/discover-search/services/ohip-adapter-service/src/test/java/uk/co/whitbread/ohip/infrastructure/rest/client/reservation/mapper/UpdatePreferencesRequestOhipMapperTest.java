package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreferencesCollection;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UpdatePreferencesRequestOhipMapperImpl.class)
class UpdatePreferencesRequestOhipMapperTest {

  @Autowired
  private UpdatePreferencesRequestOhipMapper mapper;

  @Test
  void toDto_singleReservation() {
    final String hotelId = "hotelId";
    final String reservationId = "reservationId";
    final String preferenceType = "preferenceType";
    final String preferenceValue = "preferenceValue";
    final String reservationType = "Reservation";

    var requestPreference = PreferencesCollection.builder()
        .preferenceType(preferenceType)
        .preferences(List.of(preferenceValue))
        .build();
    var preferencesRequest = ReservationPreferencesRequest.builder()
        .hotelId(hotelId)
        .reservationsIds(List.of(reservationId))
        .preferencesCollections(List.of(requestPreference))
        .build();

    var dto = mapper.toDto(preferencesRequest, reservationId);

    assertNotNull(dto);
    assertEquals(1, dto.getReservations().size());

    var reservation = dto.getReservations().get(0);
    assertEquals(hotelId, reservation.getHotelId());
    assertEquals(1, reservation.getReservationIdList().size());

    var uniqueId = reservation.getReservationIdList().get(0);
    assertEquals(reservationId, uniqueId.getId());
    assertEquals(reservationType, uniqueId.getType());
    assertEquals(1, reservation.getPreferenceCollection().size());

    var preferenceCollection = reservation.getPreferenceCollection().get(0);
    assertEquals(preferenceType, preferenceCollection.getPreferenceType());
    assertEquals(1, preferenceCollection.getPreference().size());

    var preference = preferenceCollection.getPreference().get(0);
    assertEquals(preferenceValue, preference.getPreferenceValue());
  }

  @Test
  void toDto_multipleReservations() {
    final String hotelId = "hotelId";
    final String reservationId1 = "reservationId1";
    final String reservationId2 = "reservationId2";
    final String preferenceType = "preferenceType";
    final String preferenceValue = "preferenceValue";
    final String reservationType = "Reservation";

    var requestPreference = PreferencesCollection.builder()
        .preferenceType(preferenceType)
        .preferences(List.of(preferenceValue))
        .build();
    var preferencesRequest = ReservationPreferencesRequest.builder()
        .hotelId(hotelId)
        .reservationsIds(List.of(reservationId1, reservationId2))
        .preferencesCollections(List.of(requestPreference))
        .build();

    var dto = mapper.toDto(preferencesRequest, reservationId1);

    assertNotNull(dto);
    assertEquals(1, dto.getReservations().size());

    var reservation = dto.getReservations().get(0);
    assertEquals(hotelId, reservation.getHotelId());
    assertEquals(1, reservation.getReservationIdList().size());

    var uniqueId = reservation.getReservationIdList().get(0);
    assertEquals(reservationId1, uniqueId.getId());
    assertEquals(reservationType, uniqueId.getType());
    assertEquals(1, reservation.getPreferenceCollection().size());

    var preferenceCollection = reservation.getPreferenceCollection().get(0);
    assertEquals(preferenceType, preferenceCollection.getPreferenceType());
    assertEquals(1, preferenceCollection.getPreference().size());

    var preference = preferenceCollection.getPreference().get(0);
    assertEquals(preferenceValue, preference.getPreferenceValue());
  }
}
