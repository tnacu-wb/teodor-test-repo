package uk.co.whitbread.booking.infrastructure.rest.controller.booking.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.booking.domain.model.information.in.CancelBookingRequest;
import uk.co.whitbread.booking.domain.model.information.out.CancelBookingResponse;
import uk.co.whitbread.booking.domain.model.upcoming.in.UpcomingBookingRequest;
import uk.co.whitbread.booking.domain.model.upcoming.out.UpcomingBookingsResponse;
import uk.co.whitbread.booking.domain.ports.primary.BookingInPort;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.BookingController;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.mapper.BookingMapper;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.channel.BookingChannelDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.in.CancelBookingRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out.CancelBookingResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.in.UpcomingBookingsRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.upcoming.out.UpcomingBookingsResponseDto;
import uk.co.whitbread.booking.domain.model.invoice.DownloadBookingInvoicesRequest;
import uk.co.whitbread.booking.domain.model.invoice.InvoiceDownloadResponse;
import uk.co.whitbread.booking.domain.model.invoice.Language;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.LanguageDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.in.DownloadBookingInvoicesRequestDto;
import uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.invoice.out.InvoiceDownloadResponseDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @InjectMocks
    BookingController bookingControllerUnderTest;
    @Mock
    BookingMapper bookingMapper;
    @Mock
    BookingInPort bookingInPort;
    @Mock
    AuthenticatedUserService authenticatedUserService;

    @Test
    void cancelBooking_bookingReferenceInRequestIsTheSameWithBookingReferenceInResponse() {
        //Arrange
        CancelBookingResponseDto cancelBookingResponseDto = CancelBookingResponseDto
                .builder().bookingReference("AQN086278aa9").build();

        when(bookingMapper.toModel(any(CancelBookingRequestDto.class)))
                .thenReturn(CancelBookingRequest.builder().bookingReference("AQN086278aa9").build());
        when(bookingInPort.cancelBooking(any(CancelBookingRequest.class)))
                .thenReturn(new CancelBookingResponse());
        when(bookingMapper.toDto(any(CancelBookingResponse.class), any(String.class)))
                .thenReturn(cancelBookingResponseDto);

        //Act
        final ResponseEntity<CancelBookingResponseDto> response =
                bookingControllerUnderTest.cancelBooking(new BookingChannelDto(), new CancelBookingRequestDto());

        //Assert
        assertEquals(cancelBookingResponseDto.getBookingReference(), response.getBody().getBookingReference());

    }

    @Test
    void getUpcomingBookingsForInnBusiness_WhenCalled_ThenMappersAndPortsAreVerified() {
        var expectedAuthorization = "Bearer token==";
        var upcomingBookingsRequestDto = UpcomingBookingsRequestDto.builder()
              .country("gb")
              .language("en")
              .build();
        var upcomingBookingRequest = UpcomingBookingRequest.builder()
              .country("gb")
              .language("en")
              .build();
        var upcomingBookingsResponse = UpcomingBookingsResponse.builder()
              .bookings(2)
              .stays(3)
              .build();
        var upcomingBookingsResponseDto = UpcomingBookingsResponseDto.builder()
              .bookings(2)
              .stays(3)
              .build();
        when(bookingMapper.toModel(upcomingBookingsRequestDto))
              .thenReturn(upcomingBookingRequest);
        when(bookingMapper.toDto(upcomingBookingsResponse))
              .thenReturn(upcomingBookingsResponseDto);
        when(bookingInPort.getUpcomingBookings(upcomingBookingRequest, expectedAuthorization))
                .thenReturn(upcomingBookingsResponse);

        var response = bookingControllerUnderTest.getUpcomingBookingsForInnBusiness(
              expectedAuthorization, upcomingBookingsRequestDto);

        assertEquals(upcomingBookingsResponseDto, response.getBody());
        verify(bookingMapper).toModel(upcomingBookingsRequestDto);
        verify(bookingMapper).toDto(upcomingBookingsResponse);
        verify(bookingInPort).getUpcomingBookings(upcomingBookingRequest, expectedAuthorization);
    }

    @Test
    void downloadBookingInvoices_WhenCalled_ThenMappersAndPortsAreVerified() {
        var expectedAuthorization = "Bearer token==";
        var requestDto = new DownloadBookingInvoicesRequestDto(
              java.util.List.of("GAN9859956"),
              LanguageDto.EN,
              DownloadBookingInvoicesRequestDto.BookingChannelType.PI,
              "Online",
              "PI");

        var domainRequest = DownloadBookingInvoicesRequest.builder()
              .bookingRef(java.util.List.of("GAN9859956"))
              .lang(Language.EN)
              .channel("PI")
              .subChannel("Online")
              .hotelBrand("PI")
              .build();

        var domainResponse = InvoiceDownloadResponse.builder()
              .invoices(java.util.List.of())
              .build();

        var responseDto = new InvoiceDownloadResponseDto(java.util.List.of());

        when(bookingMapper.toModel(requestDto)).thenReturn(domainRequest);
        when(bookingInPort.downloadBookingInvoices(expectedAuthorization, domainRequest)).thenReturn(domainResponse);
        when(bookingMapper.toDto(domainResponse)).thenReturn(responseDto);
        when(authenticatedUserService.isUserAuthenticated()).thenReturn(true);

        var response = bookingControllerUnderTest.downloadBookingInvoices(expectedAuthorization, requestDto);

        assertEquals(responseDto, response.getBody());
        verify(bookingMapper).toModel(requestDto);
        verify(bookingInPort).downloadBookingInvoices(expectedAuthorization, domainRequest);
        verify(bookingMapper).toDto(domainResponse);
    }
}
