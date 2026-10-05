package uk.co.whitbread.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;
import uk.co.whitbread.domain.ports.primary.GroupBookingInPort;
import uk.co.whitbread.domain.ports.secondary.GroupBookingOutPort;

@Slf4j
@RequiredArgsConstructor
public class GroupBookingInPortImpl implements GroupBookingInPort {

  private final GroupBookingOutPort groupBookingOutPort;

  public GroupBookingResponse createGroupBooking(GroupBookingRequest request) {
    return groupBookingOutPort.createGroupBooking(request);
  }

}