package uk.co.whitbread.infrastructure.rest.client.groupbooking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.domain.ports.secondary.GroupBookingOutPort;
import uk.co.whitbread.infrastructure.rest.client.Dynamics365Client;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.groupbooking.mapper.GroupBookingResponseMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class GroupBookingOutPortImpl implements GroupBookingOutPort {

  public static final String INCIDENT_ID_HEADER = "OData-EntityId";
  private final Dynamics365Client dynamics365Client;
  private final GroupBookingRequestMapper groupBookingRequestMapper;
  private final GroupBookingResponseMapper groupBookingResponseMapper;

  @Override
  public GroupBookingResponse createGroupBooking(GroupBookingRequest request) {
    log.debug("Entered createGroupBooking with groupBookingRequest={}", request);

    var dynamicsRequest = groupBookingRequestMapper.toDynamicsDto(request);
    var response = dynamics365Client.createIncident(dynamicsRequest);
    var incidentId = extractIncidentId(response.getHeaders().getFirst(INCIDENT_ID_HEADER));

    return groupBookingResponseMapper.toModel(dynamics365Client.retrieveIncident(incidentId));
  }

  private String extractIncidentId(String header) {
    if (header != null) {
      int startIndex = header.lastIndexOf('(');
      int endIndex = header.lastIndexOf(')');

      return header.substring(startIndex + 1, endIndex);
    }
    return null;
  }

}
