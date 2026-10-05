package uk.co.whitbread.shared.cdh;

import static java.util.Objects.nonNull;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACCOUNT_SERVICES_ENDPOINT_V2;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACTIVATE;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.ACTIVATION_KEY;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.COMPANY_ACCOUNT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.EMPLOYEES;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.EMPLOYEE_ACCOUNT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.IB_PAY_SERVICES_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.REGISTRATION;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.TETHERED_USER;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.ACCESS_LEVEL;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.ACTIVATION_KEY_V2;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.AWAITING_APPROVAL;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.EMAIL_ADDRESS;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.PAGE_SIZE;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.PAGE_TOKEN;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.SEARCH_CRITERIA;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.shared.cdh.model.CdhAccessContext;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyRequest;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyResponse;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesV2Request;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesV2Request;
import uk.co.whitbread.shared.cdh.model.TetheredGuid;
import uk.co.whitbread.shared.cdh.model.TetheredUserRequest;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.UriQueryParams;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeDataService {

  static final int MAX_PAGE_SIZE_FALLBACK = 50_000;

  private final CustomerDataHubClient cdhClient;
  private final CdhApiProperties cdhApiProperties;
  private final CdhApiOauthProperties cdhApiOauthProperties;
  private final CacheManager cacheManager1HourCdh;

  /**
   * Get employee account based on company account ID and employee account ID.
   *
   * @param companyAccountId  ID of the company in CDH
   * @param employeeAccountId ID of the employee in CDH
   * @param accessedBy        information about who is making the request
   * @return employee profile details, if found in CDH
   */
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhEmployee", key = "#companyAccountId + ':' + #employeeAccountId")
  public Optional<GetEmployeeResponse> getEmployee(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY_ACCOUNT
            + EMPLOYEE_ACCOUNT);
    return cdhClient.getCDH(
        builder.buildAndExpand(companyAccountId, employeeAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetEmployeeResponse.class);
  }

  /**
   * Get employee account based on company account ID and employee account ID using V2 endpoint.
   *
   * @param companyAccountId  ID of the company in CDH
   * @param employeeAccountId ID of the employee in CDH
   * @param accessedBy        information about who is making the request
   * @return employee profile details, if found in CDH
   */
  @Cacheable(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhEmployee", key = "#companyAccountId + ':' + #employeeAccountId")
  public Optional<GetEmployeeResponse> getEmployeeV2(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + COMPANY_ACCOUNT
            + EMPLOYEE_ACCOUNT);
    return cdhClient.getCDH(
        builder.buildAndExpand(companyAccountId, employeeAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetEmployeeResponse.class);
  }

  /**
   * Get company employees based on the given filter params.
   *
   * @param companyAccountId ID of the company in CDH
   * @param queryParameters  wrapper over query parameters: SearchCriteria, AwaitingApproval,
   *                         PageToken, PageSize
   * @param accessedBy       information about who is making the request
   * @return a list of employees matching the search filters
   */
  public Optional<GetEmployeesResponse> getCompanyEmployees(String companyAccountId,
      GetCompanyEmployeesQueryParams queryParameters, String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY_ACCOUNT + EMPLOYEES);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    final String searchCriteria = queryParameters.getSearchCriteria();
    if (StringUtils.isNotEmpty(searchCriteria)) {
      queryParams.add(SEARCH_CRITERIA, searchCriteria);
    }

    if (nonNull(queryParameters.getAccessLevel())) {
      queryParams.add(ACCESS_LEVEL, queryParameters.getAccessLevel().toString());
    }

    if (nonNull(queryParameters.getAwaitingApproval())) {
      queryParams.add(AWAITING_APPROVAL, String.valueOf(queryParameters.getAwaitingApproval()));
    } else {
      queryParams.add(AWAITING_APPROVAL, null);
    }

    final String pageToken = queryParameters.getPageToken();
    if (StringUtils.isNotEmpty(pageToken)) {
      queryParams.add(PAGE_TOKEN, pageToken);
    }

    final Integer pageSize = queryParameters.getPageSize();
    if (nonNull(pageSize) && pageSize > 0) {
      queryParams.add(PAGE_SIZE, pageSize.toString());
    }

    builder.queryParams(queryParams);

    return cdhClient.getCDH(builder.buildAndExpand(companyAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetEmployeesResponse.class);
  }

  /**
   * Get company employees using V2 endpoint (POST with request body).
   *
   * @param companyAccountId the company account ID
   * @param queryParameters  wrapper over query parameters: SearchCriteria, AccessLevel,
   *                         AwaitingApproval, PageToken, PageSize
   * @param accessedBy       information about who is making the request
   * @return a list of employees matching the search filters
   */
  public Optional<GetEmployeesResponse> getCompanyEmployeesV2(String companyAccountId,
      GetCompanyEmployeesQueryParams queryParameters, String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + COMPANY_ACCOUNT + EMPLOYEES);

    final GetCompanyEmployeesV2Request requestBody = GetCompanyEmployeesV2Request.builder()
        .searchCriteria(queryParameters.getSearchCriteria())
        .accessLevel(queryParameters.getAccessLevel() != null
            ? queryParameters.getAccessLevel().toString() : null)
        .awaitingApproval(queryParameters.getAwaitingApproval())
        .pageToken(queryParameters.getPageToken())
        .pageSize(queryParameters.getPageSize() != null && queryParameters.getPageSize() == 0
            ? Integer.valueOf(MAX_PAGE_SIZE_FALLBACK) : queryParameters.getPageSize())
        .build();

    return cdhClient.postCDHOptional(
        builder.buildAndExpand(companyAccountId).toUriString(), requestBody,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetCompanyEmployeesV2Request.class, GetEmployeesResponse.class);
  }

  /**
   * Get employees based on the given filter params.
   *
   * @param queryParameters wrapper over query parameters: EmailAddress, ActivationKey, PageToken,
   *                        PageSize
   * @param accessedBy      information about who is making the request
   * @return a list of employees matching the search filters
   */
  public Optional<GetEmployeesResponse> getEmployees(GetEmployeesQueryParams queryParameters,
      String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + EMPLOYEES);

    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

    final String emailAddress = queryParameters.getEmailAddress();
    if (StringUtils.isNotEmpty(emailAddress)) {
      queryParams.add(EMAIL_ADDRESS, emailAddress);
    }

    final String activationKey = queryParameters.getActivationKey();
    if (StringUtils.isNotEmpty(activationKey)) {
      queryParams.add(UriQueryParams.ACTIVATION_KEY, activationKey);
    }

    final String pageToken = queryParameters.getPageToken();
    if (StringUtils.isNotEmpty(pageToken)) {
      queryParams.add(PAGE_TOKEN, pageToken);
    }

    final Integer pageSize = queryParameters.getPageSize();
    if (nonNull(pageSize) && pageSize > 0) {
      queryParams.add(PAGE_SIZE, pageSize.toString());
    }

    builder.queryParams(queryParams);

    return cdhClient.getCDH(builder.build().toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetEmployeesResponse.class);
  }

  /**
   * Get employees based on the given filter params using the V2 API endpoint. This method uses a
   * POST request with a JSON body instead of GET with query parameters.
   *
   * @param queryParameters wrapper over query parameters: EmailAddress, ActivationKey, PageToken,
   *                        PageSize
   * @param accessedBy      information about who is making the request
   * @return a list of employees matching the search filters
   */
  public Optional<GetEmployeesResponse> getEmployeesV2(GetEmployeesQueryParams queryParameters,
      String accessedBy) {

    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + EMPLOYEES);

    final GetEmployeesV2Request requestBody = GetEmployeesV2Request.builder()
        .emailAddress(queryParameters.getEmailAddress())
        .activationKey(queryParameters.getActivationKey())
        .pageToken(queryParameters.getPageToken())
        .pageSize(queryParameters.getPageSize())
        .build();

    return cdhClient.postCDHOptional(builder.build().toUriString(), requestBody,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GetEmployeesV2Request.class, GetEmployeesResponse.class);
  }

  /**
   * Create an employee account.
   *
   * @param companyAccountId       the ID of the company to which the employee is added
   * @param employeeAccountRequest Profile details of the employee
   * @param accessedBy             information about who is making the request
   * @return the response for the create operation
   */
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CompanyEmployees", key = "#companyAccountId")
  public EmployeeAccountResponse createEmployeeAccount(String companyAccountId,
      EmployeeAccountRequest employeeAccountRequest, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY_ACCOUNT + EMPLOYEES);
    return cdhClient.postCDH(builder.buildAndExpand(companyAccountId).toUriString(),
        employeeAccountRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        EmployeeAccountRequest.class, EmployeeAccountResponse.class);
  }

  /**
   * Update an existent employee account.
   *
   * @param companyAccountId       the ID of the employee's company
   * @param employeeAccountId      the ID of the employee
   * @param employeeAccountRequest profile details of the customer
   * @param accessedBy             information about who is making the request
   * @return the response for the update operation
   */
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CdhEmployee", key = "#companyAccountId + ':' + #employeeAccountId")
  public EmployeeAccountResponse updateEmployeeAccount(String companyAccountId,
      String employeeAccountId, EmployeeAccountRequest employeeAccountRequest, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + COMPANY_ACCOUNT
            + EMPLOYEE_ACCOUNT);
    return cdhClient.putCDH(
        builder.buildAndExpand(companyAccountId, employeeAccountId).toUriString(),
        employeeAccountRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        EmployeeAccountRequest.class, EmployeeAccountResponse.class);
  }

  /**
   * Delete an existing employee account.
   *
   * @param companyAccountId  the ID of the employee's company
   * @param employeeAccountId the ID of the employee
   * @param accessedBy        information about who is making the request
   * @return the response for the delete operation
   */
  @CacheEvict(condition = "@isCacheEnabled.booleanValue()", cacheManager = "cacheManager1HourCdh",
      value = "CompanyEmployees", key = "#companyAccountId")
  public Void deleteEmployeeAccount(String companyAccountId,
      String employeeAccountId, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY_ACCOUNT
            + EMPLOYEE_ACCOUNT);
    return cdhClient.deleteCDH(
        builder.buildAndExpand(companyAccountId, employeeAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy), Void.class);
  }

  /* TODO
      This implementation is just a first iteration based on what is currently exposed
      in the CDH portal as a placeholder (the operation is not actually supported in CDH, yet).
      Changes will most probably have to be made when the CDH endpoint will be in final state.
   */

  /**
   * Generate a new activation key for an existent employee account.
   *
   * @param companyAccountId             the ID of the employee's company
   * @param employeeAccountId            the ID of the employee
   * @param generateActivationKeyRequest request body containing the employee ID
   * @param accessedBy                   information about who is making the request
   * @return the response for the operation
   */
  public GenerateActivationKeyResponse generateNewActivationKey(String companyAccountId,
      String employeeAccountId, GenerateActivationKeyRequest generateActivationKeyRequest,
      String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT + COMPANY_ACCOUNT + EMPLOYEE_ACCOUNT
            + ACTIVATION_KEY);
    return cdhClient.putCDH(
        builder.buildAndExpand(companyAccountId, employeeAccountId).toUriString(),
        generateActivationKeyRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy),
        GenerateActivationKeyRequest.class, GenerateActivationKeyResponse.class);
  }

  /**
   * Activate a given employee.
   *
   * @param employeeAccountId the EmployeeAccountId of the employee
   * @param activationKey     the activation key
   * @param accessedBy        information about who is making the request
   * @return the employee profile after activation.
   */
  public Optional<GetEmployeeResponse> activateEmployee(String employeeAccountId,
      String activationKey, String accessedBy) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + ACCOUNT_SERVICES_ENDPOINT_V2 + EMPLOYEE_ACCOUNT + ACTIVATE);
    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add(ACTIVATION_KEY_V2, activationKey);
    builder.queryParams(queryParams);
    return cdhClient.getCDH(builder.buildAndExpand(employeeAccountId).toUriString(),
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy,
            CdhAccessContext.BB.name()), GetEmployeeResponse.class);
  }


  /**
   * Tethered user registration.
   *
   * @param tetheredUserRequest request body containing tethered user registration information
   * @param accessedBy          information about who is making the request
   */
  public Void registerTetheredUser(TetheredUserRequest tetheredUserRequest, String accessedBy,
      String accessContext) {
    final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
        cdhApiProperties.getHost() + IB_PAY_SERVICES_ENDPOINT + REGISTRATION + TETHERED_USER);

    var response = cdhClient.postCDH(builder.build().toUriString(),
        tetheredUserRequest,
        buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
        TetheredUserRequest.class,
        Void.class);

    processTetheredUserEviction(tetheredUserRequest);

    return response;
  }

  public void processTetheredUserEviction(TetheredUserRequest request) {
    Cache cache = cacheManager1HourCdh.getCache("CdhRegistrationDetails");

    if (cache != null && request.getTetheredGuids() != null) {
      String companyId = request.getCompanyId();

      for (TetheredGuid guid : request.getTetheredGuids()) {
        String employeeId = guid.getEmployeeId();
        if (companyId != null && employeeId != null) {
          String cacheKey = companyId + ":" + employeeId;
          log.info("Evicting cache for key: {}", cacheKey);
          cache.evict(cacheKey);
        }
      }
    }
  }
}
