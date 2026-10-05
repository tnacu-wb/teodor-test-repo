package uk.co.whitbread.availabilitycacheservice.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.rulesagent.RulesAgentProperties;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class RulesAgentClientWebFluxTest {

  private static final String HOST = "http://testhost";
  private static final String ENDPOINT = "/room-substitution";

  @Mock
  private RulesAgentProperties rulesAgentProperties;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private ResponseSpec responseSpec;

  private RulesAgentClientWebFlux rulesAgentClientWebFlux;

  @BeforeEach
  void setUp() {
    // Lenient stubs are required here because the constructor, called before each test,
    // uses these mocks. Not every test, however, will re-trigger these specific calls.
    Mockito.lenient().when(rulesAgentProperties.getHost()).thenReturn(HOST);
    Mockito.lenient().when(rulesAgentProperties.getRoomSubstitutionEndpoint()).thenReturn(ENDPOINT);

    rulesAgentClientWebFlux = new RulesAgentClientWebFlux(rulesAgentProperties, webClient);
  }

  @Test
  void getRoomSubstitutions_shouldReturnMap() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    Map<String, Object> expected = new HashMap<>();
    expected.put("key", "value");
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
        .thenReturn(Mono.just(expected));

    // Act
    Map<String, Object> actual = rulesAgentClientWebFlux.getRoomSubstitutions("ROOM", 2, 1).block();

    // Assert
    assertNotNull(actual);
    assertEquals("value", actual.get("key"));
  }

  @Test
  void getRoomSubstitutions_shouldPropagateError() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
        .thenReturn(Mono.error(new RuntimeException("error")));

    // Act & Assert
    assertThrows(RuntimeException.class, () -> rulesAgentClientWebFlux.getRoomSubstitutions("ROOM", 2, 1).block());
  }

  @Test
  void getRoomSubstitutions_shouldBuildCorrectUri() {
    // Arrange
    ArgumentCaptor<Function<UriBuilder, URI>> uriFunctionCaptor =
        ArgumentCaptor.forClass(Function.class);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(uriFunctionCaptor.capture())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
        .thenReturn(Mono.just(new HashMap<>()));

    // Act
    rulesAgentClientWebFlux.getRoomSubstitutions("ROOMTYPE", 3, 2).block();

    // Assert
    Function<UriBuilder, URI> uriFunction = uriFunctionCaptor.getValue();
    UriBuilder uriBuilder = new DefaultUriBuilderFactory(HOST).builder();
    URI uri = uriFunction.apply(uriBuilder);
    String uriString = uri.toString();

    assertNotNull(uriFunction);
    assertTrue(uriString.startsWith(HOST + ENDPOINT));
    assertTrue(uriString.contains("roomType=ROOMTYPE"));
    assertTrue(uriString.contains("adults=3"));
    assertTrue(uriString.contains("children=2"));
    assertTrue(uriString.contains("pms=OP"));
    assertTrue(uriString.contains("channel=PI"));
  }
}
