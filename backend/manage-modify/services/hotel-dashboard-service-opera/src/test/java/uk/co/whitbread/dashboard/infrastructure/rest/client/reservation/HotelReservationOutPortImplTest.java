package uk.co.whitbread.dashboard.infrastructure.rest.client.reservation;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.model.in.FindBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper.FindBookingResponseMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.mapper.ManageBookingInfoMapper;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.FindBookingResponseDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.model.ManageBookingResponseDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.reservation.service.HotelReservationClient;

@ExtendWith(MockitoExtension.class)
class HotelReservationOutPortImplTest {

  @InjectMocks
  private HotelReservationOutPortImpl hotelReservationOutPort;
  @Mock
  private HotelReservationClient hotelReservationClient;
  @Mock
  private FindBookingResponseMapper findBookingResponseMapper;
  @Mock
  private ManageBookingInfoMapper manageBookingInfoMapper;

  @Test
  void retrieveBookingDetailsTest() {
    when(hotelReservationClient.findBooking(any(), any(), any(), any())).thenReturn(new FindBookingResponseDto());
    when(findBookingResponseMapper.toModel(any())).thenReturn(new FindBookingResponse());

    var response = hotelReservationOutPort.findBooking("Doe", LocalDate.now(), "31232132", "en");

    assertNotNull(response);
    assertThat(response, instanceOf(FindBookingResponse.class));
  }

  @Test
  void getManageBookingInfoTest() {
    when(hotelReservationClient.getManageBookingInfo(any(), any(), any(), any(), any())).thenReturn(
        new ManageBookingResponseDto());
    when(manageBookingInfoMapper.toModel(any())).thenReturn(new ManageBookingResponse());

    var response = hotelReservationOutPort.getManageBookingInfo("AH12312312", "LONEUS",
        "dummy_token", "en", LocalDateTime.now());

    assertNotNull(response);
    assertThat(response, instanceOf(ManageBookingResponse.class));
  }
}
