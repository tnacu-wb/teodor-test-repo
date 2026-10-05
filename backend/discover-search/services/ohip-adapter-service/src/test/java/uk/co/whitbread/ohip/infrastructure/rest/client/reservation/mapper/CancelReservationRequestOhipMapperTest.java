package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils.createCancelReservationRequest;
import static uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils.createCancelReservationRequestWithOverride;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CancelReservationRequestOhipMapperImpl.class)
class CancelReservationRequestOhipMapperTest {

  @Autowired
  CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper;

  @Test
  void createRequestToCancelReservation_WithoutOverride__ShouldReturnOK() {
    //Act
    var cancelReservation = cancelReservationRequestOhipMapper.toCancelReservationModel(
        "123456", createCancelReservationRequest());

    //Assert
    assertEquals("MANOLD", cancelReservation.getReservations().get(0).getHotelId());
    assertThat(cancelReservation.getReservations().get(0).getReservationIdList(), hasSize(1));
    assertEquals("123456",
        cancelReservation.getReservations().get(0).getReservationIdList().get(0).getId());
    assertEquals("Reservation",
        cancelReservation.getReservations().get(0).getReservationIdList().get(0).getType());
    assertEquals("CXL", cancelReservation.getReason().getCode());
    assertEquals("Trip Cancelled", cancelReservation.getReason().getDescription());
  }

  @Test
  void createRequestToCancelReservation_WithOverride__ShouldReturnOK() {
    //Act
    var cancelReservation = cancelReservationRequestOhipMapper.toCancelReservationModel(
        "123456", createCancelReservationRequestWithOverride());

    //Assert
    assertEquals("MANOLD", cancelReservation.getReservations().get(0).getHotelId());
    assertThat(cancelReservation.getReservations().get(0).getReservationIdList(), hasSize(1));
    assertEquals("123456",
        cancelReservation.getReservations().get(0).getReservationIdList().get(0).getId());
    assertEquals("Reservation",
        cancelReservation.getReservations().get(0).getReservationIdList().get(0).getType());
    assertEquals("ILL", cancelReservation.getReason().getCode());
    assertEquals("Illness, Jane Doe, John Smith", cancelReservation.getReason().getDescription());
  }

}
