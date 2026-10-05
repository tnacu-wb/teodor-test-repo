package uk.co.whitbread.cdh.infrastructure.rest.client.account.company;

import static java.util.Collections.singletonList;

import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.Company;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;

@Slf4j
@Component
public class CompanyClient {

  private static final String BEARER_PREFIX = "Bearer";
  private static final String ACCESS_CONTEXT = "AccessContext";
  private static final String ACCESSED_BY = "AccessedBy";
  private static final String EXCEPTION = "Retrieved exception from CDH, response status = %s on %s";
  private final CdhApiProperties cdhApiProperties;
  private final WebClient cdhAccountServicesWebclient;
  private final OAuthProvider oAuthProvider;

  public CompanyClient(CdhApiProperties cdhApiProperties,
                       @Qualifier("cdhAccountServicesWebclient") WebClient cdhAccountServicesWebclient,
                       OAuthProvider oauthProvider) {
    this.cdhAccountServicesWebclient = cdhAccountServicesWebclient;
    this.cdhApiProperties = cdhApiProperties;
    this.oAuthProvider = oauthProvider;
  }

  public Company getCompany(String companyAccountId, String accessContext, String accessedBy) {
    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetCompanyEndpoint())
            .build(companyAccountId))
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, accessContext)
        .header(ACCESSED_BY, accessedBy)
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("Company data not found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError,
            response -> throwCdhException(response, cdhApiProperties.getGetCompanyEndpoint()))
        .bodyToMono(Company.class)
        .block();
  }

  public CompanySearch getCompanies(CompanySearchCriteria companySearchCriteria) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    if (StringUtils.isNotEmpty(companySearchCriteria.getCompanyName())) {
      params.put("CompanyName", singletonList(companySearchCriteria.getCompanyName()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getAddressLine1())) {
      params.put("AddressLine1", singletonList(companySearchCriteria.getAddressLine1()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getAddressLine2())) {
      params.put("AddressLine2", singletonList(companySearchCriteria.getAddressLine2()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getAddressLine3())) {
      params.put("AddressLine3", singletonList(companySearchCriteria.getAddressLine3()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getAddressLine4())) {
      params.put("AddressLine3", singletonList(companySearchCriteria.getAddressLine4()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getAddressLine5())) {
      params.put("AddressLine5", singletonList(companySearchCriteria.getAddressLine5()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getCountryCode())) {
      params.put("CountryCode", singletonList(companySearchCriteria.getCountryCode()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getPostCode())) {
      params.put("PostCode", singletonList(companySearchCriteria.getPostCode()));
    }

    if (companySearchCriteria.getGlobalCompanyId() != null) {
      params.put("GlobalCompanyId", singletonList(Integer.toString(companySearchCriteria.getGlobalCompanyId())));
    }

    if (companySearchCriteria.getPageSize() != null) {
      params.put("PageSize", singletonList(Integer.toString(companySearchCriteria.getPageSize())));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getCompanyType())) {
      params.put("CompanyType", singletonList(companySearchCriteria.getCompanyType()));
    }

    if (companySearchCriteria.getPageNumber() != null) {
      params.put("PageNumber", singletonList(Integer.toString(companySearchCriteria.getPageNumber())));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getSortBy())) {
      params.put("SortBy", singletonList(companySearchCriteria.getSortBy()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getSortDirection())) {
      params.put("SortDirection", singletonList(companySearchCriteria.getSortDirection()));
    }

    if (StringUtils.isNotEmpty(companySearchCriteria.getCellCode())) {
      params.put("CellCode", singletonList(companySearchCriteria.getCellCode()));
    }

    return cdhAccountServicesWebclient
        .get()
        .uri(uriBuilder -> uriBuilder.path(cdhApiProperties.getGetCompaniesEndpoint())
            .queryParams(params)
            .build())
        .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + " " + oAuthProvider.getBearerToken())
        .header(ACCESS_CONTEXT, companySearchCriteria.getAccessContext())
        .header(ACCESSED_BY, companySearchCriteria.getAccessedBy())
        .retrieve()
        .onStatus(Predicate.isEqual(HttpStatus.NOT_FOUND), responseType -> {
          log.info("Company data not found in CDH");
          return Mono.empty();
        })
        .onStatus(HttpStatusCode::isError,
            response -> throwCdhException(response, cdhApiProperties.getGetCompaniesEndpoint()))
        .bodyToMono(CompanySearch.class)
        .block();
  }

  private Mono<CDHException> throwCdhException(ClientResponse response, String endpoint) {

    log.error("CDH API Response --Status: {}; --Headers: {}; --Endpoint: {}",
        response.statusCode(), response.headers().asHttpHeaders(), endpoint);
    CDHException cdhException = new CDHException(
        ErrorCode.CDH_GET_COMPANY_EXCEPTION,
        String.format(EXCEPTION, response.statusCode(), endpoint));
    return Mono.error(cdhException);
  }
}
