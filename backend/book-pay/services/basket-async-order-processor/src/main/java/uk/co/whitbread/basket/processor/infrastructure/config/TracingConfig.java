package uk.co.whitbread.basket.processor.infrastructure.config;

import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

  @Bean
  @ConditionalOnMissingBean
  public Tracer tracer() {
    // Return a no-op tracer if no other tracer bean is configured
    return Tracer.NOOP;
  }
}
