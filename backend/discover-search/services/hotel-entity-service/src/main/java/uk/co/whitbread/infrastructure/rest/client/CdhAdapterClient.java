package uk.co.whitbread.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySuppressRatesDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.exceptions.CdhAdapterServiceException;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CdhAdapterClient {

  private static final String CDH_ACCESSED_BY = "hotel-entity-service";
  private static final String CDH_ACCESS_CONTEXT = "BB_CCUI";

  private final CdhAdapterProperties cdhAdapterProperties;
  private final WebClient cdhAdapterWebClient;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager5Minutes",
      value = "CompanySuppressRatesCache", key = "#companyId")
  public CompanySuppressRatesDto getCompanySuppressRates(String companyId) {
    return cdhAdapterWebClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path(cdhAdapterProperties.getGetCompanySuppressRatesEndpoint())
            .queryParam("accessedBy", CDH_ACCESSED_BY)
            .queryParam("accessContext", CDH_ACCESS_CONTEXT)
            .build(companyId))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return Mono.error(new CdhAdapterServiceException(ErrorCode.GET_SUPPRESSED_COMPANY_RATES_EXCEPTION,
              "Error fetching suppress rates from CDH for companyId=" + companyId));
        })
        .bodyToMono(CompanySuppressRatesDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while retrieving suppress rates from CDH for companyId=" + companyId))
        .block();
  }

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager24Hours",
      value = "CompanySearchCache", key = "#request.globalCompanyId")
  public CompanySearchResponseDto searchCompanies(CompanySearchCriteriaDto request) {
    log.info("Searching for companies in CDH service with globalCompanyId = {}",
        request.getGlobalCompanyId());
    request.accessedBy(CDH_ACCESSED_BY);

    return cdhAdapterWebClient
        .post()
        .uri(cdhAdapterProperties.getCompaniesSearchEndpoint())
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(request), CompanySearchCriteriaDto.class)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return Mono.error(new CdhAdapterServiceException(ErrorCode.GET_COMPANIES_EXCEPTION,
              "Error while retrieving companies details from CDH for companyId=" + request.getGlobalCompanyId()));
        })
        .bodyToMono(CompanySearchResponseDto.class)
        .doOnError(ex -> ExceptionLogger.log(log, ex,
            "Error while retrieving companies details from CDH"))
        .block();
  }
}