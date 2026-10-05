package uk.co.whitbread.payapp.infrastructure.rest.client.cdh;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payapp.ErrorCode;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.exceptions.CDHException;
import uk.co.whitbread.payapp.infrastructure.rest.client.cdh.properties.CdhAdapterProperties;
import uk.co.whitbread.shared.cdh.model.GetEmployeeResponse;
import uk.co.whitbread.shared.cdh.model.GetEmployeesResponse;

@ExtendWith(MockitoExtension.class)
class CdhAdapterClientTest {

  @InjectMocks
  private CdhAdapterClient cdhAdapterClient;

  @Mock
  private WebClient cdhAdapterWebClient;

  @Mock
  private CdhAdapterProperties cdhAdapterProperties;

  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  private static final String ACCESS_BY = "InnBusiness";
  private static final String COMPANY_ID = "123456";
  private static final String EMPLOYEE_ID = "123456";
  private static final String EMAIL = "job@demo.ro";
  private static final String ERROR_MESSAGE =
      "Retrieved exception from CDH Adapter, response status = %s ";


  @BeforeEach
  void init() {
    cdhAdapterClient = new CdhAdapterClient(cdhAdapterProperties, cdhAdapterWebClient);
  }


  @Test
  void getEmployees__success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeesResponse.class)).thenReturn(
        createGetEmployeesResponse());

    // Act
    var getCompanyDetails = cdhAdapterClient.getEmployees(EMAIL, ACCESS_BY);

    // Assert
    assertNotNull(getCompanyDetails);
  }


  @Test
  void getEmployees__shouldFail() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(cdhAdapterWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeesResponse.class))
        .thenThrow(new CDHException(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION, ERROR_MESSAGE));

    // Act & Assert
    assertThrows(CDHException.class, () -> {
      cdhAdapterClient.getEmployees(EMAIL, ACCESS_BY);
    });
  }

  private Mono<GetEmployeesResponse> createGetEmployeesResponse() {
    return Mono.just(
        GetEmployeesResponse.builder()
            .results(List.of(
                GetEmployeeResponse.builder()
                    .bartEmployeeId(EMPLOYEE_ID)
                    .companyAccountId(COMPANY_ID)
                    .build()
            ))
            .build()
    );
  }
}
