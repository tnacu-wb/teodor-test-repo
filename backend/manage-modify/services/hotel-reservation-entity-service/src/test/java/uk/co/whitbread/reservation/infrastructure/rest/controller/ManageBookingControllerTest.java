package uk.co.whitbread.reservation.infrastructure.rest.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.reservation.domain.logic.utils.AemLabelKeyConstants;
import uk.co.whitbread.reservation.domain.model.in.BookingChannel;
import uk.co.whitbread.reservation.domain.model.in.CiolStatusEnum;
import uk.co.whitbread.reservation.domain.model.in.FindBookingRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationUdfsRequest;
import uk.co.whitbread.reservation.domain.model.out.FindBookingResponse;
import uk.co.whitbread.reservation.domain.model.out.ManageBookingResponse;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.ManageBookingController;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingChannelRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ManageBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UdfsDomainMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingChannelDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingKioskRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.FindBookingRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.GetCancelInformationRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UdfsRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.FindBookingResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out.ManageBookingResponseDto;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
class ManageBookingControllerTest {

  @InjectMocks
  ManageBookingController manageBookingController;
  @Mock
  ManageBookingResponseMapper manageBookingResponseMapper;
  @Mock
  private ManageBookingInPort manageBookingInPort;
  @Mock
  private FindBookingResponseMapper findBookingResponseMapper;
  @Mock
  private FindBookingRequestMapper findBookingRequestMapper;
  @Mock
  BookingChannelRequestMapper bookingChannelRequestMapper;
  @Mock
  UdfsDomainMapper udfsDomainMapper;

  @Test
  void cancelPolicies__isCancellable() {
    //Arrange
    when(manageBookingInPort.getManageBookingInformation("TestHotelId",
                    "basket", "2022-09-15T07:47:19 00:00", null, createBookingChannel(), true, null))
            .thenReturn(ManageBookingResponse.builder()
                    .isCancellable(Boolean.TRUE)
                    .isAmendable(Boolean.FALSE)
                    .isRuleCompliant(Boolean.TRUE)
                    .build());
    when(manageBookingResponseMapper.toDto(ManageBookingResponse.builder()
                    .isCancellable(Boolean.TRUE)
                    .isAmendable(Boolean.FALSE)
                    .isRuleCompliant(Boolean.TRUE)
                    .build())).
            thenReturn(new ManageBookingResponseDto(true, false, false, AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C111_CANCEL_A2C, false,false, false,""));

    //act
    final var cancelResponse = manageBookingInPort.getManageBookingInformation(
            "TestHotelId",
            "basket", "2022-09-15T07:47:19 00:00", null, createBookingChannel(), true, null);
    final var reservationResponseDto = manageBookingResponseMapper.toDto(cancelResponse);
    final ResponseEntity<ManageBookingResponseDto> response =
            manageBookingController.getManageBookingInformation(
                    createGetCancelInformationRequest("TestHotelId", "basket", "2022-09-15T07:47:19 00:00"),
                    createBookingChannelDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());

  }

  @Test
  void cancelPolicies_distributionChannel_isCancellable() {
    //Arrange
    when(manageBookingInPort.getManageBookingInformation("TestHotelId",
                    "basket", "2024-04-15T07:47:19 00:00", null, createDistributionChannel(), true, null))
            .thenReturn(ManageBookingResponse.builder()
                    .isCancellable(Boolean.TRUE)
                    .isAmendable(Boolean.FALSE)
                    .isRuleCompliant(Boolean.TRUE)
                    .build());
    when(manageBookingResponseMapper.toDto(ManageBookingResponse.builder()
                    .isCancellable(Boolean.TRUE)
                    .isAmendable(Boolean.FALSE)
                    .isRuleCompliant(Boolean.TRUE)
                    .build())).
            thenReturn(new ManageBookingResponseDto(true, false, false, AemLabelKeyConstants.CCUI_MANAGE_BOOKING_ERROR_C111_CANCEL_A2C, false,false, false,""));

    //act
    final var cancelResponse = manageBookingInPort.getManageBookingInformation(
            "TestHotelId",
            "basket", "2024-04-15T07:47:19 00:00", null, createDistributionChannel(), true, null);
    final var reservationResponseDto = manageBookingResponseMapper.toDto(cancelResponse);
    final ResponseEntity<ManageBookingResponseDto> response =
            manageBookingController.getManageBookingInformation(
                    createGetCancelInformationRequest("TestHotelId", "basket", "2024-04-15T07:47:19 00:00"),
                    createBookingDistributionDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());

  }

  @Test
  void findBooking__bookingIsFound() {
    //Arrange
    when(manageBookingInPort.findBooking(getFindBookingRequest(), createBookingChannel()))
            .thenReturn(getFindBookingResponse());
    when(findBookingRequestMapper.toModel(getFindBookingRequestDto())).
            thenReturn(getFindBookingRequest());
    when(findBookingResponseMapper.toDto(getFindBookingResponse())).
            thenReturn(getFindBookingResponseDto());
    when(bookingChannelRequestMapper.toModel(createBookingChannelDto()))
            .thenReturn(createBookingChannel());
    //act
    final ResponseEntity<FindBookingResponseDto> response =
            manageBookingController.findBooking(getFindBookingRequestDto(),
                    createBookingChannelDto());

    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("check hotel id",response.getBody().getHotelId(),"hotelId");
  }

  @Test
  void findBookingForKiosk__shouldCallInPortWithKioskChannel() {
      // Arrange
      FindBookingKioskRequestDto requestDto = FindBookingKioskRequestDto.builder()
              .resNo("BSKT123456")
              .build();

      FindBookingRequest expectedRequest = getFindBookingRequest();
      BookingChannel expectedChannel = BookingChannel.builder()
              .channel(BookingChannel.KIOSK_BOOKING_CHANNEL)
              .subchannel(BookingChannel.WEB_SUBCHANNEL)
              .build();
      when(findBookingRequestMapper.toModel(requestDto))
              .thenReturn(expectedRequest);
      when(manageBookingInPort.findBooking(expectedRequest, expectedChannel))
              .thenReturn(getFindBookingResponse());
      when(findBookingResponseMapper.toDto(getFindBookingResponse()))
              .thenReturn(getFindBookingResponseDto());

      // Act
      manageBookingController.findBookingForKiosk(requestDto);

      // Assert
      verify(manageBookingInPort).findBooking(
              eq(expectedRequest),
              argThat(channel ->
                      BookingChannel.KIOSK_BOOKING_CHANNEL.equals(channel.getChannel()) &&
                      BookingChannel.WEB_SUBCHANNEL.equals(channel.getSubchannel())
              )
      );
  }

  @Test
  void findBookingForKiosk__shouldReturnOkStatus() {
      // Arrange
      FindBookingKioskRequestDto requestDto = FindBookingKioskRequestDto.builder()
              .resNo("BSKT999999")
              .build();
      when(findBookingRequestMapper.toModel(requestDto))
              .thenReturn(getFindBookingRequest());
      when(manageBookingInPort.findBooking(any(), any()))
              .thenReturn(getFindBookingResponse());
      when(findBookingResponseMapper.toDto(any()))
              .thenReturn(getFindBookingResponseDto());

      // Act
      ResponseEntity<FindBookingResponseDto> response =
              manageBookingController.findBookingForKiosk(requestDto);

      // Assert
      assertEquals("Status should be 200", 200, response.getStatusCode().value());
      assertNotNull(response.getBody());
  }

  @Test
  void updateUdfc20_shouldConvertDtoAndCallInPort() {
      // Arrange
      UdfsRequestDto dto = UdfsRequestDto.builder()
                .reservationIds(Set.of("RES1"))
                .hotelId("TEST_HOTEL")
                .ciolStatus(CiolStatusEnum.CIOL_COMPLETED)
                .build();

      UpdateReservationUdfsRequest domain = UpdateReservationUdfsRequest.builder()
                .reservationIds(Set.of("RES1"))
                .hotelId("TEST_HOTEL")
                .udfs(null)
                .build();

      when(udfsDomainMapper.toDomainModel(dto)).thenReturn(domain);

      // Act
      manageBookingController.updateUdfc20(dto);

      // Assert
      verify(udfsDomainMapper).toDomainModel(dto);
      verify(manageBookingInPort).updateUdfc20(domain);
  }

  private GetCancelInformationRequestDto createGetCancelInformationRequest(
          String hotelId, String basketReference, String userDateTime) {
    return GetCancelInformationRequestDto.builder()
            .hotelId(hotelId)
            .basketReference(basketReference)
            .userDateTime(userDateTime)
            .build();
  }

  private BookingChannel createBookingChannel() {
    BookingChannel bookingChannel = new BookingChannel();

    bookingChannel.setChannel("PI");

    return bookingChannel;
  }

  private BookingChannel createDistributionChannel() {
    BookingChannel bookingChannel = new BookingChannel();

    bookingChannel.setChannel("DISTR");

    return bookingChannel;
  }

  private BookingChannelDto createBookingChannelDto() {
    BookingChannelDto bookingChannelDto = new BookingChannelDto();

    bookingChannelDto.setChannel("PI");

    return bookingChannelDto;
  }

  private BookingChannelDto createBookingDistributionDto() {
    BookingChannelDto bookingChannelDto = new BookingChannelDto();

    bookingChannelDto.setChannel("DISTR");

    return bookingChannelDto;
  }

  private FindBookingRequest getFindBookingRequest() {
    return FindBookingRequest.builder().resNo("BSKT123456").arrivalDate("2023-12-01")
            .lastName("Caesar").country("gb")
            .language("en").build();
  }

  private FindBookingRequestDto getFindBookingRequestDto() {
    return FindBookingRequestDto.builder().resNo("BSKT123456").arrivalDate("2023-12-01")
            .lastName("Caesar")
            .country("gb").language("en").build();
  }

  private FindBookingResponse getFindBookingResponse() {
    return new FindBookingResponse("cookie-test", "Opera", "BSKT123456",
            "TST-16a014c5-d8a4-4418-8d15-2537660f08e9", "redirect.base", "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
            "30", "763746937","hotelId", "idContext");
  }

  private FindBookingResponseDto getFindBookingResponseDto() {
    return new FindBookingResponseDto("cookie-test", "Opera", "BSKT123456",
        "TST-16a014c5-d8a4-4418-8d15-2537660f08e9", "redirect.base",
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "30", "763746937", "hotelId", "idContext");
  }
}
