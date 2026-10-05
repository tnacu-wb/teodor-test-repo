package uk.co.whitbread.infrastructure.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.cfg.ConstructorDetector;

@Configuration
public class JacksonConfig {

  @Bean
  public JsonMapperBuilderCustomizer jacksonMapperCustomizer() {
    return builder -> builder
        .constructorDetector(ConstructorDetector.DEFAULT)
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
  }
}
