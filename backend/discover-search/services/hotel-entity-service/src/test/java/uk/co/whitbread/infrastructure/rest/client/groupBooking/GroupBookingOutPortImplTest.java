package uk.co.whitbread.infrastructure.rest.client.groupBooking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.infrastructure.rest.client.Dynamics365Client;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.GroupBookingOutPortImpl;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.exception.GroupBookingException;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.in.GroupBookingRequestDynamicsDto;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.model.out.GroupBookingResponseDynamicsDto;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class GroupBookingOutPortImplTest {

  @InjectMocks
  private GroupBookingOutPortImpl groupBookingOutPort;
  @Mock
  private GroupBookingRequestMapper groupBookingRequestMapper;
  @Mock
  private GroupBookingResponseMapper groupBookingResponseMapper;
  @Mock
  private Dynamics365Client dynamics365Client;
  @Mock
  private ResponseEntity responseEntity;
  @Mock
  private HttpHeaders responseHeader;
  private final String incidentIdHeader = "OData-EntityId";

  @Test
  void createGroupBooking() {
    //Arrange
    GroupBookingResponseDynamicsDto responseDynamicsDto = new GroupBookingResponseDynamicsDto();
    String header = "incidents(f56d0971-9149-ef11-bfe2-000d3aae8d3c)";
    GroupBookingRequest request = GroupBookingRequest.builder().build();
    GroupBookingResponse response = GroupBookingResponse.builder()
        .incidentId("CAS-23740-K4T1G9")
        .ticketNumber("2596c141-ed48-ef11-bfe2-000d3aae8d3c").build();

    when(groupBookingRequestMapper.toDynamicsDto(any())).thenReturn(new GroupBookingRequestDynamicsDto());
    when(dynamics365Client.createIncident(any())).thenReturn(responseEntity);
    when(responseEntity.getHeaders()).thenReturn(responseHeader);
    when(responseHeader.getFirst(incidentIdHeader)).thenReturn(header);
    when(dynamics365Client.retrieveIncident("f56d0971-9149-ef11-bfe2-000d3aae8d3c"))
        .thenReturn(responseDynamicsDto);
    when(groupBookingResponseMapper.toModel(responseDynamicsDto)).thenReturn(response);
    //Act
    GroupBookingResponse groupBookingResponse = groupBookingOutPort.createGroupBooking(request);

    //Assert
    assertNotNull(groupBookingResponse);
    assertThat(groupBookingResponse.getIncidentId(),
        is("CAS-23740-K4T1G9"));
    assertThat(groupBookingResponse.getTicketNumber(),
        is("2596c141-ed48-ef11-bfe2-000d3aae8d3c"));
  }
  @Test
  void createGroupBooking_headerNull__ShouldThrowException() {
    //Arrange
    GroupBookingRequest request = GroupBookingRequest.builder().build();

    when(groupBookingRequestMapper.toDynamicsDto(any())).thenReturn(new GroupBookingRequestDynamicsDto());
    when(dynamics365Client.createIncident(any())).thenReturn(responseEntity);
    when(responseEntity.getHeaders()).thenReturn(responseHeader);
    when(responseHeader.getFirst(incidentIdHeader)).thenReturn(null);

    // Act
    GroupBookingResponse groupBookingResponse = groupBookingOutPort.createGroupBooking(request);

    // Assert
    assertNull(groupBookingResponse);
  }

  @Test
  void createGroupBooking__ShouldThrowException() {
    //Arrange
    String error = "Error while trying to retrieve incident response";
    GroupBookingResponseDynamicsDto responseDynamicsDto = new GroupBookingResponseDynamicsDto();
    String header = "incidents(f56d0971-9149-ef11-bfe2-000d3aae8d3c)";
    GroupBookingRequest request = GroupBookingRequest.builder().build();

    when(groupBookingRequestMapper.toDynamicsDto(any())).thenReturn(new GroupBookingRequestDynamicsDto());
    when(dynamics365Client.createIncident(any())).thenReturn(responseEntity);
    when(responseEntity.getHeaders()).thenReturn(responseHeader);
    when(responseHeader.getFirst(incidentIdHeader)).thenReturn(header);
    when(dynamics365Client.retrieveIncident("f56d0971-9149-ef11-bfe2-000d3aae8d3c"))
        .thenReturn(responseDynamicsDto);
    when(groupBookingResponseMapper.toModel(any()))
        .thenThrow(new GroupBookingException("message", error, new Exception(), 1));

    //Act
    GroupBookingException exception = Assertions.assertThrows(GroupBookingException.class,
        () -> groupBookingOutPort.createGroupBooking(request));

    //Assert
    assertEquals(error, exception.getMessage());
  }
}
