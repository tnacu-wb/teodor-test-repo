package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationInvoicesResponse;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchOutPortImplInvoicesTest {

  @Mock
  private CdhReservationSearchClientV1 cdhReservationSearchClientV1;

  @Mock
  private CdhReservationSearchClientV2 cdhReservationSearchClientV2;

  @Mock
  private CdhReservationInvoicesClientV1 cdhReservationInvoicesClientV1;

  @InjectMocks
  private CdhReservationSearchOutPortImpl outPort;

  @Test
  void getBookingInvoices_whenClientReturnsNull_returnsEmptyResponse() {
    when(cdhReservationInvoicesClientV1.getBookingInvoices("GAN9859956", "tester@example.com",
        "PI")).thenReturn(null);

    ReservationInvoicesResponse response = outPort.getBookingInvoices("GAN9859956",
        "tester@example.com", "PI");

    assertThat(response).isNotNull();
    assertThat(response.getNumberOfResults()).isZero();
  }

  @Test
  void getBookingInvoices_whenClientReturnsResponse_returnsIt() {
    ReservationInvoicesResponse expected = ReservationInvoicesResponse.builder().numberOfResults(2).build();
    when(cdhReservationInvoicesClientV1.getBookingInvoices("GAN9859956", "tester@example.com",
        "PI")).thenReturn(expected);

    ReservationInvoicesResponse response = outPort.getBookingInvoices("GAN9859956",
        "tester@example.com", "PI");

    assertThat(response).isSameAs(expected);
  }
}

