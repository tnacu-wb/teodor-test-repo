package uk.co.whitbread.hotel.card.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

  // Jackson 2 ObjectMapper for backward compatibility
  // Will be removed after full migration to Jackson 3
  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
    return mapper;
  }

  // Jackson 3 mapper for WebClient (used by HTTP message converters)
  // By default, FAIL_ON_NULL_FOR_PRIMITIVES is false in Jackson 2.x, but true in Jackson 3.x, so we need
  // to disable it explicitly to avoid deserialization errors when null values are encountered for primitive types.
  @Bean
  @Primary
  public tools.jackson.databind.json.JsonMapper jsonMapper() {
    return tools.jackson.databind.json.JsonMapper.builder()
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
  }
}