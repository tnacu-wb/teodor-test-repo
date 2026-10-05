package uk.co.whitbread.payapp.infrastructure.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.properties.CdhAdapterProperties;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.properties.CompanyProperties;
import uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties.WorldlineProperties;

@Data
@Slf4j
@Configuration
public class PayAppWebClientConfig {

  private final WorldlineProperties worldlineProperties;
  private final CompanyProperties companyProperties;
  private final CdhAdapterProperties cdhAdapterProperties;
  private final JsonMapper jacksonJsonMapper;

  @Bean(name = "worldlineWebClient")
  public WebClient worldlineWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(worldlineProperties.getUrl())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .filter(logResponse())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "companyWebClient")
  public WebClient companyWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(companyProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .filter(logResponseCompany())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "cdhAdapterWebClient")
  public WebClient cdhAdapterWebClient(WebClient.Builder webClientBuilder, HttpClient httpClient) {
    return webClientBuilder
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl(cdhAdapterProperties.getHost())
        .codecs(configurer -> configureCodecs(configurer, jacksonJsonMapper))
        .filter(logRequest())
        .filter(logResponseCdhAdapter())
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  private void configureCodecs(CodecConfigurer configurer, JsonMapper jsonMapper) {
    JsonMapper configuredMapper = jsonMapper.rebuild()
        .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .build();
    configurer.registerDefaults(false);
    configurer.customCodecs()
        .register(new JacksonJsonEncoder(configuredMapper, MediaType.APPLICATION_JSON));
    configurer.customCodecs()
        .register(new JacksonJsonDecoder(configuredMapper, MediaType.APPLICATION_JSON));
  }

  private ExchangeFilterFunction logRequest() {

    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      logRequest(clientRequest);
      return Mono.just(clientRequest);
    });
  }

  private void logRequest(ClientRequest clientRequest) {

    log.info("{} {} {} {} \n--Headers {}", clientRequest.method().name(),
        clientRequest.url().getHost(), clientRequest.url().getPath(),
        clientRequest.url().getQuery(), clientRequest.headers());
  }

  private ExchangeFilterFunction logResponse() {

    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("WorldLine API Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

  private ExchangeFilterFunction logResponseCompany() {

    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("Company Service Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

  private ExchangeFilterFunction logResponseCdhAdapter() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      log.info("Cdh Adapter Service Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }
}
