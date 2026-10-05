package uk.co.whitbread.wallet.domain.logic;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.generated.models.reservation.FindBookingResponseDto;
import uk.co.whitbread.wallet.domain.model.in.WalletRequest;
import uk.co.whitbread.wallet.domain.ports.secondary.HotelReservationOutPort;

@ExtendWith(MockitoExtension.class)
class HotelReservationInPortImplTest {

  @InjectMocks
  private HotelReservationInPortImpl hotelReservationInPort;

  @Mock
  private HotelReservationOutPort hotelReservationOutPort;

  @Test
  void getBasketReferenceTest() {
    var basketRequest = new WalletRequest("AKU5411146", "2024-10-01", "Test", "en", "gb", "PI",
        false);

    var findBookingResponseDto = new FindBookingResponseDto();
    findBookingResponseDto.setBasketReference("reference");
    when(hotelReservationOutPort.findBooking(any())).thenReturn(findBookingResponseDto);

    var result = hotelReservationInPort.getBasketReference(basketRequest);
    MatcherAssert.assertThat(result, instanceOf(FindBookingResponseDto.class));
    assertThat(result.getBasketReference(), is("reference"));
  }
}
