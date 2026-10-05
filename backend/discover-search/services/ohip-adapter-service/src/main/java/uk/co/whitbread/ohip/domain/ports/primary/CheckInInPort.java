package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;

public interface CheckInInPort {

  CheckInResponse getCheckInResponse(CheckInRequest checkInRequest, String hotelId,
      String reservationId);

  void updateReservationComment(String reservationId, String hotelId,
      CommentDetails commentDetails);

}
