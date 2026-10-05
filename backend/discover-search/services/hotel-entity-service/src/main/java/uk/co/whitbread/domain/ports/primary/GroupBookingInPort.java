package uk.co.whitbread.domain.ports.primary;

import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;

public interface GroupBookingInPort {

  GroupBookingResponse createGroupBooking(GroupBookingRequest createGroupBookingRequest);

}
