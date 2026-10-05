package uk.co.whitbread.payapp.infrastructure.rest.client.company;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDetailsResponseDto;
import uk.co.whitbread.payapp.generated.models.company.CompanyDto;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.exceptions.CompanyResponseException;
import uk.co.whitbread.payapp.infrastructure.rest.client.company.properties.CompanyProperties;

@ExtendWith(MockitoExtension.class)
class CompanyClientTest {

  @InjectMocks
  private CompanyClient companyClient;

  @Mock
  private WebClient companyWebClient;

  @Mock
  private CompanyProperties companyProperties;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  private static final String AUTHORIZATION_TOKEN = "Bearer dummy";
  private static final String COMPANY_ID = "123456";
  private static final String ERROR_MESSAGE =
      "Error while trying to get company details from company service. Response: ";

  @BeforeEach
  void init() { companyClient = new CompanyClient(companyWebClient, companyProperties); }

  @Test
  void getCompanyDetails__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(companyWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyDetailsResponseDto.class)).thenReturn(createCompanyDetailsResponseDto());

    // Act
    var getCompanyDetails = companyClient.getCompanyDetails(COMPANY_ID, AUTHORIZATION_TOKEN);

    // Assert
    assertNotNull(getCompanyDetails);
  }

  @Test
  void getCompanyDetails__shouldFail() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(companyWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyDetailsResponseDto.class))
        .thenThrow(new CompanyResponseException(ErrorCode.COMPANY_GET_ERROR, ERROR_MESSAGE));

    // Act & Assert
    assertThrows(CompanyResponseException.class, () -> {
      companyClient.getCompanyDetails(COMPANY_ID, AUTHORIZATION_TOKEN);
    });
  }

  Mono<CompanyDetailsResponseDto> createCompanyDetailsResponseDto() {
    CompanyDetailsDto companyDetailsDto = new CompanyDetailsDto();
    companyDetailsDto.setCompanyName("Company Name");

    CompanyDto companyDto = new CompanyDto();
    companyDto.setCompanyDetails(companyDetailsDto);

    CompanyDetailsResponseDto companyDetailsResponseDto = new CompanyDetailsResponseDto();
    companyDetailsResponseDto.setRequestedCompany(companyDto);

    return Mono.just(companyDetailsResponseDto);
  }

}
