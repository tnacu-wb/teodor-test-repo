package uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.exceptions.CdhBookingInvoiceException;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.ReservationInvoicesResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("unchecked")
class CdhAdapterClientTest {

  private static final String BOOKING_REFERENCE = "GAA9674785";
  private static final String ENDPOINT = "/v1/cdh/reservation/invoices";
  private static final String EXPECTED_URI =
      "http://localhost:9119/v1/cdh/reservation/invoices?bookingReference=GAA9674785&accessedBy=test.user@whitbread.com&accessContext=PI";

  @Mock
  private WebClient cdhAdapterWebClient;
  @Mock
  private CdhAdapterProperties cdhAdapterProperties;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Test
  void getBookingInvoices_success() {
    CdhAdapterClient client = new CdhAdapterClient(cdhAdapterProperties, cdhAdapterWebClient);
    ReservationInvoicesResponseDto expectedResponse = new ReservationInvoicesResponseDto();

    when(cdhAdapterProperties.getBookingInvoicesEndpoint()).thenReturn(ENDPOINT);
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
      Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
      URI uri = uriFunction.apply(new DefaultUriBuilderFactory("http://localhost:9119").builder());
      assertEquals(EXPECTED_URI, uri.toString());
      return requestHeadersSpec;
    });
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationInvoicesResponseDto.class))
        .thenReturn(Mono.just(expectedResponse));

    ReservationInvoicesResponseDto actualResponse = client.getBookingInvoices(BOOKING_REFERENCE);

    assertNotNull(actualResponse);
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getBookingInvoices_throwsCdhBookingInvoiceException_on4xx() {
    CdhAdapterClient client = new CdhAdapterClient(cdhAdapterProperties, cdhAdapterWebClient);

    when(cdhAdapterProperties.getBookingInvoicesEndpoint()).thenReturn(ENDPOINT);
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationInvoicesResponseDto.class))
        .thenReturn(Mono.error(new CdhBookingInvoiceException("exp", "message", 100)));

    assertThrows(CdhBookingInvoiceException.class,
        () -> client.getBookingInvoices(BOOKING_REFERENCE));
  }
}
