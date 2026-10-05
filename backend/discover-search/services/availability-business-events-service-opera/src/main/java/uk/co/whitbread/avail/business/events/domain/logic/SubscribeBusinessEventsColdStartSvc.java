package uk.co.whitbread.avail.business.events.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import uk.co.whitbread.avail.business.events.infrastructure.client.EventSubscriptionWebSocketClient;
import uk.co.whitbread.avail.business.events.infrastructure.config.OhipGraphQlMockProperties;

@Slf4j
@ConditionalOnProperty(
    prefix = "SubscribeBusinessEventsColdStartSvc.runner",
    value = "enabled",
    havingValue = "true",
    matchIfMissing = true)
@Component
@RequiredArgsConstructor
public class SubscribeBusinessEventsColdStartSvc implements ApplicationRunner {

  private final EventSubscriptionWebSocketClient eventSubscriptionWebSocketClient;
  private final OhipGraphQlMockProperties ohipGraphQlMockProperties;

  @Override
  public void run(ApplicationArguments args) {
    log.info("SubscribeBusinessEventsColdStartSvc started: {}", args);
    final boolean mockServiceEnabled = ohipGraphQlMockProperties.isEnabled();
    if (mockServiceEnabled) {
      final String url = ohipGraphQlMockProperties.getUrl();
      log.info("ohip-graphql-mock-server is enabled, url: {}", url);
      eventSubscriptionWebSocketClient.subscribeForBusinessEventsFromMock();
    } else {
      eventSubscriptionWebSocketClient.initializeBizEventsSubscription();
    }
  }
}
