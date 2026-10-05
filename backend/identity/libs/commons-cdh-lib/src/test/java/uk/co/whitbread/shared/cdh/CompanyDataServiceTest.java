package uk.co.whitbread.shared.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.shared.cdh.model.company.CompanyAccountRequest;
import uk.co.whitbread.shared.cdh.model.company.CreateCompanyResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesQueryParams;
import uk.co.whitbread.shared.cdh.model.company.GetCompaniesResponse;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.company.UpdateCompanyResponse;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

@ExtendWith(MockitoExtension.class)
public class CompanyDataServiceTest {

  private static final String HOST = "https://localhost";
  private static final String SUBSCRIPTION_KEY = "subscription-key";
  private static final String ACCESSED_BY = "customer@mail.com";
  private static final String COMPANY_ACCOUNT_ID = "COMP0f57321e-8b6f-4b98-b6ae-11a94080beea";
  private static final String EMPLOYEE_ACCOUNT_ID = "EMPL9a1ba61e-7bb4-4862-8dd0-68c931c1b436";

  @Mock
  private CustomerDataHubClient cdhClient;
  @Mock
  private CdhApiProperties cdhApiProperties;
  @Mock
  private CdhApiOauthProperties cdhApiOauthProperties;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Mock
  private CacheManager cacheManager1HourCdh;

  @InjectMocks
  private CompanyDataService companyDataService;

  @BeforeEach
  void setup() {
    when(cdhApiProperties.getHost()).thenReturn(HOST);
    when(cdhApiOauthProperties.getSubscriptionKey()).thenReturn(SUBSCRIPTION_KEY);
  }

  @Test
  void getCompanies_success() throws IOException {
    var queryParams = GetCompaniesQueryParams.builder()
        .postCode("EC1N 2TD")
        .build();
    when(cdhClient.getCDH(anyString(), any(HttpHeaders.class),
        eq(GetCompaniesResponse.class))).thenReturn(Optional.of(buildCompaniesResponse()));

    var companies = companyDataService.getCompanies(queryParams, ACCESSED_BY);
    assertTrue(companies.isPresent());
    assertEquals(1, companies.get().getResults().size());
  }

  @Test
  void getCompany_success() throws IOException {
    when(cdhClient.getCDH(anyString(), any(HttpHeaders.class),
        eq(GetCompanyResponse.class))).thenReturn(Optional.of(buildCompanyResponse()));

    var company = companyDataService.getCompany(COMPANY_ACCOUNT_ID, ACCESSED_BY);
    assertNotNull(company);
  }

  @Test
  void createCompanyAccount_success() throws IOException {
    var companyRequest = buildCreateCompanyRequest();
    var companyResponse = CreateCompanyResponse.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID)
        .build();
    when(cdhClient.postCDH(anyString(), eq(companyRequest), any(HttpHeaders.class),
        eq(CompanyAccountRequest.class), eq(CreateCompanyResponse.class)))
        .thenReturn(companyResponse);

    var response = companyDataService.createCompanyAccount(companyRequest, ACCESSED_BY);
    assertEquals(COMPANY_ACCOUNT_ID, response.getCompanyAccountId());
    assertEquals(EMPLOYEE_ACCOUNT_ID, response.getEmployeeAccountId());
  }

  @Test
  void updateCompany_success() throws IOException {
    var companyRequest = buildCreateCompanyRequest();
    var updateCompanyResponse = UpdateCompanyResponse.builder().build();
    when(cdhClient.putCDH(anyString(), eq(companyRequest), any(HttpHeaders.class),
        eq(CompanyAccountRequest.class), eq(UpdateCompanyResponse.class)))
        .thenReturn(updateCompanyResponse);

    var response = companyDataService.updateCompany(COMPANY_ACCOUNT_ID, companyRequest, ACCESSED_BY);
    assertNotNull(response);
  }

  @Test
  void updateCompany_Cache() throws IOException {
    var companyRequest = buildCreateCompanyRequest();
    var updateCompanyResponse = UpdateCompanyResponse.builder().build();
    when(cdhClient.putCDH(anyString(), eq(companyRequest), any(HttpHeaders.class),
        eq(CompanyAccountRequest.class), eq(UpdateCompanyResponse.class)))
        .thenReturn(updateCompanyResponse);
    Cache cache = mock(Cache.class);
    when(cacheManager1HourCdh.getCache("CdhEmployee")).thenReturn(cache);

    var response = companyDataService.updateCompany(COMPANY_ACCOUNT_ID, companyRequest, ACCESSED_BY);
    assertNotNull(response);
    // Assert: verify cache.get was called with the expected key
    String expectedCacheKey = COMPANY_ACCOUNT_ID + ":" + companyRequest.getMainContact().getEmployeeAccountId();
    verify(cache).evict(expectedCacheKey);
  }

  private GetCompanyResponse buildCompanyResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_company_response.json"),
        GetCompanyResponse.class);
  }

  private GetCompaniesResponse buildCompaniesResponse() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/get_companies_response.json"),
        GetCompaniesResponse.class);
  }

  private CompanyAccountRequest buildCreateCompanyRequest() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/samples/create_company_request.json"),
        CompanyAccountRequest.class);
  }

}
