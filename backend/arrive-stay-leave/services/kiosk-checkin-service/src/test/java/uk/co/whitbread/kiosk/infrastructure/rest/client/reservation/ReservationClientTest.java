package uk.co.whitbread.kiosk.infrastructure.rest.client.reservation;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.in.ConfirmReservationRequest;
import uk.co.whitbread.kiosk.domain.model.confirmreservation.out.ConfirmReservationResponse;
import uk.co.whitbread.kiosk.infrastructure.rest.CustomTestResponseSpec;
import uk.co.whitbread.kiosk.infrastructure.rest.client.ohip.exceptions.OhipAdapterException;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.ReservationClient;
import uk.co.whitbread.kiosk.infrastructure.rest.client.reservation.service.properties.ReservationProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ReservationClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private ReservationProperties reservationProperties;

  @InjectMocks
  private ReservationClient reservationClient;

  @Test
  void createProfile_WhenOhipClientReturnsAnError_ThenProfileExceptionIsThrown() {
    OhipAdapterException ex = mock(OhipAdapterException.class);
    when(customResponseSpec.bodyToMono(ConfirmReservationResponse.class)).thenReturn(Mono.error(ex));
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    ConfirmReservationRequest confirmReservationRequest = new ConfirmReservationRequest();
    assertThrows(OhipAdapterException.class,
        () -> reservationClient.makeDeposit(confirmReservationRequest));
  }

}