package uk.co.whitbread.avail.business.events.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import uk.co.whitbread.avail.business.events.domain.ports.secondary.OperaAuthenticationPort;
import uk.co.whitbread.avail.business.events.infrastructure.adapters.HotelAvailabilityDbBatchService;
import uk.co.whitbread.avail.business.events.infrastructure.config.OperaProperties;
import uk.co.whitbread.avail.business.events.infrastructure.model.opera.EventHeader;
import uk.co.whitbread.avail.business.events.infrastructure.utils.HashingUtils;
import uk.co.whitbread.avail.business.events.infrastructure.utils.OperaSubscriptionErrors;

@RequiredArgsConstructor
@Slf4j
public class EventSubscriptionWebSocketClient {

  public static final String HANDSHAKE_REQUEST_FAILED_WITH_ERROR =
      "Handshake request failed with error: {}";

  public static final String SUBSCRIPTION_REQUEST_FAILED_WITH_ERROR =
      "Subscription request failed with error: {}";

  private final OperaProperties operaProperties;

  private final WebSocketGraphqlClientProvider webSocketGraphqlClientProvider;

  private final HotelAvailabilityDbBatchService hotelAvailabilityDbBatchService;

  private final OperaAuthenticationPort operaAuthenticationPort;

  private Disposable disposable;


  public void initializeBizEventsSubscription() {
    log.info("Initializing subscription to biz-events");
    subscribeForBusinessEvents(false);
  }

  public void subscribeForBusinessEvents(final boolean isInvalidToken) {
    final String apiKey = operaProperties.getAppkey();
    log.trace("apiKey: {}", HashingUtils.maskStringExceptLast4(apiKey));
    final String oauthToken = operaAuthenticationPort.fetchOauthToken(isInvalidToken);
    final WebSocketGraphQlClient webSocketGraphQlClient =
        webSocketGraphqlClientProvider.getWebSocketGraphqlClient(apiKey, oauthToken);
    webSocketGraphQlClient.start().doOnError(throwable ->
        log.error(HANDSHAKE_REQUEST_FAILED_WITH_ERROR, throwable.getMessage()));
    makeSubscriptionCall(webSocketGraphQlClient, apiKey);
  }

  public void subscribeForBusinessEventsFromMock() {

    log.info("Requesting handshake with ohip-graphql-mock-server.");

    final WebSocketGraphQlClient webSocketGraphQlClient =
        webSocketGraphqlClientProvider.getWebSocketGraphqlClient();

    webSocketGraphQlClient.start().doOnError(throwable ->
        log.error(HANDSHAKE_REQUEST_FAILED_WITH_ERROR, throwable.getMessage()));

    makeSubscriptionCall(webSocketGraphQlClient, "MOCK_APP-KEY");
  }

  private void makeSubscriptionCall(
      final WebSocketGraphQlClient webSocketGraphQlClient,
      final String appKey) {

    final BigInteger offset = getOffset(appKey);

    Map<String, Object> createSubscriptionVariables = getSubscriptionInput(offset);

    final ObjectMapper objectMapper = new ObjectMapper();

    Flux<EventHeader> result = webSocketGraphQlClient.documentName("subscriptiondocument")
        .variables(createSubscriptionVariables)
        .executeSubscription()
        .doOnCancel(() -> log.error("Flux Canceled, sending complete message!"))
        .doOnError(throwable -> reConnectOnError(webSocketGraphQlClient, throwable))
        .map(response -> {
          if (!response.isValid()) {
            log.error("Received Error Msg from server: {}", response.getErrors());
          }
          log.debug("Is received Event valid: {}", response.isValid());
          return objectMapper.convertValue(
              Objects.requireNonNull(response.<Map<String, Object>>getData()).get("newEvent"),
              EventHeader.class);
        });

    disposable =
    result.subscribe(eventHeader -> {
      log.info("Received Event, metadata: {}, hotel id: {}, timestamp: {}, "
              + "eventName: {}",
          eventHeader.getMetadata(), eventHeader.getHotelId(), eventHeader.getTimestamp(),
          eventHeader.getEventName());
      //Below logger is only for lower environments,
      //Pls move the below logger to trace mode for production environment.
      log.trace("Event:{}", eventHeader);
      hotelAvailabilityDbBatchService.processDbBatchUpdate(eventHeader, appKey);
    });
    log.trace("Processing successfully done!");
  }

  private Map<String, Object> getSubscriptionInput(final BigInteger offset) {

    if (isNewConnection(offset)) {

      return getInputWithoutOffset();

    } else {

      return getInputWithOffset(offset);

    }
  }

  private Map<String, Object> getInputWithOffset(final BigInteger offset) {
    log.info("getInputWithOffset invoked with offset: {}", offset);
    String offsetString = String.valueOf(offset);
    return Map.of(
        "input", Map.of(
            "chainCode", "WHBOC001",
            "offset", offsetString
            //"delta", true
        )
    );
  }

  private Map<String, Object> getInputWithoutOffset() {
    log.info("getInputWithoutOffset invoked!");
    return Map.of(
        "input", Map.of(
            "chainCode", "WHBOC001"
            //"chainCode", "WHBOC001",
            //"delta", true
        )
    );
  }

  private void reConnectOnError(
      WebSocketGraphQlClient webSocketGraphQlClient,
      Throwable throwable) {
    log.debug("current time from reConnectOnError: {}", LocalDateTime.now());
    log.error(SUBSCRIPTION_REQUEST_FAILED_WITH_ERROR, throwable.getMessage());
    boolean isInvalidToken = false;
    if (throwable.getMessage().contains(OperaSubscriptionErrors.EXPIRED_TK_INVALID_CRED)) {
      isInvalidToken = true;
    }
    disposeFlux();
    webSocketGraphQlClient.stop();
    try {
      Thread.sleep(operaProperties.getServiceUrl().getSubscriptionConnDelay());
    } catch (InterruptedException e) {
      log.warn("error occurred while asking thread to sleep: {}", e);
      Thread.currentThread().interrupt();
    }
    this.subscribeForBusinessEvents(isInvalidToken);
  }

  private BigInteger getOffset(final String appKey) {
    BigInteger lastProcessedOffset = hotelAvailabilityDbBatchService.getProcessedOffset(appKey);
    if (lastProcessedOffset != null) {
      return lastProcessedOffset.add(BigInteger.ONE);
    }
    return BigInteger.ONE;
  }

  private boolean isNewConnection(final BigInteger offset) {
    final int compareResult = offset.compareTo(BigInteger.ONE);
    return compareResult <= 0;
  }


  /*we can perform explicit cancellation on an ongoing Flux by calling dispose method on Disposable.
  When the Flux for the stream is cancelled, spring-graphql client send the "complete" message to
  the server.*/
  @PreDestroy
  private void disposeFlux() {
    if (disposable != null) {
      log.info("Sending complete message before disconnecting!");
      disposable.dispose();
    }
  }

}
