package uk.co.whitbread.avail.business.events.infrastructure.client.content.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import uk.co.whitbread.avail.business.events.infrastructure.client.content.exception.ContentException;
import uk.co.whitbread.avail.business.events.infrastructure.client.content.exception.NoHeaderDataException;
import uk.co.whitbread.avail.business.events.infrastructure.utils.CustomTestResponseSpec;
import uk.co.whitbread.content.entity.service.generated.models.content.GlobalConfigDto;

@ExtendWith(MockitoExtension.class)
class ContentClientTest {

  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @InjectMocks
  private ContentClient contentClient;

  @Test
  void getGlobalConfig_Success() {

    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(Mono.just(new GlobalConfigDto()));

    // Act
    var response = contentClient.getGlobalConfig("gb", "en");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getGlobalConfig_ContentException() {
    // Arrange
    ContentException ex = mock(ContentException.class);
    Mono<GlobalConfigDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(ContentException.class,
        () -> contentClient.getGlobalConfig("en", "gb"));
  }

  @Test
  void getGlobalConfig_NoHeaderDataException() {
    // Arrange
    NoHeaderDataException ex = mock(NoHeaderDataException.class);
    Mono<GlobalConfigDto> rsp = Mono.error(ex);
    when(customResponseSpec.bodyToMono(GlobalConfigDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(NoHeaderDataException.class, () -> contentClient.getGlobalConfig("gb", "en"));
  }
}