package uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.dashboard.infrastructure.rest.client.hotelaccount.service.properties.HotelAccountProperties;
import uk.co.whitbread.dashboard.infrastructure.rest.utils.CustomTestResponseSpec;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class HotelAccountClientTest {

  @InjectMocks
  private HotelAccountClient hotelAccountClient;
  @Mock
  private WebClient webClient;
  @Mock
  private HotelAccountProperties hotelAccountProperties;

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



  @Test
  void sendGetAccountStays_success() {
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(hotelAccountProperties.getCustomerStaysEndpoint()).thenReturn("/account");
    when(requestBodyUriSpec.uri(anyString(),anyString())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);

    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(StaysResponse.class)).thenReturn(Mono.just(new StaysResponse()));
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    // Act
    var response = hotelAccountClient.getAccountStays(new BBStaysRequestV2(),"customerId",
        "dummy_token", "MOBILE","local" );

    // Assert
    assertNotNull(response);
  }
}
