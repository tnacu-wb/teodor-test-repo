package uk.co.whitbread.hotel.card.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class HotelCardWebClientConfig {

  private static final int CDH_MAX_IN_MEMORY_SIZE = 16 * 1024 * 1024;

  @Bean
  public ExchangeStrategies exchangeStrategies(JsonMapper jsonMapper) {
    return ExchangeStrategies.builder()
        .codecs(configurer -> configureCodecs(configurer, jsonMapper))
        .build();
  }

  private void configureCodecs(CodecConfigurer configurer, JsonMapper jsonMapper) {
    // Create a configured mapper with FAIL_ON_NULL_FOR_PRIMITIVES disabled
    // This prevents deserialization errors when CDH API returns null for primitive boolean fields
    JsonMapper configuredMapper = jsonMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();

    configurer.defaultCodecs().maxInMemorySize(CDH_MAX_IN_MEMORY_SIZE);
    configurer.defaultCodecs()
        .jacksonJsonEncoder(new JacksonJsonEncoder(configuredMapper, MediaType.APPLICATION_JSON));
    configurer.defaultCodecs()
        .jacksonJsonDecoder(new JacksonJsonDecoder(configuredMapper, MediaType.APPLICATION_JSON));
  }
}
