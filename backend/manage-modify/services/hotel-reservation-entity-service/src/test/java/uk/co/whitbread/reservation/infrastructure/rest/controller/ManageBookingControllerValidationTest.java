package uk.co.whitbread.reservation.infrastructure.rest.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.co.whitbread.commons.exceptions.advice.GlobalExceptionHandler;
import uk.co.whitbread.reservation.domain.ports.primary.ManageBookingInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.ManageBookingController;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.BookingChannelRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.FindBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.ManageBookingResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SearchBookingsRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.SearchBookingsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.mapper.UdfsDomainMapper;

@ExtendWith(MockitoExtension.class)
class ManageBookingControllerValidationTest {

  private MockMvc mockMvc;

  @InjectMocks
  private ManageBookingController manageBookingController;

  @Mock
  private ManageBookingInPort manageBookingInPort;
  @Mock
  private ManageBookingResponseMapper manageBookingResponseMapper;
  @Mock
  private BookingChannelRequestMapper bookingChannelRequestMapper;
  @Mock
  private FindBookingResponseMapper findBookingResponseMapper;
  @Mock
  private FindBookingRequestMapper findBookingRequestMapper;
  @Mock
  private SearchBookingsRequestMapper searchBookingsRequestMapper;
  @Mock
  private SearchBookingsResponseMapper searchBookingsResponseMapper;
  @Mock
  private UdfsDomainMapper udfsDomainMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders
        .standaloneSetup(manageBookingController)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void getCancelInformation_whenHotelIdIsBlank_returns422AndDoesNotCallInPort() throws Exception {
    mockMvc.perform(get("/v1/reservations/cancel")
            .param("hotelId", "")
            .param("basketReference", "AQN-test-ref")
            .param("userDateTime", "2025-04-03T12:14:00+03:00")
            .param("channel", "PI")
            .param("subchannel", "WEB"))
        .andExpect(status().isUnprocessableEntity());

    verify(manageBookingInPort, never()).getManageBookingInformation(
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.anyBoolean(),
        org.mockito.ArgumentMatchers.any());
  }

  @Test
  void getCancelInformation_whenHotelIdIsAbsent_returns400AndDoesNotCallInPort() throws Exception {
    mockMvc.perform(get("/v1/reservations/cancel")
            .param("basketReference", "AQN-test-ref")
            .param("userDateTime", "2025-04-03T12:14:00+03:00")
            .param("channel", "PI")
            .param("subchannel", "WEB"))
        .andExpect(status().isUnprocessableEntity());

    verify(manageBookingInPort, never()).getManageBookingInformation(
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.any(),
        org.mockito.ArgumentMatchers.anyBoolean(),
        org.mockito.ArgumentMatchers.any());
  }
}
