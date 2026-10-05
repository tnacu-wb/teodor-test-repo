package uk.co.whitbread.cdh.domain.logic;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;
import uk.co.whitbread.cdh.domain.ports.secondary.CdhReservationSearchOutPort;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchInPortImplInvoicesTest {

  @Mock
  private CdhReservationSearchOutPort cdhReservationSearchOutPort;

  @InjectMocks
  private CdhReservationSearchInPortImpl inPort;

  @Test
  void getBookingInvoices_delegatesToOutPort() {
    String bookingRef = "GAN9859956";
    String accessedBy = "tester@example.com";
    String accessContext = "PI";
    ReservationInvoicesResponse expected = ReservationInvoicesResponse.builder().numberOfResults(2).build();

    when(cdhReservationSearchOutPort.getBookingInvoices(bookingRef, accessedBy, accessContext))
        .thenReturn(expected);

    inPort.getBookingInvoices(bookingRef, accessedBy, accessContext);

    verify(cdhReservationSearchOutPort).getBookingInvoices(bookingRef, accessedBy, accessContext);
  }
}
