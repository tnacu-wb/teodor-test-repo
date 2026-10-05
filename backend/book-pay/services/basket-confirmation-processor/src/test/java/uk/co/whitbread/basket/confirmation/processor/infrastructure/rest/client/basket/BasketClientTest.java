package uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.basket;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.exception.BasketConfirmationException;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.rest.client.model.in.ConfirmItemProcessingRequestDto;
import uk.co.whitbread.basket.confirmation.processor.infrastructure.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class BasketClientTest {

  @InjectMocks
  private BasketClient basketClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private CustomTestResponseSpec responseSpec;

  @Test
  void testSendAcknowledge__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);

    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Void.class)).thenReturn(mockResponse());

    //Act
    ConfirmItemProcessingRequestDto build = ConfirmItemProcessingRequestDto.builder().status(0)
        .build();
    basketClient.sendAcknowledge("TEST", "TEST", build);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testSendAcknowledge__failure() {
    String errorMessage =  "An error was returned calling the Basket Service.";

    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(Void.class)).thenReturn(
          Mono.error(new BasketConfirmationException("message", errorMessage,new Exception(),1)));

    //Act
    var exception = assertThrowsExactly(BasketConfirmationException.class,
        () -> basketClient
            .sendAcknowledge("TEST", "TEST", ConfirmItemProcessingRequestDto.builder()
                .status(0)
                .build()));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
    verifyNoMoreInteractions(webClient);
  }

  private Mono<Void> mockResponse() {
    return Mono.empty();
  }

}