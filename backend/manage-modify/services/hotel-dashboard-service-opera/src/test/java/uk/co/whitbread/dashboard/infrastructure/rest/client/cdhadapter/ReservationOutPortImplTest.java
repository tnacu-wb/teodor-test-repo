package uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;
import uk.co.whitbread.dashboard.infrastructure.rest.client.cdhadapter.service.CdhAdapterClient;

@ExtendWith(MockitoExtension.class)
class ReservationOutPortImplTest {

  @InjectMocks
  private CdhReservationOutPortImpl cdhReservationOutPort;
  @Mock
  private CdhAdapterClient cdhAdapterClient;

  @Test
  void retrieveBookingDetailsTest() {
    when(cdhAdapterClient.retrieveBooking(anyString())).thenReturn(new CdhReservationSearchDto());

    var response = cdhReservationOutPort.retrieveBooking("31232132");

    assertNotNull(response);
    assertThat(response, instanceOf(CdhReservationSearchDto.class));
  }

}
