package uk.co.whitbread.account.infrastructure.rest.client.marketing.service;

import static uk.co.whitbread.account.infrastructure.exception.GlobalErrorHandler.handleError;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.account.infrastructure.exception.ErrorCode;
import uk.co.whitbread.account.infrastructure.exception.ServiceException;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.model.in.MarketingPreferencesRequestV2Dto;
import uk.co.whitbread.account.infrastructure.rest.client.marketing.service.properties.MarketingPreferencesClientProperties;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
public class MarketingPreferencesClient {
  public static final String WEBCLIENT_SERVICE_AUTHENTICATION_ERROR =
      "Authentication to Webclient Service failed.";
  private static final String AZURE_FDID_HEADER = "X-Azure-FDID";
  private static final String APIM_SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";
  private final WebClient marketingPreferencesWebClient;
  private final MarketingPreferencesClientProperties marketingPreferenceClientProperties;

  public MarketingPreferencesClient(@Qualifier("marketingPreferencesWebClient") WebClient marketingPreferencesWebClient,
      MarketingPreferencesClientProperties marketingPreferenceClientProperties) {
    this.marketingPreferencesWebClient = marketingPreferencesWebClient;
    this.marketingPreferenceClientProperties = marketingPreferenceClientProperties;
  }

  public String updateMarketingPreferences(@NotNull MarketingPreferencesRequestDto marketingPreferencesRequestDto) {
    String endpointUrl = marketingPreferenceClientProperties.getNewsletterPreferencesEndpoint()
                + marketingPreferencesRequestDto.getCustomer().getCustomerId();
    return marketingPreferencesWebClient.put()
          .uri(endpointUrl)
          .contentType(MediaType.APPLICATION_JSON)
          .header(AZURE_FDID_HEADER, marketingPreferenceClientProperties.getAzureFDID())
          .header(APIM_SUBSCRIPTION_KEY_HEADER, marketingPreferenceClientProperties.getApimSubscriptionKey())
          .body(Mono.just(marketingPreferencesRequestDto), MarketingPreferencesRequestDto.class)
          .retrieve()
          .onStatus(status -> status.isSameCodeAs(HttpStatus.UNAUTHORIZED)
              || status.isSameCodeAs(HttpStatus.FORBIDDEN),
              response -> Mono.error(new ServiceException(
                  ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION,
                  WEBCLIENT_SERVICE_AUTHENTICATION_ERROR)))
          .onStatus(HttpStatusCode::isError, response -> handleError(response,
                ErrorCode.UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION,
                ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION))
          .bodyToMono(String.class)
          .defaultIfEmpty("")
          .doOnError(exception -> ExceptionLogger.log(log, exception,
                "Error while trying to update marketing preferences."))
          .block();
  }

  public String updateMarketingPreferences(@NotNull MarketingPreferencesRequestV2Dto marketingPreferencesRequestDto) {
    String endpointUrl = marketingPreferenceClientProperties.getInternalNewsletterPreferencesEndpoint();
    return marketingPreferencesWebClient.put()
        .uri(endpointUrl)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(marketingPreferencesRequestDto), MarketingPreferencesRequestV2Dto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> handleError(response,
            ErrorCode.UPDATE_MARKETING_PREFERENCES_REQUEST_INCORRECT_EXCEPTION_V2,
            ErrorCode.UPDATE_MARKETING_PREFERENCES_EXCEPTION_V2))
        .bodyToMono(String.class)
        .defaultIfEmpty("")
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            "Error while trying to update marketing preferences."))
        .block();
  }

}