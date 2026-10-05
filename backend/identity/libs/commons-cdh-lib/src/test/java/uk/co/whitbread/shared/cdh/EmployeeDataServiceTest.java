package uk.co.whitbread.shared.cdh;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.shared.cdh.RegistrationDataService.REGISTRATION_DETAILS_CACHE;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountRequest;
import uk.co.whitbread.shared.cdh.model.EmployeeAccountResponse;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyRequest;
import uk.co.whitbread.shared.cdh.model.GenerateActivationKeyResponse;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCompanyEmployeesV2Request;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesQueryParams;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;
import uk.co.whitbread.shared.cdh.model.TetheredUserRequest;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;
import uk.co.whitbread.shared.cdh.properties.UriPaths;
import uk.co.whitbread.shared.cdh.properties.UriQueryParams;

@ExtendWith(MockitoExtension.class)
class EmployeeDataServiceTest {

  private static final String COMPANY_ACCOUNT_ID = "COMP5f2e7e80-b4fd-42e9-958a-b9b24b61a9d5";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMPL97aa5ff0-038a-4260-bcfd-cbf8b20f236a";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String ACTIVATION_KEY = "5ySOct+hB0K+oI7gUrnuGQ";
  private static final String SEARCH_CRITERIA = "John";
  private static final String EMAIL_ADDRESS = ACCESSED_BY;
  private static final String PAGE_TOKEN = "page_token";
  private static final String ACCESS_CONTEXT = "InBusiness";
  private static final int PAGE_SIZE = 20;
  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private CdhApiProperties cdhApiProperties;

  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private CustomerDataHubClient cdhClient;

  @InjectMocks
  private EmployeeDataService employeeDataService;

  @BeforeEach
  void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void getEmployee_success() throws IOException {
    when(cdhClient.getCDH(anyString(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeeResponse.class))).thenReturn(Optional.ofNullable(buildGetEmployeeResponse()));

    final var employeeAccount = employeeDataService.getEmployee(COMPANY_ACCOUNT_ID,
        EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    assertThat(employeeAccount.isPresent(), is(true));
    final var getEmployeeResponse = employeeAccount.get();
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getEmployee_urlIsBuiltCorrectly() {
    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    employeeDataService.getEmployee(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    verify(cdhClient).getCDH(urlCaptor.capture(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeeResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "/" + EMPLOYEE_ACCOUNT_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getEmployeeV2_success() throws IOException {
    when(cdhClient.getCDH(anyString(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeeResponse.class))).thenReturn(Optional.ofNullable(buildGetEmployeeResponse()));

    final var employeeAccount = employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID,
        EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    assertThat(employeeAccount.isPresent(), is(true));
    final var getEmployeeResponse = employeeAccount.get();
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getEmployeeV2_urlIsBuiltCorrectly() {
    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    employeeDataService.getEmployeeV2(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    verify(cdhClient).getCDH(urlCaptor.capture(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeeResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT_V2
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "/" + EMPLOYEE_ACCOUNT_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getCompanyEmployees_success() throws IOException {
    when(cdhClient.getCDH(anyString(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeesResponse.class))).thenReturn(Optional.of(buildGetEmployeesResponse()));

    final var employeeAccounts = employeeDataService.getCompanyEmployees(COMPANY_ACCOUNT_ID,
        GetCompanyEmployeesQueryParams.builder().build(), ACCESSED_BY);

    assertThat(employeeAccounts.isEmpty(), is(false));
    final GetEmployeeResponse getEmployeeResponse = employeeAccounts.get().getResults().get(0);
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getCompanyEmployees_urlIsBuiltCorrectly() {
    final GetCompanyEmployeesQueryParams queryParameters = GetCompanyEmployeesQueryParams.builder()
        .searchCriteria(SEARCH_CRITERIA)
        .awaitingApproval(true)
        .pageToken(PAGE_TOKEN)
        .pageSize(PAGE_SIZE)
        .build();

    employeeDataService.getCompanyEmployees(COMPANY_ACCOUNT_ID, queryParameters, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).getCDH(urlCaptor.capture(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeesResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "?"
        + UriQueryParams.SEARCH_CRITERIA + "=" + SEARCH_CRITERIA + "&"
        + UriQueryParams.AWAITING_APPROVAL + "=" + true + "&"
        + UriQueryParams.PAGE_TOKEN + "=" + PAGE_TOKEN + "&"
        + UriQueryParams.PAGE_SIZE + "=" + PAGE_SIZE;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getCompanyEmployeesV2_success() throws IOException {
    when(cdhClient.postCDHOptional(anyString(), any(), any(), any(), any()))
        .thenReturn(Optional.of(buildGetEmployeesResponse()));

    final var employeeAccounts = employeeDataService.getCompanyEmployeesV2(COMPANY_ACCOUNT_ID,
        GetCompanyEmployeesQueryParams.builder().build(), ACCESSED_BY);

    assertThat(employeeAccounts.isEmpty(), is(false));
    final GetEmployeeResponse getEmployeeResponse = employeeAccounts.get().getResults().get(0);
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getCompanyEmployeesV2_urlIsBuiltCorrectly() {
    final GetCompanyEmployeesQueryParams queryParameters = GetCompanyEmployeesQueryParams.builder()
        .searchCriteria(SEARCH_CRITERIA)
        .accessLevel(AccessLevel.SUPER)
        .awaitingApproval(true)
        .pageToken(PAGE_TOKEN)
        .pageSize(PAGE_SIZE)
        .build();

    employeeDataService.getCompanyEmployeesV2(COMPANY_ACCOUNT_ID, queryParameters, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).postCDHOptional(urlCaptor.capture(), any(), any(), any(), any());

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT_V2
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getEmployees_success() throws IOException {
    when(cdhClient.getCDH(anyString(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeesResponse.class))).thenReturn(Optional.of(buildGetEmployeesResponse()));

    final var employeeAccounts = employeeDataService.getEmployees(
        GetEmployeesQueryParams.builder().build(), ACCESSED_BY);

    assertThat(employeeAccounts.isEmpty(), is(false));
    assertFalse(employeeAccounts.get().getResults().isEmpty());

    final GetEmployeeResponse getEmployeeResponse = employeeAccounts.get().getResults().get(0);
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getEmployees_urlIsBuiltCorrectly() {
    final GetEmployeesQueryParams queryParameters = GetEmployeesQueryParams.builder()
        .emailAddress(EMAIL_ADDRESS)
        .activationKey(ACTIVATION_KEY)
        .pageToken(PAGE_TOKEN)
        .pageSize(PAGE_SIZE)
        .build();

    employeeDataService.getEmployees(queryParameters, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).getCDH(urlCaptor.capture(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GetEmployeesResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.EMPLOYEES + "?"
        + UriQueryParams.EMAIL_ADDRESS + "=" + EMAIL_ADDRESS + "&"
        + UriQueryParams.ACTIVATION_KEY + "=" + ACTIVATION_KEY + "&"
        + UriQueryParams.PAGE_TOKEN + "=" + PAGE_TOKEN + "&"
        + UriQueryParams.PAGE_SIZE + "=" + PAGE_SIZE;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getEmployeesV2_success() throws IOException {
    when(cdhClient.postCDHOptional(anyString(), any(), any(), any(), any()))
        .thenReturn(Optional.of(buildGetEmployeesResponse()));

    final var employeeAccounts = employeeDataService.getEmployeesV2(
        GetEmployeesQueryParams.builder().build(), ACCESSED_BY);

    assertThat(employeeAccounts.isEmpty(), is(false));
    assertFalse(employeeAccounts.get().getResults().isEmpty());

    final GetEmployeeResponse getEmployeeResponse = employeeAccounts.get().getResults().get(0);
    assertThat(getEmployeeResponse.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(getEmployeeResponse.getCompanyAccountId(), is(COMPANY_ACCOUNT_ID));
  }

  @Test
  void getEmployeesV2_urlIsBuiltCorrectly() {
    final GetEmployeesQueryParams queryParameters = GetEmployeesQueryParams.builder()
        .emailAddress(EMAIL_ADDRESS)
        .activationKey(ACTIVATION_KEY)
        .pageToken(PAGE_TOKEN)
        .pageSize(PAGE_SIZE)
        .build();

    employeeDataService.getEmployeesV2(queryParameters, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).postCDHOptional(urlCaptor.capture(), any(), any(), any(), any());

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT_V2
        + UriPaths.EMPLOYEES;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void getCompanyEmployeesV2_pageSizeZero_usesFallback() {
    final GetCompanyEmployeesQueryParams queryParameters = GetCompanyEmployeesQueryParams.builder()
        .pageSize(0)
        .build();

    employeeDataService.getCompanyEmployeesV2(COMPANY_ACCOUNT_ID, queryParameters, ACCESSED_BY);

    final var requestBodyCaptor = ArgumentCaptor.forClass(GetCompanyEmployeesV2Request.class);
    verify(cdhClient).postCDHOptional(anyString(), requestBodyCaptor.capture(), any(), any(), any());

    assertThat(requestBodyCaptor.getValue().getPageSize(),
        is(EmployeeDataService.MAX_PAGE_SIZE_FALLBACK));
  }

  @Test
  void getCompanyEmployeesV2_pageSizeNull_passesNull() {
    final GetCompanyEmployeesQueryParams queryParameters = GetCompanyEmployeesQueryParams.builder()
        .pageSize(null)
        .build();

    employeeDataService.getCompanyEmployeesV2(COMPANY_ACCOUNT_ID, queryParameters, ACCESSED_BY);

    final var requestBodyCaptor = ArgumentCaptor.forClass(GetCompanyEmployeesV2Request.class);
    verify(cdhClient).postCDHOptional(anyString(), requestBodyCaptor.capture(), any(), any(), any());

    assertThat(requestBodyCaptor.getValue().getPageSize(), is(nullValue()));
  }

  @Test
  void getCompanyEmployeesV2_pageSizeProvided_usesProvidedValue() {
    final GetCompanyEmployeesQueryParams queryParameters = GetCompanyEmployeesQueryParams.builder()
        .pageSize(PAGE_SIZE)
        .build();

    employeeDataService.getCompanyEmployeesV2(COMPANY_ACCOUNT_ID, queryParameters, ACCESSED_BY);

    final var requestBodyCaptor = ArgumentCaptor.forClass(GetCompanyEmployeesV2Request.class);
    verify(cdhClient).postCDHOptional(anyString(), requestBodyCaptor.capture(), any(), any(), any());

    assertThat(requestBodyCaptor.getValue().getPageSize(), is(PAGE_SIZE));
  }

  @Test
  void createEmployeeAccount_success() throws IOException {
    final var createEmployeeAccountRequest = buildEmployeeAccountRequest();
    when(cdhClient.postCDH(anyString(), eq(createEmployeeAccountRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(EmployeeAccountRequest.class), eq(EmployeeAccountResponse.class))).thenReturn(
        buildEmployeeAccountResponse());

    final var employeeAccount = employeeDataService.createEmployeeAccount(
        COMPANY_ACCOUNT_ID, createEmployeeAccountRequest, ACCESSED_BY);

    assertThat(employeeAccount.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
    assertThat(employeeAccount.getActivationKey(), is(ACTIVATION_KEY));
  }

  @Test
  void createEmployeeAccount_urlIsBuiltCorrectly() throws IOException {
    final var createEmployeeAccountRequest = buildEmployeeAccountRequest();
    employeeDataService.createEmployeeAccount(COMPANY_ACCOUNT_ID, createEmployeeAccountRequest,
        ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).postCDH(urlCaptor.capture(), eq(createEmployeeAccountRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(EmployeeAccountRequest.class), eq(EmployeeAccountResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void updateEmployeeAccount_success() throws IOException {
    final var updateEmployeeAccountRequest = buildEmployeeAccountRequest();
    when(cdhClient.putCDH(anyString(), eq(updateEmployeeAccountRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(EmployeeAccountRequest.class), eq(EmployeeAccountResponse.class))).thenReturn(
        buildEmployeeAccountResponse());

    final var employeeAccount = employeeDataService.updateEmployeeAccount(
        COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, updateEmployeeAccountRequest, ACCESSED_BY);

    assertThat(employeeAccount.getEmployeeAccountId(), is(EMPLOYEE_ACCOUNT_ID));
  }

  @Test
  void updateEmployeeAccount_urlIsBuiltCorrectly() throws IOException {
    final var updateEmployeeAccountRequest = buildEmployeeAccountRequest();
    employeeDataService.updateEmployeeAccount(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
        updateEmployeeAccountRequest, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).putCDH(urlCaptor.capture(), eq(updateEmployeeAccountRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(EmployeeAccountRequest.class), eq(EmployeeAccountResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT_V2
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "/" + EMPLOYEE_ACCOUNT_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void deleteEmployeeAccount_success() {
    when(cdhClient.deleteCDH(anyString(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(Void.class))).thenReturn(null);
    final var response = employeeDataService.deleteEmployeeAccount(COMPANY_ACCOUNT_ID,
        EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);
    assertThat(response, nullValue());
  }

  @Test
  void deleteEmployeeAccount_urlIsBuiltCorrectly() {
    employeeDataService.deleteEmployeeAccount(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).deleteCDH(urlCaptor.capture(),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)), eq(Void.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "/" + EMPLOYEE_ACCOUNT_ID;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void generateNewActivationKey_success() throws IOException {
    final var generateActivationKeyRequest = buildGenerateActivationKeyRequest();
    when(cdhClient.putCDH(anyString(), eq(generateActivationKeyRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GenerateActivationKeyRequest.class),
        eq(GenerateActivationKeyResponse.class))).thenReturn(buildGenerateActivationKeyResponse());

    final var activationKeyResponse = employeeDataService.generateNewActivationKey(
        COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID, generateActivationKeyRequest, ACCESSED_BY);

    assertThat(activationKeyResponse.getActivationKey(), is(ACTIVATION_KEY));
  }

  @Test
  void generateNewActivationKey_urlIsBuiltCorrectly() throws IOException {
    final var generateActivationKeyRequest = buildGenerateActivationKeyRequest();
    employeeDataService.generateNewActivationKey(COMPANY_ACCOUNT_ID, EMPLOYEE_ACCOUNT_ID,
        generateActivationKeyRequest, ACCESSED_BY);

    final var urlCaptor = ArgumentCaptor.forClass(String.class);
    verify(cdhClient).putCDH(urlCaptor.capture(), eq(generateActivationKeyRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY)),
        eq(GenerateActivationKeyRequest.class), eq(GenerateActivationKeyResponse.class));

    final var url = urlCaptor.getValue();

    final var expectedUrl = cdhApiProperties.getHost()
        + UriPaths.ACCOUNT_SERVICES_ENDPOINT
        + UriPaths.COMPANIES + "/" + COMPANY_ACCOUNT_ID
        + UriPaths.EMPLOYEES + "/" + EMPLOYEE_ACCOUNT_ID
        + UriPaths.ACTIVATION_KEY;

    assertThat(url, is(expectedUrl));
  }

  @Test
  void registerTetheredUser_success() throws IOException {
    final var tetheredUserRequest = buildTetheredUserRequest();
    when(cdhClient.postCDH(anyString(), eq(tetheredUserRequest),
        eq(buildHeaders(SUBSCRIPTION_KEY, ACCESSED_BY, ACCESS_CONTEXT)),
        eq(TetheredUserRequest.class),
        eq(Void.class))).thenReturn(null);

    final var response = employeeDataService.registerTetheredUser(
        tetheredUserRequest, ACCESSED_BY, ACCESS_CONTEXT);

    assertThat(response, nullValue());
  }

  @Test
  void registerTetheredUser_updatesRegistrationDetailsCache() throws IOException {
    // Arrange
    TetheredUserRequest tetheredUserRequest = buildTetheredUserRequest();
    Cache cache = mock(Cache.class);
    when(cacheManager.getCache(REGISTRATION_DETAILS_CACHE)).thenReturn(cache);

    // Act
    employeeDataService.registerTetheredUser(tetheredUserRequest, ACCESSED_BY, ACCESS_CONTEXT);

    // Assert: verify cache.get was called with the expected key
    tetheredUserRequest.getTetheredGuids().forEach(tg -> {
      String key = uk.co.whitbread.shared.cdh.utils.RegistrationDetailsKeyGenerator
          .generateKey(tetheredUserRequest.getCompanyId(), tg.getEmployeeId());
      verify(cache).evict(key);
    });
  }

  private EmployeeAccountResponse buildEmployeeAccountResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_employeeAccount_response.json"),
        EmployeeAccountResponse.class);
  }

  private EmployeeAccountRequest buildEmployeeAccountRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_employeeAccount_request.json"),
        EmployeeAccountRequest.class);
  }

  private GetEmployeeResponse buildGetEmployeeResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_employee_response.json"),
        GetEmployeeResponse.class);
  }

  private GetEmployeesResponse buildGetEmployeesResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_employees_response.json"),
        GetEmployeesResponse.class);
  }

  private GenerateActivationKeyResponse buildGenerateActivationKeyResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/generate_activationKey_response.json"),
        GenerateActivationKeyResponse.class);
  }

  private GenerateActivationKeyRequest buildGenerateActivationKeyRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/generate_activationKey_request.json"),
        GenerateActivationKeyRequest.class);
  }

  private TetheredUserRequest buildTetheredUserRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_tetheredUser_request.json"),
        TetheredUserRequest.class);
  }
}
