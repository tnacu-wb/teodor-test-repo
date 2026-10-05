package uk.co.whitbread.reservation.domain.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AppConfig {

  // Jackson 2 ObjectMapper for backward compatibility
  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    return mapper;
  }

  // Jackson 3 JsonMapper used by Spring Boot 4 for HTTP message conversion.
  // In Jackson 3.x, FAIL_ON_NULL_FOR_PRIMITIVES is true by default (unlike Jackson 2.x),
  // so we disable it explicitly to avoid deserialization errors when null values are encountered.
  @Bean
  @Primary
  public tools.jackson.databind.json.JsonMapper jsonMapper() {
    return tools.jackson.databind.json.JsonMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
  }
}