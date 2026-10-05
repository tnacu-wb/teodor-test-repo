package uk.co.whitbread.avail.business.events.domain.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import uk.co.whitbread.avail.business.events.infrastructure.client.EventSubscriptionWebSocketClient;
import uk.co.whitbread.avail.business.events.infrastructure.config.OhipGraphQlMockProperties;

@ExtendWith(MockitoExtension.class)
public class SubscribeBusinessEventsColdStartSvcTest {

  private static final String DUMMY_oauth_token
      = "oauh488c82b8-50fe-4cc1-ae82-4b7aa470bf58token";

  /*@Mock
  private OperaAuthenticationPort operaAuthenticationPort;*/

  @Mock
  private OhipGraphQlMockProperties ohipGraphQlMockProperties;

  @Mock
  private EventSubscriptionWebSocketClient eventSubscriptionWebSocketClient;

  private SubscribeBusinessEventsColdStartSvc subscribeBusinessEventsColdStartSvc;

  private ApplicationArguments nullApplicationArguments;

  @BeforeEach
  public void setup() {
    nullApplicationArguments = null;
    subscribeBusinessEventsColdStartSvc = new SubscribeBusinessEventsColdStartSvc(
        eventSubscriptionWebSocketClient,
        ohipGraphQlMockProperties);

  }

  @Test
  public void runShouldInvokeOhipServerForEventsTest() {

    Mockito.when(ohipGraphQlMockProperties.isEnabled()).thenReturn(false);

    //Mockito.when(operaAuthenticationPort.fetchOauthToken()).thenReturn(DUMMY_oauth_token);

    subscribeBusinessEventsColdStartSvc.run(nullApplicationArguments);

    Mockito.verify(eventSubscriptionWebSocketClient, Mockito.times(1))
        .initializeBizEventsSubscription();

    Mockito.verify(eventSubscriptionWebSocketClient, Mockito.never())
        .subscribeForBusinessEventsFromMock();

  }

  @Test
  public void runShouldInvokeMockServerTest() {

    Mockito.when(ohipGraphQlMockProperties.isEnabled()).thenReturn(true);

    subscribeBusinessEventsColdStartSvc.run(nullApplicationArguments);

    Mockito.verify(eventSubscriptionWebSocketClient, Mockito.times(1))
        .subscribeForBusinessEventsFromMock();

    Mockito.verify(eventSubscriptionWebSocketClient, Mockito.never())
        .initializeBizEventsSubscription();
  }
}
