package uk.co.whitbread.shared.cdh;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.shared.cdh.exception.CDHException;
import uk.co.whitbread.shared.cdh.oauth.OAuthProvider;

@Slf4j
@Component
@ConditionalOnProperty(value = "cdh.api.is-oauth", havingValue = "true")
public class CustomerDataHubClient {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String CONTENT_LENGTH = "0";
  private final WebClient cdhWebClient;
  private final OAuthProvider oauthProvider;

  @Autowired
  public CustomerDataHubClient(@Qualifier("cdhWebClient") WebClient cdhWebClient, OAuthProvider oauthProvider) {
    this.cdhWebClient = cdhWebClient;
    this.oauthProvider = oauthProvider;
  }

  /**
   * Get CDH API generic call
   *
   * @param url          complete API url
   * @param headers      headers of the request
   * @param responseType returned API encapsulated object
   * @param <T>          responseType generic object
   * @return Optional of the encapsulated
   */
  public <T> Optional<T> getCDH(String url, HttpHeaders headers, Class<T> responseType) {
    return cdhWebClient
          .get()
          .uri(url)
          //OAuth token - new value generated per hour
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .retrieve()
          .onStatus(CustomerDataHubClient::isGetError, this::throwCdhException)
          .onStatus(CustomerDataHubClient::isNotFound, clientResponse -> Mono.empty())
          .bodyToMono(responseType)
          .blockOptional();
  }

  /**
   * Get List CDH API generic call
   *
   * @param url          complete API url
   * @param headers      headers of the request
   * @param responseType returned API encapsulated object
   * @param <T>          responseType generic object
   * @return List of encapsulated objects
   */
  public <T> List<T> getListCDH(String url, HttpHeaders headers, Class<T> responseType) {
    return cdhWebClient
          .get()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .retrieve()
          .onStatus(CustomerDataHubClient::isGetError, this::throwCdhException)
          .onStatus(CustomerDataHubClient::isNotFound, clientResponse -> Mono.empty())
          .bodyToFlux(responseType)
          .collectList()
          .block();
  }

  /**
   * Post CDH API generic call
   *
   * @param url          complete API url
   * @param requestBody  request body encapsulated object
   * @param headers      headers of the request
   * @param requestType  request body class
   * @param responseType returned created object encapsulated object
   * @param <U>          requestType generic object
   * @param <T>          responseType generic object
   * @return the response of the post operation
   */
  public <U, T> T postCDH(String url, U requestBody, HttpHeaders headers, Class<U> requestType,
        Class<T> responseType) {
    return cdhWebClient
          .post()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .body(Mono.just(requestBody), requestType)
          .retrieve()
          .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful), this::throwCdhException)
          .bodyToMono(responseType)
          .block();
  }

  /**
   * Post CDH API generic call with optional response handling. Returns empty on 404, matching the
   * getCDH contract.
   *
   * @param url          complete API url
   * @param requestBody  request body encapsulated object
   * @param headers      headers of the request
   * @param requestType  request body class
   * @param responseType returned created object encapsulated object
   * @param <U>          requestType generic object
   * @param <T>          responseType generic object
   * @return Optional of the response
   */
  public <U, T> Optional<T> postCDHOptional(String url, U requestBody, HttpHeaders headers,
        Class<U> requestType, Class<T> responseType) {
    return cdhWebClient
          .post()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .body(Mono.just(requestBody), requestType)
          .retrieve()
          .onStatus(CustomerDataHubClient::isGetError, this::throwCdhException)
          .onStatus(CustomerDataHubClient::isNotFound, clientResponse -> Mono.empty())
          .bodyToMono(responseType)
          .blockOptional();
  }

  /**
   * Put CDH API generic call
   *
   * @param url          complete API url
   * @param requestBody  request body encapsulated object
   * @param headers      headers of the request
   * @param requestType  request body class
   * @param responseType returned created object encapsulated object
   * @param <U>          requestType generic object
   * @param <T>          responseType generic object
   * @return the response of the put operation
   */
  public <U, T> T putCDH(String url, U requestBody, HttpHeaders headers, Class<U> requestType,
        Class<T> responseType) {
    return cdhWebClient
          .put()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .body(Mono.just(requestBody), requestType)
          .retrieve()
          .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful), this::throwCdhException)
          .bodyToMono(responseType)
          .block();
  }

  /**
   * Delete CDH API generic call
   *
   * @param url          complete API url
   * @param headers      headers of the request
   * @param responseType returned deleted object encapsulated object
   * @param <T>          responseType generic object
   * @return the response of the delete operation
   */
  public <T> T deleteCDH(String url, HttpHeaders headers, Class<T> responseType) {
    return cdhWebClient
          .delete()
          .uri(url)
          .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + oauthProvider.getBearerToken())
          .header(HttpHeaders.CONTENT_LENGTH, CONTENT_LENGTH)
          .headers(httpHeaders -> httpHeaders.addAll(headers))
          .retrieve()
          .onStatus(Predicate.not(HttpStatusCode::is2xxSuccessful), this::throwCdhException)
          .bodyToMono(responseType)
          .block();
  }

  private Mono<CDHException> throwCdhException(ClientResponse response) {
    return response.bodyToMono(String.class)
        .map(body -> {
          log.error("CDH API Response --Status: {}; --Headers: {}", response.statusCode(), response.headers().asHttpHeaders());
          log.error("Response body: {}", body);
          CDHException cdhException = new CDHException().addStatus(response.statusCode().value());
          cdhException.setMessage(body);
          return cdhException;
        })
        .flatMap(Mono::error);
  }

  private static boolean isGetError(HttpStatusCode status) {
    return !(status.is2xxSuccessful() || status == HttpStatus.NOT_FOUND);
  }

  private static boolean isNotFound(HttpStatusCode status) {
    return status == HttpStatus.NOT_FOUND;
  }
}
