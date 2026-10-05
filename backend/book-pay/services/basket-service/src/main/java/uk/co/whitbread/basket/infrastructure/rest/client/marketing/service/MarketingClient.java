package uk.co.whitbread.basket.infrastructure.rest.client.marketing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.domain.exception.ErrorCode;
import uk.co.whitbread.basket.domain.exception.MarketingException;
import uk.co.whitbread.basket.generated.models.marketing.UpdatePreferencesRequest;
import uk.co.whitbread.basket.infrastructure.rest.client.marketing.service.properties.MarketingClientProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Component
@Slf4j
public class MarketingClient {

  private final WebClient marketingWebClient;
  private final MarketingClientProperties marketingClientProperties;

  public MarketingClient(
      @Qualifier("marketingWebClient") WebClient marketingWebClient,
      MarketingClientProperties marketingClientProperties) {
    this.marketingWebClient = marketingWebClient;
    this.marketingClientProperties = marketingClientProperties;
  }

  public void updateMarketingPreferences(String contactType, String contactValue,
                                         UpdatePreferencesRequest updatePreferencesRequest) {
    marketingWebClient
        .put()
        .uri(uriBuilder -> uriBuilder.path(marketingClientProperties.getMarketingNewsletterEndpoint())
            .build(contactType, contactValue))
        .body(Mono.just(updatePreferencesRequest), UpdatePreferencesRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return Mono.error(
              new MarketingException(ErrorCode.MARKET_SERVICE_EXCEPTION,
                  "An error was returned by Marketing Service! Error while updating preferences"));
        })
        .toBodilessEntity()
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }
}
