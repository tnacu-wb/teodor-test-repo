package uk.co.whitbread.domain.ports.secondary;

import uk.co.whitbread.domain.model.groupbooking.in.GroupBookingRequest;
import uk.co.whitbread.domain.model.groupbooking.out.GroupBookingResponse;

public interface GroupBookingOutPort {

  GroupBookingResponse createGroupBooking(GroupBookingRequest groupBookingRequest);

}
