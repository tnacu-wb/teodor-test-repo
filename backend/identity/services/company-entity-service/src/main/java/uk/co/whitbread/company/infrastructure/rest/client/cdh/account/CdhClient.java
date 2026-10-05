package uk.co.whitbread.company.infrastructure.rest.client.cdh.account;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.company.domain.model.in.CompaniesSearchRequest;
import uk.co.whitbread.company.exceptions.CdhCompaniesException;
import uk.co.whitbread.company.infrastructure.rest.client.cdh.config.CdhProperties;
import uk.co.whitbread.company.infrastructure.rest.client.utils.WebClientUtils;

@Component
@Slf4j
public class CdhClient {

  private final WebClient cdhServiceWebClient;
  private final CdhProperties cdhProperties;

  public CdhClient(
      @Qualifier("cdhServiceWebClient") WebClient cdhServiceWebClient,
      CdhProperties cdhProperties) {
    this.cdhServiceWebClient = cdhServiceWebClient;
    this.cdhProperties = cdhProperties;
  }

  public CompanySearchResponseDto getCompanies(final CompaniesSearchRequest companiesSearchRequest) {
    return cdhServiceWebClient
        .post()
        .uri(uriBuilder ->
            uriBuilder.path(cdhProperties.getCompaniesEndpoint()).build())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(companiesSearchRequest), CompaniesSearchRequest.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return response.bodyToMono(CdhCompaniesException.class)
              .onErrorResume(e -> Mono.error(
                  new CdhCompaniesException(
                      "CDH request failed with HTTP " + response.statusCode(),
                      "Error parsing response: " + e.getMessage(),
                      e,
                      response.statusCode().value()
                  )
              ));
        })
        .bodyToMono(CompanySearchResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format(
                "Error trying to search companies from CDH for CompaniesRequest=%s."
                    + "An error occurred trying to search companies from CDH!",
                companiesSearchRequest)))
        .block();
  }
}
