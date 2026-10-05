package uk.co.whitbread.ohip.infrastructure.rest.client.token.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.infrastructure.exceptions.OhipInternalException;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.dto.TokenResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.token.service.properties.TokenServiceProperties;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenServiceClient {

  private final WebClient tokenServiceWebClient;
  private final TokenServiceProperties properties;

  public Mono<TokenResponse> getOperaAccessTokenResponse() {
    log.debug("Requesting Opera access token response from token service");

    return tokenServiceWebClient
        .get()
        .uri(properties.getTokenEndpoint())
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.error("Failed to get token from token service. Status: {}", response.statusCode());
          return Mono.error(new OhipInternalException(
              "Failed to get token from token service",
              ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION.getMessage(),
              ErrorCode.OHIP_RETRIES_EXHAUSTED_EXCEPTION.getCode()
          ));
        })
        .bodyToMono(TokenResponse.class)
        .doOnSuccess(
            response -> log.debug("Successfully retrieved token response from token service"))
        .doOnError(
            exception -> log.error("Failed to get token response from token service", exception));
  }
}
