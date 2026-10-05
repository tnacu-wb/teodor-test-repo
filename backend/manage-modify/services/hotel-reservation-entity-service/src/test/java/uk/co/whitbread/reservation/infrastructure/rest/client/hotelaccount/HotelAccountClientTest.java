package uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import uk.co.whitbread.hotel.account.service.generated.models.CustomerRequest;
import uk.co.whitbread.hotel.account.service.generated.models.CustomerResponse;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.exceptions.HotelAccountException;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.HotelAccountClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class HotelAccountClientTest {

  private static final String CUSTOMER_123 = "customer123";
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @Mock
  private HotelAccountProperties hotelAccountProperties;

  @InjectMocks
  private HotelAccountClient hotelAccountClient;

  @Test
  void updateCustomer_success() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenReturn(customResponseSpec);
    when(customResponseSpec.bodyToMono(CustomerResponse.class)).thenReturn(
        Mono.just(new CustomerResponse()));

    // Act
    var response = hotelAccountClient.sendUpdateCustomerRequest(new CustomerRequest(), CUSTOMER_123, "auth-token");

    // Assert
    assertNotNull(response);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateCustomer_failure() {
    // Arrange
    // Arrange
    var ex = mock(HotelAccountException.class);
    Mono<CustomerResponse> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(CustomerResponse.class)).thenReturn(rsp);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);

    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    var req = new CustomerRequest();

    // Act & Assert
    assertThrows(HotelAccountException.class,
        () -> hotelAccountClient.sendUpdateCustomerRequest(req, CUSTOMER_123, "auth-token"));
  }
}
