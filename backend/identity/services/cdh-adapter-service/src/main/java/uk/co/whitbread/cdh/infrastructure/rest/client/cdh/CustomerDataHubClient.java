package uk.co.whitbread.cdh.infrastructure.rest.client.cdh;

import jakarta.annotation.PostConstruct;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiOauthProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerDataHubClient {

  private WebClient webClient;
  private static final String BEARER_PREFIX = "Bearer";
  private static final String ACCESSED_BY = "AccessedBy";
  private static final String ACCESS_CONTEXT = "AccessContext";
  private final OAuthProvider oAuthProvider;
  private final CdhApiOauthProperties cdhApiOauthProperties;
  private final CdhApiProperties cdhApiProperties;
  private final WebClientProperties webClientProperties;

  @PostConstruct
  private void init() {
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
        .codecs(
            codecs -> codecs.defaultCodecs()
                .maxInMemorySize(webClientProperties.getSize() * 1024 * 1024))
        .build();
    webClient = WebClient.builder()
        .filter(logRequest())
        .filter(logResponse())
        .filter(getToken())
        .exchangeStrategies(strategies)
        .build();
  }

  public <T> T getCdh(String url, Class<T> responseType) {
    return webClient
        .get()
        .uri(url)
        .header(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
            cdhApiOauthProperties.getBookingSubscriptionKey())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue())
        .retrieve()
        .onStatus(CustomerDataHubClient::isGetError, clientResponse ->
            throwCdhException(clientResponse, ErrorCode.CDH_GET_EXCEPTION,
                String.format("Exception from CDH on %s", url)))
        .onStatus(CustomerDataHubClient::isNotFound, clientResponse -> Mono.empty())
        .bodyToMono(responseType)
        .block();
  }

  public ReservationSearch postCdh(String url, ReservationSearchCriteria requestBody) {
    return webClient
        .post()
        .uri(url)
        .contentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
        .header(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
            cdhApiOauthProperties.getBookingSubscriptionKey())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue())
        .body(Mono.just(requestBody), ReservationSearchCriteria.class)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("No data found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::is5xxServerError, response -> {
          CDHException cdhException = new CDHException(ErrorCode.CDH_SEARCH_COMPANY_EXCEPTION,
              String.format("Retrieved exception from CDH on %s", url));
          return Mono.error(cdhException);
        })
        .bodyToMono(ReservationSearch.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
  }

  public <T, R> R postCdh(String url, T requestBody, Class<R> responseType, String accessedBy,
      String accessContext) {
    return webClient
        .post()
        .uri(url)
        .contentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
        .header(cdhApiOauthProperties.getSubscriptionKeyHeaderName(),
            cdhApiOauthProperties.getBookingSubscriptionKey())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(cdhApiProperties.getRequestHeaderName(), cdhApiProperties.getRequestHeaderValue())
        .header(ACCESSED_BY, accessedBy)
        .header(ACCESS_CONTEXT, accessContext)
        .body(Mono.just(requestBody), Object.class)
        .retrieve()
        .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful),
            clientResponse ->
                throwCdhException(clientResponse, ErrorCode.CDH_POST_EXCEPTION,
                    String.format("Exception from CDH on %s", url)))
        .bodyToMono(responseType)
        .doOnError(exception -> ExceptionLogger.log(log, exception))
        .block();
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
      log.info("CDH API Response: {}", clientResponse.statusCode());
      return Mono.just(clientResponse);
    });
  }

  private ExchangeFilterFunction getToken() {

    return (request, next) -> next.exchange(request)
        .flatMap((Function<ClientResponse, Mono<ClientResponse>>) clientResponse -> {
          if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
            log.debug("401 Unauthorised - refreshing Auth Token");
            String newToken = oAuthProvider.getNewBearerToken();
            ClientRequest retryRequest = ClientRequest.from(request)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(newToken))
                .build();
            logRequest(retryRequest);
            return next.exchange(retryRequest);
          } else {
            return Mono.just(clientResponse);
          }
        });
  }

  private static void logErrorResponse(ClientResponse response) {
    log.error("CDH API Response --Status: {}; --Headers: {}", response.statusCode(),
        response.headers().asHttpHeaders());
    response.bodyToMono(Object.class)
        .subscribe(body -> log.error("Response body: {}", body));
  }

  private Mono<CDHException> throwCdhException(ClientResponse response, ErrorCode error,
      String debugMessage) {
    logErrorResponse(response);
    CDHException cdhException = new CDHException(error, debugMessage);
    return Mono.error(cdhException);
  }

  private static boolean isGetError(HttpStatusCode status) {
    return !(status.is2xxSuccessful() || status == HttpStatus.NOT_FOUND);
  }

  private static boolean isNotFound(HttpStatusCode status) {
    return status == HttpStatus.NOT_FOUND;
  }
}
