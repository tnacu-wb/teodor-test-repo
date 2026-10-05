package uk.co.whitbread.ohip.infrastructure.rest.client.checkin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_CHECKIN_DETAILS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_UPDATE_COMMENTS_RESERVATION_EXCEPTION;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentReservationIdList;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentText;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskChangeReservation;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskComment;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskComments;
import uk.co.whitbread.ohip.domain.model.checkin.in.KioskReservation;
import uk.co.whitbread.ohip.domain.model.checkin.in.Reservation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.exception.CheckInException;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.mapper.UpdateReservationCommentOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.checkin.ohip.OhipCheckInClient;

@ExtendWith(MockitoExtension.class)
class CheckInOutPortImplTest {

  @Spy
  @InjectMocks
  private CheckInOutPortImpl checkInOutPort;

  @Mock
  private OhipCheckInClient checkInClient;

  @Mock
  private UpdateReservationCommentOhipMapper updateReservationCommentOhipMapper;

  @Test
  void getCheckInResponse__ShouldReturnOK() {
    //Arrange
    var request = mockCheckInRequest();
    when(checkInClient.getCheckInResponse(any(), anyString(), anyString())).thenReturn(
        new CheckInResponse());

    //Act
    var checkInResponse = checkInOutPort.getCheckInResponse(request, "HOTEL_ID", "RSV_ID");

    //Assert
    assertThat(checkInResponse, notNullValue());
  }

  @Test
  void updateReservationComment__ShouldReturnOK() {
    //Arrange
    when(updateReservationCommentOhipMapper.toModel(any(), any(), any())).thenReturn(
        mockUpdateReservationDetails());
    //Act
    checkInOutPort.updateReservationComment("1234", "MANOLD", null);

    //Assert
    verify(checkInOutPort, times(1)).updateReservationComment("1234", "MANOLD", null);
  }

  @Test
  void updateReservationComment__shouldThrowException() {
    String error = "Error while trying to update Comments to reservation";
    doThrow(
        new CheckInException(OHIP_UPDATE_COMMENTS_RESERVATION_EXCEPTION, error)).when(
            checkInClient)
        .sendKioskChangeReservationRequest(anyString(), anyString(), any());
    // Act
    CheckInException exception = Assertions
        .assertThrows(CheckInException.class, () ->
            checkInOutPort.updateReservationComment("1234", "HOTEL_ID", null)
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  private KioskChangeReservation mockUpdateReservationDetails() {
    KioskChangeReservation changeReservation = new KioskChangeReservation();
    changeReservation.setReservations(
        Collections.singletonList(new KioskReservation()));
    KioskComments commentInfoType = new KioskComments();
    commentInfoType.setComment(new KioskComment());
    commentInfoType.getComment().setType("CARREG");
    commentInfoType.getComment().setCommentTitle("Car Registration");
    commentInfoType.getComment().setNotificationLocation("RESERVATION");
    commentInfoType.getComment().setInternal(false);
    commentInfoType.getComment().setText(new CommentText());
    commentInfoType.getComment().getText().setValue("TN45BR8932");
    changeReservation.getReservations().get(0)
        .setComments(Collections.singletonList(commentInfoType));
    CommentReservationIdList commentReservationIdList = new CommentReservationIdList();
    commentReservationIdList.setType("Reservation");
    commentReservationIdList.setId("1234");
    changeReservation.getReservations().get(0)
        .setReservationIdList(Collections.singletonList(commentReservationIdList));
    changeReservation.getReservations().get(0).setHotelId("MANOLD");
    return changeReservation;
  }

  @Test
  void getCheckInResponse__shouldThrowException() {
    // Arrange
    String error = "Error while trying to get checkIn details";
    var request = new CheckInRequest();
    Mockito.when(checkInClient.getCheckInResponse(any(), anyString(), anyString()))
        .thenThrow(
            new CheckInException(OHIP_GET_CHECKIN_DETAILS_EXCEPTION, error));
    // Act
    CheckInException exception = Assertions
        .assertThrows(CheckInException.class, () ->
            checkInOutPort.getCheckInResponse(request, "HOTEL_ID", "RSV_ID")
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  private CheckInRequest mockCheckInRequest() {
    return CheckInRequest.builder().reservation(Reservation.builder().roomId("123").build())
        .fetchReservationInstruction(List.of("checkInResponse")).build();
  }

}
