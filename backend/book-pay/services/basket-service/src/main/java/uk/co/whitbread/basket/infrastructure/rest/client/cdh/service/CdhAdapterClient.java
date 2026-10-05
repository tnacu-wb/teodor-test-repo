package uk.co.whitbread.basket.infrastructure.rest.client.cdh.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchCriteriaDto;
import uk.co.whitbread.basket.generated.models.cdh.CompanySearchResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.exceptions.CdhClientException;
import uk.co.whitbread.basket.infrastructure.rest.client.cdh.service.properties.CdhAdapterProperties;
import uk.co.whitbread.basket.infrastructure.rest.utils.WebClientUtils;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private final CdhAdapterProperties cdhAdapterProperties;
  private final WebClient cdhAdapterWebClient;

  public CompanySearchResponseDto searchCompanies(CompanySearchCriteriaDto request) {
    log.info("Searching for companies in CDH service with globalCompanyId = {}",
            request.getGlobalCompanyId());

    return cdhAdapterWebClient
        .post()
        .uri(cdhAdapterProperties.getCompaniesSearchEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CompanySearchCriteriaDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorResponse(log, response);
          return response.bodyToMono(CdhClientException.class);
        })
        .bodyToMono(CompanySearchResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while retrieving companies details from CDH"))
        .block();
  }
}
