package uk.co.whitbread.ohip.infrastructure.rest.controller.checkin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.checkin.in.CheckInRequest;
import uk.co.whitbread.ohip.domain.model.checkin.in.CommentDetails;
import uk.co.whitbread.ohip.domain.model.checkin.in.Reservation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CheckInResponse;
import uk.co.whitbread.ohip.domain.ports.primary.CheckInInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CheckInRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CheckInResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.mapper.CommentDetailsRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CheckInDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.in.CommentDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.checkin.model.out.CheckInResponseDto;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

  private static final String HOTEL_ID = "hotelId";
  private static final String RESERVATION_NUMBER = "reservationNumber";
  private static final String ROOM_ID = "roomId";

  @Spy
  @InjectMocks
  CheckInController checkInController;

  @Mock
  private CheckInRequestMapper checkInRequestMapper;

  @Mock
  private CheckInResponseMapper checkInResponseMapper;

  @Mock
  private CheckInInPort checkInInPort;

  @Mock
  private CommentDetailsRequestMapper commentDetailsRequestMapper;

  @Test
  void getCheckIn__ShouldReturnOk() {
    //Arrange
    CheckInDetailsDto requestDto = createCheckInDetailsDto();

    when(checkInRequestMapper.toCheckInInputRequestModel(requestDto)).thenReturn(
        createCheckInRequest());
    when(checkInInPort.getCheckInResponse(createCheckInRequest(), HOTEL_ID,
        RESERVATION_NUMBER)).thenReturn(
        createCheckInResponse());
    when(checkInResponseMapper.toDto(createCheckInResponse())).thenReturn(
        createAmendSummaryResponseDto());

    //act
    CheckInRequest request = checkInRequestMapper.toCheckInInputRequestModel(requestDto);
    CheckInResponse checkInResponse = checkInInPort.getCheckInResponse(request, HOTEL_ID,
        RESERVATION_NUMBER);
    CheckInResponseDto checkInResponseDto = checkInResponseMapper.toDto(checkInResponse);
    var response = checkInController.getCheckIn(requestDto);

    //Assert
    Assertions.assertNotNull(response);
  }

  @Test
  void updateCarRegistrationComment__ShouldReturnOk() {
    var commentDetailsDto = mockCommendDetailsDto();
    when(commentDetailsRequestMapper.toModel(any())).thenReturn(mockCommentDetails());

    checkInController.updateCarRegistrationComment("1234", "MANOLD", commentDetailsDto);

    verify(commentDetailsRequestMapper, times(1)).toModel(any());
    verify(checkInController, times(1)).updateCarRegistrationComment("1234", "MANOLD",
        commentDetailsDto);
  }

  private CommentDetails mockCommentDetails() {
    return CommentDetails.builder().commentTitle("Car Registration").textValue("TN45BR8932")
        .type("CARREG").build();
  }

  private CommentDetailsDto mockCommendDetailsDto() {
    return CommentDetailsDto.builder().commentTitle("Car Registration").textValue("TN45BR8932")
        .type("CARREG").build();
  }

  private CheckInDetailsDto createCheckInDetailsDto() {
    var requestDto = new CheckInDetailsDto();
    requestDto.setHotelId(HOTEL_ID);
    requestDto.setReservationNumber(RESERVATION_NUMBER);
    requestDto.setRoomId(ROOM_ID);
    return requestDto;
  }

  private CheckInRequest createCheckInRequest() {
    return CheckInRequest.builder().reservation(
            Reservation.builder().roomId(ROOM_ID).ignoreWarnings(true)
                .overrideAdvancePaymentValidation(true).build()).includeNotifications(true)
        .fetchReservationInstruction(
            Arrays.asList("ReservationDetail")).build();
  }

  private CheckInResponse createCheckInResponse() {
    return new CheckInResponse();
  }

  private CheckInResponseDto createAmendSummaryResponseDto() {
    return new CheckInResponseDto();
  }
}
