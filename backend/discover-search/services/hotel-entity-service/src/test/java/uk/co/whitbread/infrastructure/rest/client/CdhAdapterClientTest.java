package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchCriteriaDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySearchResponseDto;
import uk.co.whitbread.booking.infrastructure.rest.client.cdhadapter.generated.models.CompanySuppressRatesDto;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.exceptions.CdhAdapterServiceException;
import uk.co.whitbread.infrastructure.rest.client.cdhadapter.service.properties.CdhAdapterProperties;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CdhAdapterClientTest {

  @Mock
  private CdhAdapterProperties cdhAdapterProperties;
  @Mock
  private WebClient cdhAdapterWebClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec customResponseSpec;
  @InjectMocks
  private CdhAdapterClient cdhAdapterClient;

  private static final String COMPANY_ID = "COMPANY_123";
  private static final Integer GLOBAL_COMPANY_ID = 1001;

  @Test
  void getCompanySuppressRates_ShouldReturnCompanySuppressRatesDto() {
    // Arrange
    var companySuppressRatesDto = mock(CompanySuppressRatesDto.class);
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanySuppressRatesDto.class)).thenReturn(Mono.just(companySuppressRatesDto));

    // Act
    var result = cdhAdapterClient.getCompanySuppressRates(COMPANY_ID);

    // Assert
    assertThat(result, notNullValue());
  }

  @Test
  void getCompanySuppressRates_ShouldThrowCdhAdapterServiceException_WhenServerError() {
    // Arrange
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(CdhAdapterServiceException.class,
        () -> cdhAdapterClient.getCompanySuppressRates(COMPANY_ID));

    // Assert
    assertEquals(
        "Error fetching suppress rates from CDH for companyId=" + COMPANY_ID,
        thrownException.getDebugMessage()
    );
    verifyNoMoreInteractions(cdhAdapterWebClient);
  }

  @Test
  void searchCompanies_ShouldReturnCompanySearchResponseDto() {
    // Arrange
    var companySearchResponseDto = mock(CompanySearchResponseDto.class);
    var request = new CompanySearchCriteriaDto("BB_CCUI", "hotel-entity-service")
        .globalCompanyId(GLOBAL_COMPANY_ID);

    when(cdhAdapterProperties.getCompaniesSearchEndpoint()).thenReturn("/companies/search");
    when(cdhAdapterWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanySearchResponseDto.class)).thenReturn(Mono.just(companySearchResponseDto));

    // Act
    var result = cdhAdapterClient.searchCompanies(request);

    // Assert
    assertThat(result, notNullValue());
  }

  @Test
  void searchCompanies_ShouldThrowCdhAdapterServiceException_WhenServerError() {
    // Arrange
    var request = new CompanySearchCriteriaDto("BB_CCUI", "hotel-entity-service")
        .globalCompanyId(GLOBAL_COMPANY_ID);

    when(cdhAdapterProperties.getCompaniesSearchEndpoint()).thenReturn("/companies/search");
    when(cdhAdapterWebClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(CdhAdapterServiceException.class,
        () -> cdhAdapterClient.searchCompanies(request));

    // Assert
    assertEquals(
        "Error while retrieving companies details from CDH for companyId=" + GLOBAL_COMPANY_ID,
        thrownException.getDebugMessage()
    );
    verifyNoMoreInteractions(cdhAdapterWebClient);
  }
}

