package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationGuestRequestOhipMapperImpl.class)
class ReservationGuestRequestOhipMapperTest {

  @Autowired
  ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper;

  @Test
  void reservationGuestRequestToCreateReservationGuest__ShouldReturnOK() {
    //Arrange
    ReservationGuestRequest reservationGuestRequest = ReservationTestUtils.mockReservationGuestRequest();
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuests();
    final var bookerProfileId = "123456";
    final var companyProfileId = "234567";
    //Act
    var createReservationGuest =
        reservationGuestRequestOhipMapper.toDto(reservationGuestRequest, stayingGuest, bookerProfileId,
            companyProfileId);

    //Assert
    assertEquals("LONEUS", createReservationGuest.getReservations().get(0).getHotelId());
    assertEquals("LEI", createReservationGuest.getReservations().get(0).getAdditionalGuestInfo()
        .getPurposeOfStay());
    assertNull(
            createReservationGuest.getReservations().get(0).getReservationGuests().get(0).getProfileInfo().getProfile());
    assertEquals("1234",
            createReservationGuest.getReservations().get(0).getReservationGuests().get(0).getProfileInfo()
                    .getProfileIdList().get(0).getId());
    assertEquals(2,
            createReservationGuest.getReservations().get(0).getReservationProfiles().getReservationProfile().size());
    assertEquals("UDFC14",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(0).getName());
    assertEquals("YN",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(0).getValue());
    assertEquals("UDFC36",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(1).getName());
    assertEquals("TDE13849",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(1).getValue());
  }

  @Test
  void reservationGuestRequestToCreateReservationGuestSameAsBooker__ShouldReturnOK() {
    //Arrange
    ReservationGuestRequest reservationGuestRequest = ReservationTestUtils.mockReservationGuestRequest();
    StayingGuest stayingGuest = ReservationTestUtils.mockReservationGuestsSameAsBooker();
    final var bookerProfileId = "123456";
    final var companyProfileId = "234567";
    //Act
    var createReservationGuest =
        reservationGuestRequestOhipMapper.toDto(reservationGuestRequest, stayingGuest, bookerProfileId,
            companyProfileId);

    //Assert
    assertEquals("LONEUS", createReservationGuest.getReservations().get(0).getHotelId());
    assertEquals("LEI", createReservationGuest.getReservations().get(0).getAdditionalGuestInfo()
        .getPurposeOfStay());
    assertNull(
        createReservationGuest.getReservations().get(0).getReservationGuests().get(0).getProfileInfo().getProfile());
    assertEquals("123456",
        createReservationGuest.getReservations().get(0).getReservationGuests().get(0).getProfileInfo()
            .getProfileIdList().get(0).getId());
    assertEquals("UDFC14",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(0).getName());
    assertEquals("YN",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(0).getValue());
    assertEquals("UDFC36",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(1).getName());
    assertEquals("TDE13849",
        createReservationGuest.getReservations().get(0).getUserDefinedFields().getCharacterUDFs().get(1).getValue());
  }
}
