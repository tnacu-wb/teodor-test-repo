package uk.co.whitbread.avail.business.events.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import uk.co.whitbread.avail.business.events.infrastructure.config.OhipGraphQlMockProperties;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties.ServiceUrl;
import uk.co.whitbread.avail.business.events.infrastructure.utils.HashingUtils;

@ExtendWith(MockitoExtension.class)
public class WebSocketGraphqlClientProviderTest {

  private static final String DUMMY_API_KEY = "488c82b8-50fe-4cc1-ae82-4b7aa470bf58";

  private static final String DUMMY_oauth_token
      = "oauh488c82b8-50fe-4cc1-ae82-4b7aa470bf58token";

  private static final String EXPECTED_HASHED_API_KEY =
      "3cc6054463d01e4dfd845f1f0afdd37128ea9368192de89c48c6b381ce06980e";

  private static final String HOST =
      "wss://test4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com";

  @Mock
  private OperaProperties operaProperties;

  @Mock
  private OhipGraphQlMockProperties ohipGraphQlMockProperties;

  private WebSocketGraphqlClientProvider webSocketGraphqlClientProvider;

  @BeforeEach
  public void setup() {
    webSocketGraphqlClientProvider
        = new WebSocketGraphqlClientProvider(operaProperties, ohipGraphQlMockProperties);
  }

  @Test
  public void getWebSocketGraphqlClientTest() {

    try (MockedStatic<HashingUtils> hashingUtils = mockStatic(HashingUtils.class)) {
      hashingUtils.when(() -> HashingUtils.getSha256Hash(DUMMY_API_KEY)).thenReturn(EXPECTED_HASHED_API_KEY);
    }

    final ServiceUrl serviceUrl = getServiceUrl();

    when(operaProperties.getServiceUrl()).thenReturn(serviceUrl);

    WebSocketGraphQlClient webSocketGraphQlClient =
        webSocketGraphqlClientProvider.getWebSocketGraphqlClient(DUMMY_API_KEY, DUMMY_oauth_token);

    assertNotNull(webSocketGraphQlClient);

  }

  @Test
  public void getWebSocketGraphqlClientForMockServerTest() {

    WebSocketGraphQlClient webSocketGraphQlClient =
        webSocketGraphqlClientProvider.getWebSocketGraphqlClient();

    assertNotNull(webSocketGraphQlClient);

  }

  private ServiceUrl getServiceUrl() {

    ServiceUrl serviceUrl = new ServiceUrl();
    serviceUrl.setSubscriptionUrl("wss://test4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com");
    serviceUrl
        .setOauth("https://test4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com/oauth/v1/tokens");
    return serviceUrl;
  }

}
