package uk.co.whitbread.cdh.infrastructure.rest.client.account.company;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.cdh.domain.model.account.in.CompanySearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.out.CompanySearch;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class CompanyClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @Mock
  private OAuthProvider oAuthProvider;

  @Mock
  CdhApiProperties cdhApiProperties;

  @InjectMocks
  private CompanyClient companyClient;

  @Test
  void test_getCompanies_success() {
    // Arrange
    CompanySearchCriteria companySearchCriteria = getCompanySearchCriteria();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanySearch.class)).thenReturn(
        mockGetCompaniesResponse());

    // Act
    var response = companyClient.getCompanies(companySearchCriteria);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.getTotalResults());
  }

  @Test
  void test_getCompanies_failed() {
    // Arrange
    CompanySearchCriteria companySearchCriteria = getCompanySearchCriteria();
    when(cdhApiProperties.getGetCompaniesEndpoint()).thenReturn("url");
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> companyClient.getCompanies(companySearchCriteria));

    assertEquals(ErrorCode.CDH_GET_COMPANY_EXCEPTION.getCode(), exception.getErrorCode());
  }

  private static CompanySearchCriteria getCompanySearchCriteria() {
    return CompanySearchCriteria
        .builder()
        .companyName("test")
        .accessContext("test-access-context")
        .accessedBy("junit")
        .globalCompanyId(123)
        .companyType("BB")
        .addressLine1("address one")
        .addressLine2("address two")
        .addressLine3("address three")
        .addressLine4("address four")
        .addressLine5("address five")
        .cellCode("cell code")
        .countryCode("country code")
        .pageSize(1)
        .postCode("post code")
        .pageNumber(0)
        .sortBy("companyName")
        .sortDirection("asc")
        .build();
  }

  private Mono<CompanySearch> mockGetCompaniesResponse() {
    return Mono.just(CompanySearch.builder().totalResults(1).build());

  }

}
