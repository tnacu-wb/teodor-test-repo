package uk.co.whitbread.ohip.domain.logic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.Reservation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.CheckInOutPort;

@ExtendWith(MockitoExtension.class)
class CheckInInPortImplTest {

  @InjectMocks
  private CheckInInPortImpl checkInInPort;

  @Mock
  private CheckInOutPort checkInOutPort;

  @Test
  void getCheckInResponse__ShouldReturnOK() {
    //Arrange
    var request = mockCheckInRequest();
    when(checkInOutPort.getCheckInResponse(any(), anyString(), anyString())).thenReturn(
        new CheckInResponse());

    //Act
    var response = checkInInPort.getCheckInResponse(request, "HOTELID", "123456");

    //Assert
    assertThat(response, notNullValue());

    verifyNoMoreInteractions(checkInOutPort);

  }

  private CheckInRequest mockCheckInRequest(){
    return CheckInRequest.builder().reservation(Reservation.builder().roomId("123").build())
        .fetchReservationInstruction(List.of("checkInResponse")).build();
  }

}
