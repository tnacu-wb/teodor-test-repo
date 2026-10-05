package uk.co.whitbread.infrastructure.rest.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.basket.generated.models.BasketDto;
import uk.co.whitbread.infrastructure.rest.client.basket.exceptions.BasketServiceException;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class BasketServiceClientTest {

  public static final String BASKET_REFERENCE = "TEST_BASKET";
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @InjectMocks
  private BasketServiceClient basketServiceClient;

  @Test
  void getBasket_shouldReturnBasketDto() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(BasketDto.class)).thenReturn(Mono.just(new BasketDto()));
    // Act
    var basket = basketServiceClient.getBasket(BASKET_REFERENCE);

    // Assert
    assertNotNull(basket);
  }

  @Test
  void getBasket_shouldThrowBasketServiceException() {
    // Arrange
    var error = "Error while trying to get basket";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(BasketDto.class)).thenReturn(
        Mono.error(new BasketServiceException("message", error, new Exception(), 1)));

    // Act & Assert
    var exception = assertThrows(BasketServiceException.class, () -> {
      basketServiceClient.getBasket(BASKET_REFERENCE);
    });

    assertEquals("Error while trying to get basket", exception.getMessage());
  }
}
