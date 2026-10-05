package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UpdateReservationAttachedProfilesRequestOhipMapperImpl.class)
class UpdateReservationAttachedProfilesRequestOhipMapperTest {

  @Autowired
  private UpdateReservationAttachedProfilesRequestOhipMapper updateReservationAttachedProfilesRequestOhipMapper;

  @Test
  void createUpdateProfilesReservationRequest__ShouldReturnOK() {
    //Arrange
    var hotelId = "LONEUS";
    var reservationId = "123456";
    var bookerProfileId = "123457";
    var companyProfileId = "123458";

    //Act
    var changeReservationProfilesRequest =
        updateReservationAttachedProfilesRequestOhipMapper.toDto(hotelId, reservationId, bookerProfileId,
            companyProfileId);

    //Assert
    assertNotNull(changeReservationProfilesRequest);
    assertEquals("LONEUS", changeReservationProfilesRequest.getReservations().get(0).getHotelId());
    assertEquals("123456",
        changeReservationProfilesRequest.getReservations().get(0).getReservationIdList().get(0).getId());
    assertEquals(2,
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .size());
    assertEquals("123457",
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .get(0).getProfileIdList().get(0).getId());
    assertEquals("123458",
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .get(1).getProfileIdList().get(0).getId());
  }

  @Test
  void createUpdateProfilesNoCompanyNameReservationRequest__ShouldReturnOK() {
    //Arrange
    var hotelId = "LONEUS";
    var reservationId = "123456";
    var bookerProfileId = "123457";

    //Act
    var changeReservationProfilesRequest =
        updateReservationAttachedProfilesRequestOhipMapper.toDto(hotelId, reservationId, bookerProfileId, null);

    //Assert
    assertNotNull(changeReservationProfilesRequest);
    assertEquals("LONEUS", changeReservationProfilesRequest.getReservations().get(0).getHotelId());
    assertEquals("123456",
        changeReservationProfilesRequest.getReservations().get(0).getReservationIdList().get(0).getId());
    assertEquals(2,
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .size());
    assertEquals("123457",
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .get(0).getProfileIdList().get(0).getId());
    assertNull(
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .get(1).getProfileIdList());
    assertEquals(ResProfileTypeType.COMPANY,
        changeReservationProfilesRequest.getReservations().get(0).getReservationProfiles().getReservationProfile()
            .get(1).getReservationProfileType());
  }

}
