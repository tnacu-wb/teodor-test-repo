package uk.co.whitbread.infrastructure.rest.client.companyentity;

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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.booking.infrastructure.rest.client.companyentity.generated.models.CompanyResponseDto;
import uk.co.whitbread.infrastructure.rest.client.CompanyEntityServiceClient;
import uk.co.whitbread.infrastructure.rest.client.companyentity.exceptions.CompanyEntityServiceException;
import uk.co.whitbread.infrastructure.rest.client.companyentity.service.properties.CompanyEntityServiceProperties;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class CompanyEntityServiceClientTest {

  @Mock
  private CompanyEntityServiceProperties companyEntityServiceProperties;
  @Mock
  private WebClient companyEntityServiceWebClient;
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
  private CompanyEntityServiceClient companyEntityServiceClient;

  private static final String COMPANY_ID = "COMPANY_123";

  @Test
  void getCompanyById_shouldReturnCompanyResponseDto() {
    // Arrange
    var companyResponseDto = mock(CompanyResponseDto.class);
    when(companyEntityServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyResponseDto.class)).thenReturn(Mono.just(companyResponseDto));

    // Act
    var result = companyEntityServiceClient.getCompanyById(COMPANY_ID);

    // Assert
    assertThat(result, notNullValue());
  }

  @Test
  void getCompanyById_shouldThrowCompanyEntityServiceException_whenServerError() {
    // Arrange
    when(companyEntityServiceWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(CompanyEntityServiceException.class,
        () -> companyEntityServiceClient.getCompanyById(COMPANY_ID));

    // Assert
    assertEquals(
        "Error fetching company details from Company Entity Service for id=" + COMPANY_ID,
        thrownException.getDebugMessage()
    );
    verifyNoMoreInteractions(companyEntityServiceWebClient);
  }
}
