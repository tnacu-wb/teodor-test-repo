package uk.co.whitbread.basket.infrastructure.rest.client.marketing.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.basket.generated.models.marketing.UpdatePreferencesRequest;
import uk.co.whitbread.basket.infrastructure.rest.client.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class MarketingClientTest {

  private static final UpdatePreferencesRequest UPDATE_PREFERENCES_REQUEST = new UpdatePreferencesRequest();
  @InjectMocks
  private MarketingClient marketingClient;

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;


  @Test
  void updateMarketingPreferences__ShouldReturnOk() {
    // Arrange

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    marketingClient.updateMarketingPreferences("contactType", "contactValue",
        UPDATE_PREFERENCES_REQUEST);

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateMarketingPreferences__ShouldThrowException() {
    // Arrange
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class)))
        .thenCallRealMethod();
    // Act
    assertThrows(RuntimeException.class,
        () -> marketingClient.updateMarketingPreferences("contactType", "contactValue",
            UPDATE_PREFERENCES_REQUEST));
    //Assert
    verifyNoMoreInteractions(webClient);
  }

}