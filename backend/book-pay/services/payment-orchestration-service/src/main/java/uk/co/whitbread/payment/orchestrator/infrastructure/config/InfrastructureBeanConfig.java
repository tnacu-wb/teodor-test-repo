package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import java.time.Duration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.payment.orchestrator.domain.logic.PaymentOrchestrationInPortImpl;
import uk.co.whitbread.payment.orchestrator.domain.logic.WebhookInPortImpl;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.PaymentOrchestrationInPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.primary.WebhookInPort;
import uk.co.whitbread.payment.orchestrator.domain.ports.secondary.PaymentWorkflowPort;

/**
 * Central bean wiring for domain logic implementations.
 *
 * <p>Domain logic classes have no Spring annotations — they are
 * instantiated here to keep the domain layer framework-agnostic.
 */
@Configuration
public class InfrastructureBeanConfig {

  private final ClientHttpRequestFactoryBuilder<?> requestFactoryBuilder;
  private final HttpClientSettings defaultHttpClientSettings;

  public InfrastructureBeanConfig(
      ClientHttpRequestFactoryBuilder<?> requestFactoryBuilder,
      HttpClientSettings defaultHttpClientSettings) {
    this.requestFactoryBuilder = requestFactoryBuilder;
    this.defaultHttpClientSettings = defaultHttpClientSettings;
  }

  @Bean
  public PaymentOrchestrationInPort paymentOrchestrationInPort(
      PaymentWorkflowPort paymentWorkflowPort) {
    return new PaymentOrchestrationInPortImpl(paymentWorkflowPort);
  }

  /**
   * Creates the inbound webhook use case, wiring it to the payment workflow port.
   *
   * @param paymentWorkflowPort the port used to signal the payment workflow with webhook events
   * @return the webhook primary port implementation
   */
  @Bean
  public WebhookInPort webhookInPort(PaymentWorkflowPort paymentWorkflowPort) {
    return new WebhookInPortImpl(paymentWorkflowPort);
  }

  /**
   * Creates a RestClient pre-configured for Datatrans API calls.
   *
   * @param builder the auto-configured RestClient builder
   * @param properties Datatrans configuration properties
   * @return a named RestClient instance for Datatrans integration
   */
  @Bean(name = "datatransRestClient")
  public RestClient datatransRestClient(RestClient.Builder builder,
      DatatransProperties properties) {
    return restClient(builder, properties.getBaseUrl(),
        properties.getConnectTimeout(), properties.getReadTimeout());
  }

  /**
   * Creates a RestClient pre-configured for Hotel Reservation Entity Service calls.
   *
   * @param builder the auto-configured RestClient builder
   * @param properties reservation service configuration properties
   * @return a named RestClient instance for reservation service integration
   */
  @Bean(name = "reservationRestClient")
  public RestClient reservationRestClient(RestClient.Builder builder,
      ReservationProperties properties) {
    return restClient(builder, properties.getHost(),
        properties.getConnectTimeout(), properties.getReadTimeout());
  }

  /**
   * Creates a RestClient pre-configured for Payment Method Entity Service calls.
   *
   * @param builder the auto-configured RestClient builder
   * @param properties payment method service configuration properties
   * @return a named RestClient instance for payment method service integration
   */
  @Bean(name = "paymentMethodRestClient")
  public RestClient paymentMethodRestClient(RestClient.Builder builder,
      PaymentMethodProperties properties) {
    return restClient(builder, properties.getHost(),
        properties.getConnectTimeout(), properties.getReadTimeout());
  }

  /**
   * Creates a RestClient pre-configured for Basket Service calls.
   *
   * @param builder the auto-configured RestClient builder
   * @param properties basket service configuration properties
   * @return a named RestClient instance for basket service integration
   */
  @Bean(name = "basketRestClient")
  public RestClient basketRestClient(RestClient.Builder builder,
      BasketProperties properties) {
    return restClient(builder, properties.getHost(),
        properties.getConnectTimeout(), properties.getReadTimeout());
  }

  /**
   * Builds a RestClient with a base URL and externalised timeouts.
   *
   * <p>Starts from the auto-configured {@link RestClient.Builder} so Boot's registered
   * customizers — notably observability instrumentation — are applied; building from the
   * static {@code RestClient.builder()} would silently drop outbound tracing and metrics.
   *
   * <p>The request-factory builder and baseline settings come from Spring Boot so global
   * client configuration, factory customizers, and virtual-thread support remain applied.
   * The integration-specific timeouts override only the matching baseline values.
   */
  private RestClient restClient(RestClient.Builder builder, String baseUrl,
      Duration connectTimeout, Duration readTimeout) {
    HttpClientSettings settings = defaultHttpClientSettings
        .withTimeouts(connectTimeout, readTimeout);

    return builder
        .baseUrl(baseUrl)
        .requestFactory(requestFactoryBuilder.build(settings))
        .build();
  }
}
