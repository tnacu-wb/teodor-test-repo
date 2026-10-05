package uk.co.whitbread.ohip.infrastructure.rest.client.checkin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.CheckInOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.mapper.UpdateReservationCommentOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip.OhipCheckInClient;

@RequiredArgsConstructor
@Slf4j
public class CheckInOutPortImpl implements CheckInOutPort {

  private final OhipCheckInClient checkInClient;
  private final UpdateReservationCommentOhipMapper updateReservationCommentOhipMapper;

  @Override
  public CheckInResponse getCheckInResponse(CheckInRequest checkInRequest, String hotelId,
                                            String reservationId) {
    return checkInClient.getCheckInResponse(checkInRequest, hotelId, reservationId);
  }

  @Override
  public void updateReservationComment(String reservationId, String hotelId,
                                       CommentDetails commentDetails) {
    final var updateReservationCommentRequest = updateReservationCommentOhipMapper.toModel(
        reservationId, hotelId, commentDetails);
    checkInClient.sendKioskChangeReservationRequest(hotelId, reservationId,
        updateReservationCommentRequest);

  }

}

