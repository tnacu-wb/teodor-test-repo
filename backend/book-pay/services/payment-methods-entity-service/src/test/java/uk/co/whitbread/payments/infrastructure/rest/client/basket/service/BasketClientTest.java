package uk.co.whitbread.payments.infrastructure.rest.client.basket.service;

import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions.BasketBadRequestException;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.model.out.BasketDto;
import uk.co.whitbread.payments.infrastructure.rest.client.basket.service.properties.BasketClientProperties;

import java.util.function.Function;
import uk.co.whitbread.payments.infrastructure.rest.client.service.CustomTestResponseSpec;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BasketClientTest {

  @Mock
  private WebClient basketWebClient;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @Mock
  private BasketClientProperties basketProperties;
  @InjectMocks
  private BasketClient basketClient;
  private String errorMessage = "Error while trying to get basket";

  @Test
  void sendGetBasketByReference_internalServerError() {
    //Arrange
    when(basketWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BasketDto.class)).thenReturn(
        Mono.error(new BasketInternalException("message", errorMessage, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(BasketInternalException.class,
        () -> basketClient.sendGetBasketByReference("basketReference"));

    //Assert
    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void sendGetBasketByReference_badRequest() {
    //Arrange
    when(basketWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST); // Use BAD_REQUEST (400)
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BasketDto.class)).thenReturn(
        Mono.error(new BasketBadRequestException("message", errorMessage, new Exception(), 1)));

    //Act
    Exception exception = assertThrows(BasketBadRequestException.class,
        () -> basketClient.sendGetBasketByReference("badRequest"));

    //Assert
    assertTrue(exception.getMessage().contains(errorMessage));
  }

  @Test
  void sendGetBasketByReference_notFound() {
    //Arrange
    when(basketWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BasketDto.class)).thenReturn(Mono.empty());

    //Act
    var result = basketClient.sendGetBasketByReference("notfound");

    //Assert
    assertNull(result);
  }

  @Test
  void sendGetBasketByReference_success() {
    //Arrange
    when(basketWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.OK);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpecMock.bodyToMono(BasketDto.class)).thenReturn(Mono.just(new BasketDto()));

    //Act
    var result = basketClient.sendGetBasketByReference("ref123");

    //Assert
    assertNotNull(result);
  }
}
