package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelinfo.service.HotelInfoClient;

@ExtendWith(MockitoExtension.class)
class HotelInfoOutPortImplTest {

  @InjectMocks
  private HotelInfoOutPortImpl hotelInfoOutPort;

  @Mock
  private HotelInfoClient hotelInfoClient;

  @Test
  void getBookingDetailsTest() {
    when(hotelInfoClient.getHotelInfo(any())).thenReturn(new HotelInfo());

    final var response = hotelInfoOutPort.getHotelInfo("LONEUS");

    assertNotNull(response);
    assertThat(response, instanceOf(HotelInfo.class));
    verifyNoMoreInteractions(hotelInfoClient);
  }
}
