package uk.co.whitbread.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.domain.ports.secondary.GroupBookingOutPort;

@ExtendWith(MockitoExtension.class)
class GroupBookingInPortImplTest {

  @Mock
  private GroupBookingOutPort groupBookingOutPort;
  @InjectMocks
  private GroupBookingInPortImpl groupBookingInPortImpl;

  @Test
  void createGroupBooking__ShouldReturnOk(){
    //Arrange
    GroupBookingRequest request = GroupBookingRequest.builder().build();
    GroupBookingResponse expectedResponse = GroupBookingResponse.builder()
        .incidentId("CAS-23740-K4T1G9")
        .ticketNumber("2596c141-ed48-ef11-bfe2-000d3aae8d3c").build();

    when(groupBookingOutPort.createGroupBooking(request)).thenReturn(expectedResponse);

    //Act
    GroupBookingResponse actualResponse = groupBookingInPortImpl.createGroupBooking(request);

    //Assert
    assertEquals(expectedResponse, actualResponse);
    verify(groupBookingOutPort).createGroupBooking(request);
  }

}
