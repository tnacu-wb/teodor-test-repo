package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT_V3;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANIES;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANY;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.queryParamsToMap;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CreateCompanyResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.company.UpdateCompanyResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyDataService {

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;
  private final CacheManager cacheManager1HourCdh;

  /**
   * Get companies based on the given filter params.
   *
   * @param queryParams wrapper over query parameters
   * @param accessedBy  information about who is making the request
   * @return list of companies matching the search filters
   */
  public Optional<GetCompaniesResponse> getCompanies(GetCompaniesQueryParams queryParams,
      String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANIES);
    builder.queryParams(queryParamsToMap(queryParams));

    return cdhClient.getCDH(builder.build().toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetCompaniesResponse.class);
  }

  /**
   * Get company based on the companyId.
   *
   * @param companyId the id of the company in CDH
   * @param accessedBy information about who is making the request
   * @return the company data, if found in CDH
   */
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyId")
  public Optional<GetCompanyResponse> getCompany(String companyId,
      String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + COMPANY);
    return cdhClient.getCDH(builder.buildAndExpand(companyId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetCompanyResponse.class);
  }

  /**
   * Create a company account
   *
   * @param companyAccountRequest details of the company
   * @param accessedBy            information about who is making the request
   * @return the response for the create operation
   */
  public CreateCompanyResponse createCompanyAccount(CompanyAccountRequest companyAccountRequest,
      String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + COMPANIES);

    return cdhClient.postCDH(builder.build().toUriString(), companyAccountRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        CompanyAccountRequest.class, CreateCompanyResponse.class);
  }

  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhCompany", key = "#companyId")
  public UpdateCompanyResponse updateCompany(String companyId, CompanyAccountRequest companyUpdateRequest,
      String accessedBy) {
    processCdhEmployeeEviction(companyId, companyUpdateRequest);

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V3 + COMPANY);

    return cdhClient.putCDH(builder.buildAndExpand(companyId).toUriString(), companyUpdateRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        CompanyAccountRequest.class, UpdateCompanyResponse.class);
  }


  public void processCdhEmployeeEviction(String companyId, CompanyAccountRequest companyUpdateRequest) {
    Cache cache = cacheManager1HourCdh.getCache("CdhEmployee");
    if (cache != null && companyId != null
        && Objects.nonNull(companyUpdateRequest.getMainContact())
        && Objects.nonNull(companyUpdateRequest.getMainContact().getEmployeeAccountId())) {
      String cacheKey = companyId + ":" + companyUpdateRequest.getMainContact().getEmployeeAccountId();
      log.info("Evicting cache for key: {}", cacheKey);
      cache.evict(cacheKey);
    }
  }
}
