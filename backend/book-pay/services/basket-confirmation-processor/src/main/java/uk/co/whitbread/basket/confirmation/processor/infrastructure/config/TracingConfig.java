package uk.co.whitbread.basket.confirmation.processor.infrastructure.config;

import brave.Tracing;
import brave.sampler.Sampler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.brave.bridge.BraveBaggageManager;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for distributed tracing using Micrometer Tracing with Brave.
 */
@Configuration
public class TracingConfig {

  /**
   * Creates the Brave tracing instance.
   *
   * @return Brave Tracing instance
   */
  @Bean
  public Tracing braveTracing() {
    return Tracing.newBuilder()
        .localServiceName("basket-confirmation-processor")
        .sampler(Sampler.ALWAYS_SAMPLE)
        .build();
  }

  /**
   * Creates the Micrometer Tracer bean using Brave bridge.
   *
   * @param tracing Brave tracing instance
   * @return Micrometer Tracer
   */
  @Bean
  public io.micrometer.tracing.Tracer tracer(Tracing tracing) {
    return new BraveTracer(
        tracing.tracer(),
        new BraveCurrentTraceContext(tracing.currentTraceContext()),
        new BraveBaggageManager()
    );
  }

  /**
   * Creates an ObjectMapper bean if one is not already present.
   * Configured with common Spring Boot defaults.
   *
   * @return ObjectMapper instance
   */
  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    return mapper;
  }

  /**
   * Creates a WebClient.Builder bean if one is not already present.
   *
   * @return WebClient.Builder instance
   */
  @Bean
  @ConditionalOnMissingBean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
