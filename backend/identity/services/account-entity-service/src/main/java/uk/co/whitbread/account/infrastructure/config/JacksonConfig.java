package uk.co.whitbread.account.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

  // Jackson 3 mapper for Spring MVC (used by HTTP message converters)
  // By default, FAIL_ON_NULL_FOR_PRIMITIVES is false in Jackson 2.x, but true in Jackson 3.x, so we need
  // to disable it explicitly to avoid deserialization errors when null values are encountered for primitive types.
  @Bean
  @Primary
  public tools.jackson.databind.json.JsonMapper jsonMapper() {
    return tools.jackson.databind.json.JsonMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
  }
}
