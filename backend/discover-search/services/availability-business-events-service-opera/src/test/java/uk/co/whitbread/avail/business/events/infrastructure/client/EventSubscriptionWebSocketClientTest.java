package uk.co.whitbread.avail.business.events.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.graphql.client.ClientGraphQlResponse;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OperaAuthenticationPort;
import uk.co.whitbread.avail.business.events.infrastructure.adapters.HotelAvailabilityDbBatchService;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;


@DirtiesContext
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class EventSubscriptionWebSocketClientTest {

  @Mock
  private OperaProperties operaProperties;

  @Mock
  private WebSocketGraphqlClientProvider webSocketGraphqlClientProvider;

  @Mock
  private WebSocketGraphQlClient webSocketGraphQlClientMock;

  @Mock
  private GraphQlClient.RequestSpec requestSpec;

  @Mock
  private ClientGraphQlResponse clientGraphQlResponseMock;

  @Mock
  Flux<ClientGraphQlResponse> clientGraphQlResponseFluxMock;

  @Mock
  private HotelAvailabilityDbBatchService hotelAvailabilityDbBatchService;

  @Mock
  private OperaAuthenticationPort operaAuthenticationPort;


  private EventSubscriptionWebSocketClient eventSubscriptionWebSocketClient;
  private static final String DUMMY_API_KEY = "488c82b8-50fe-4cc1-ae82-4b7aa470bf58";
  private static final String DUMMY_oauth_token
      = "oauh488c82b8-50fe-4cc1-ae82-4b7aa470bf58token";

  @BeforeEach
  public void setup(){
    eventSubscriptionWebSocketClient =
        new EventSubscriptionWebSocketClient(
            operaProperties,
            webSocketGraphqlClientProvider,
            hotelAvailabilityDbBatchService,
            operaAuthenticationPort);
  }

  @Test
  public void successfulScenarioSubscribeForBusinessEventsTest(){
    when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
    when(operaAuthenticationPort.fetchOauthToken(false)).thenReturn(DUMMY_oauth_token);

    when(webSocketGraphqlClientProvider
        .getWebSocketGraphqlClient(DUMMY_API_KEY, DUMMY_oauth_token))
        .thenReturn(webSocketGraphQlClientMock);

    Mono<String> monoString=Mono.just("test");
    Mono<Void> monoVoid=monoString.then();
    when(webSocketGraphQlClientMock.start()).thenReturn(monoVoid);

    when(webSocketGraphQlClientMock
        .documentName(anyString())).thenReturn(requestSpec);

    when(requestSpec.variables(anyMap())).thenReturn(requestSpec);

    when(requestSpec.executeSubscription()).thenReturn(clientGraphQlResponseFluxMock);

    doCallRealMethod().when(clientGraphQlResponseFluxMock).doOnCancel(any());
    eventSubscriptionWebSocketClient.subscribeForBusinessEvents(false);

    verify(webSocketGraphQlClientMock, times(1)).start();
    verify(requestSpec, times(1)).executeSubscription();
  }

  @Test
  public void shouldThrowExceptionIfHandshakeIsNotHappening(){
    when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
    when(operaAuthenticationPort.fetchOauthToken(false)).thenReturn(DUMMY_oauth_token);

    when(webSocketGraphqlClientProvider
        .getWebSocketGraphqlClient(DUMMY_API_KEY, DUMMY_oauth_token))
        .thenReturn(webSocketGraphQlClientMock);

    when(webSocketGraphQlClientMock.start())
        .thenThrow(new RuntimeException("Some exception occurred!"));

    assertThrows(RuntimeException.class, () -> eventSubscriptionWebSocketClient.subscribeForBusinessEvents(false));

    verify(webSocketGraphQlClientMock, times(1)).start();
    verify(requestSpec, never()).executeSubscription();
  }

  @Test
  public void shouldThrowExceptionWhenExecuteSubscribeIsThrowingError(){
    when(operaProperties.getAppkey()).thenReturn(DUMMY_API_KEY);
    when(operaAuthenticationPort.fetchOauthToken(false)).thenReturn(DUMMY_oauth_token);

    when(webSocketGraphqlClientProvider
        .getWebSocketGraphqlClient(DUMMY_API_KEY, DUMMY_oauth_token))
        .thenReturn(webSocketGraphQlClientMock);

    Mono<String> monoString=Mono.just("test");
    Mono<Void> monoVoid=monoString.then();
    when(webSocketGraphQlClientMock.start()).thenReturn(monoVoid);

    when(webSocketGraphQlClientMock
        .documentName(anyString())).thenReturn(requestSpec);

    when(requestSpec.variables(anyMap())).thenReturn(requestSpec);

    when(requestSpec.executeSubscription())
        .thenReturn(Flux.error(
            new WebSocketClientHandshakeException("Invalid handshake response!")))
        .thenReturn(clientGraphQlResponseFluxMock);

    eventSubscriptionWebSocketClient.subscribeForBusinessEvents(false);

  }

  @Test
  public  void subscribeForBusinessEventsFromMockServiceErrorLogTest(){

    when(webSocketGraphqlClientProvider.getWebSocketGraphqlClient())
        .thenReturn(webSocketGraphQlClientMock);

    Mono<String> monoString=Mono.just("test");
    Mono<Void> monoVoid=monoString.then();
    when(webSocketGraphQlClientMock.start()).thenReturn(monoVoid);

    when(webSocketGraphQlClientMock
        .documentName(anyString())).thenReturn(requestSpec);

    when(requestSpec.variables(anyMap())).thenReturn(requestSpec);

    Flux<ClientGraphQlResponse> responseFlux = Flux.just(clientGraphQlResponseMock);

    when(requestSpec.executeSubscription())
        .thenReturn(responseFlux);

      eventSubscriptionWebSocketClient.subscribeForBusinessEventsFromMock();

  }

  @Test
  public  void subscribeForBusinessEventsFromMockServiceNoErrorLogTest(){

    when(webSocketGraphqlClientProvider.getWebSocketGraphqlClient())
        .thenReturn(webSocketGraphQlClientMock);

    Mono<String> monoString=Mono.just("test");
    Mono<Void> monoVoid=monoString.then();
    when(webSocketGraphQlClientMock.start()).thenReturn(monoVoid);

    when(webSocketGraphQlClientMock
        .documentName(anyString())).thenReturn(requestSpec);

    when(requestSpec.variables(anyMap())).thenReturn(requestSpec);

    when(requestSpec.executeSubscription())
        .thenReturn(clientGraphQlResponseFluxMock);

    doCallRealMethod().when(clientGraphQlResponseFluxMock).doOnCancel(any());

    eventSubscriptionWebSocketClient.subscribeForBusinessEventsFromMock();

  }


}
