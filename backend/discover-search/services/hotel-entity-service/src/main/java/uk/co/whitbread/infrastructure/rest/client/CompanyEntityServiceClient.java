package uk.co.whitbread.infrastructure.rest.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.CompanyResponseDto;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.domain.exceptions.ErrorCode;
import uk.co.whitbread.infrastructure.rest.client.companyentity.exceptions.CompanyEntityServiceException;
import uk.co.whitbread.infrastructure.rest.client.companyentity.service.properties.CompanyEntityServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.utils.WebClientUtils;

@Slf4j
@RequiredArgsConstructor
public class CompanyEntityServiceClient {

  private final WebClient companyEntityServiceWebClient;
  private final CompanyEntityServiceProperties companyEntityServiceProperties;

  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager24Hours",
      value = "CompanyByIdCache", key = "#id")
  public CompanyResponseDto getCompanyById(String id) {
    log.debug("Entered getCompanyById for id={}", id);

    return companyEntityServiceWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path(companyEntityServiceProperties.getCompanyEndpoint())
            .build(id))
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          WebClientUtils.logErrorHeader(log, response);
          return Mono.error(new CompanyEntityServiceException(ErrorCode.GET_COMPANY_BY_ID_EXCEPTION,
              "Error fetching company details from Company Entity Service for id=" + id));
        })
        .bodyToMono(CompanyResponseDto.class)
        .doOnError(exception -> ExceptionLogger.log(log, exception,
            String.format("Error trying to get company details for id=%s", id)))
        .block();
  }
}

