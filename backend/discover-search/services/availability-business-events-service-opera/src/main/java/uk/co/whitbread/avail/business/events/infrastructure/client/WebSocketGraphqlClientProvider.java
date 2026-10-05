package uk.co.whitbread.avail.business.events.infrastructure.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClientInterceptor;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.avail.business.events.infrastructure.config.OhipGraphQlMockProperties;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.InitMessage;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.Payload;
import uk.co.whitbread.avail.business.events.infrastructure.utils.HashingUtils;

@RequiredArgsConstructor
@Slf4j
public class WebSocketGraphqlClientProvider {

  private final OperaProperties operaProperties;
  private final OhipGraphQlMockProperties ohipGraphQlMockProperties;

  public WebSocketGraphQlClient getWebSocketGraphqlClient(
      final String apiKey,
      final String oauthToken) {

    final String hashedApiKey = HashingUtils.getSha256Hash(apiKey);

    final String subscriptionUrl = operaProperties.getServiceUrl().getSubscriptionUrl();

    final String url = subscriptionUrl + "/subscriptions?key=" + hashedApiKey;

    log.debug("complete url: {}", subscriptionUrl + "/subscriptions?key="
        + HashingUtils.maskStringExceptLast4(hashedApiKey));

    final WebSocketClient client = new ReactorNettyWebSocketClient();

    return WebSocketGraphQlClient.builder(url, client)
        .interceptor(new OperaWebSocketGraphQlClientInterceptor(apiKey, oauthToken))
        .headers(httpHeaders -> httpHeaders.set("Sec-WebSocket-Protocol", "graphql-transport-ws"))
        .build();
  }

  public WebSocketGraphQlClient getWebSocketGraphqlClient() {

    final String url = ohipGraphQlMockProperties.getUrl();
    log.debug("url: {}", url);

    final WebSocketClient client = new ReactorNettyWebSocketClient();
    return WebSocketGraphQlClient.builder(url, client)
        .build();
  }

  static class OperaWebSocketGraphQlClientInterceptor
      implements WebSocketGraphQlClientInterceptor {

    private final String apiKey;
    private final String oauthToken;

    public OperaWebSocketGraphQlClientInterceptor(
        final String apiKey,
        final String oauthToken) {
      this.apiKey = apiKey;
      this.oauthToken = oauthToken;
    }

    @Override
    public Mono<Object> connectionInitPayload() {
      final InitMessage initMessage = getInitMessage(oauthToken, apiKey);

      log.info("InitMessage Payload: {}, ApiKey: {}",
          HashingUtils.maskStringExceptLast4(initMessage.getPayload().getAuthorization()),
          HashingUtils.maskStringExceptLast4(initMessage.getPayload().getXappKey()));
      
      return Mono.just(initMessage.getPayload());
    }

    private static InitMessage getInitMessage(final String oauthToken, final String apiKey) {

      final String bearerAuthToken = "Bearer " + oauthToken;

      final Payload payload =
          Payload.builder().xappKey(apiKey).authorization(bearerAuthToken).build();

      return InitMessage.builder().type("connection_init").payload(payload).build();

    }
  }

}
