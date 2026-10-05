package uk.co.whitbread.infrastructure.rest.client.accounts;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.rest.client.accounts.exceptions.AccountServiceException;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CellCode;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.Company;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetails;
import uk.co.whitbread.infrastructure.rest.client.accounts.model.out.CompanyDetailsResponse;
import uk.co.whitbread.infrastructure.rest.client.accounts.service.AccountServiceClient;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class AccountServiceClientTest {

  private static final String COMPANY_NAME = "Company Name";
  private static final String BFLEX = "BFLEX";
  private static final String AUTHORIZATION = "1234567890abcdef";
  private static final String COMPANY_ID = "COMP_abcdef_1234_5678_90abcdef";

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @InjectMocks
  private AccountServiceClient accountServiceWebClient;

  @Test
  void getCompanyDetailsResponse() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyDetailsResponse.class)).thenReturn(mockCompanyDetailsResponse());

    // Act
    var companyResponse = accountServiceWebClient.getCompanyDetails(AUTHORIZATION, COMPANY_ID);

    // Assert
    assertThat(companyResponse, notNullValue());
    assertThat(companyResponse.getCompanyCellCodes().size(), is(1));
    assertThat(companyResponse.getCompanyCellCodes().get(0).getDescription(), is(BFLEX));
    assertThat(companyResponse.getRequestedCompany().getCompanyDetails().getCompanyName(), is(COMPANY_NAME));
  }

  @Test
  void getCompanyDetailsResponse__shouldReturnError() {
    String errorMessage = "An error occurred from Account Services!";
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(customResponseSpec.bodyToMono(CompanyDetailsResponse.class))
            .thenReturn(Mono.error(new AccountServiceException("message",
                    errorMessage, new Exception(), 1)));

    // Act
    var thrownException = assertThrowsExactly(AccountServiceException.class,
        () -> accountServiceWebClient.getCompanyDetails(AUTHORIZATION, COMPANY_ID));

    // Assert
    String debugMessage = thrownException.getDebugMessage();
    assertThat(debugMessage, is(errorMessage));
    verifyNoMoreInteractions(webClient);
  }

  private Mono<CompanyDetailsResponse> mockCompanyDetailsResponse() {
    return Mono.just(createCompanyDetailsResponse());
  }

  private CompanyDetailsResponse createCompanyDetailsResponse() {
    CompanyDetailsResponse response = new CompanyDetailsResponse();
    Company company = new Company();
    CompanyDetails companyDetails = new CompanyDetails();
    companyDetails.setCompanyName(COMPANY_NAME);
    company.setCompanyDetails(companyDetails);
    CellCode cellCode = new CellCode();
    cellCode.setDescription(BFLEX);
    company.setCompanyCellCodes(List.of(cellCode));
    response.setRequestedCompany(company);
    response.setCompanyCellCodes(List.of(cellCode));
    return response;
  }
}
