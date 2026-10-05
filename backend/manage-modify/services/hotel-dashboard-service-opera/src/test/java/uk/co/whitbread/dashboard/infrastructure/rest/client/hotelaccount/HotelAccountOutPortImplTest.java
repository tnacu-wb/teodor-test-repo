package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount;

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
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;

@ExtendWith(MockitoExtension.class)
class HotelAccountOutPortImplTest {

  @InjectMocks
  private HotelAccountOutPortImpl hotelAccountOutPort;

  @Mock
  private HotelAccountClient hotelAccountClient;


  @Test
  void getAccountStaysTest() {
    when(hotelAccountClient.getAccountStays(any(),any(),any(), any(), any())).thenReturn(new StaysResponse());

    var response = hotelAccountOutPort.getAccountStays(new BBStaysRequestV2(), "13213222", "auth",
        "PI", "");

    assertNotNull(response);
    assertThat(response, instanceOf(StaysResponse.class));
    verifyNoMoreInteractions(hotelAccountClient);
  }
}
