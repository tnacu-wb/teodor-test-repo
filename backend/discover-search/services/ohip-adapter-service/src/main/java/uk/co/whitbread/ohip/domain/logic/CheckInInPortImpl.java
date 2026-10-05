package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.domain.ports.primary.CheckInInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.CheckInOutPort;

@RequiredArgsConstructor
@Slf4j
public class CheckInInPortImpl implements CheckInInPort {

  private final CheckInOutPort checkInOutPort;

  @Override
  public CheckInResponse getCheckInResponse(CheckInRequest checkInRequest, String hotelId,
      String reservationId) {
    return checkInOutPort.getCheckInResponse(checkInRequest, hotelId, reservationId);
  }

  @Override
  public void updateReservationComment(String reservationId, String hotelId,
      CommentDetails commentDetails) {
    checkInOutPort.updateReservationComment(reservationId, hotelId, commentDetails);
  }
}
