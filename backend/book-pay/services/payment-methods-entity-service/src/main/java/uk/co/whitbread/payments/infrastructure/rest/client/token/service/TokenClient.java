package uk.co.whitbread.payments.infrastructure.rest.client.token.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.payments.domain.exception.ErrorCode;
import uk.co.whitbread.payments.infrastructure.rest.client.config.WebClientConstants;
import uk.co.whitbread.payments.infrastructure.rest.client.token.TokenException;
import uk.co.whitbread.payments.infrastructure.rest.client.token.model.out.TokenResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.client.token.service.properties.TokenClientProperties;
import uk.co.whitbread.payments.infrastructure.rest.client.util.WebClientUtils;

@Slf4j
@Component
public class TokenClient {

  private final WebClient tokenWebClient;
  private final TokenClientProperties tokenProperties;

  public TokenClient(
      @Qualifier("tokenWebClient") WebClient tokenWebClient,
      TokenClientProperties tokenProperties) {
    this.tokenWebClient = tokenWebClient;
    this.tokenProperties = tokenProperties;
  }

  public TokenResponseDto getPaypalToken(String countryCode) {
    log.debug(
        "Entered getPaypalToken with CountryCode={}",
        countryCode);

    return tokenWebClient.get()
        .uri(uriBuilder -> uriBuilder.path(tokenProperties.getPaypalTokenEndPoint())
            .queryParam(WebClientConstants.COUNTRY_CODE, countryCode).build()).retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(new TokenException(ErrorCode.GET_PAYPAL_TOKEN_EXCEPTION,
              "Error while trying to get paypal token!"));
        })
        .bodyToMono(TokenResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception, String.format(
            "Error while trying to get paypal rule response for countryCode=%s",
            countryCode)))
        .block();
  }
}
