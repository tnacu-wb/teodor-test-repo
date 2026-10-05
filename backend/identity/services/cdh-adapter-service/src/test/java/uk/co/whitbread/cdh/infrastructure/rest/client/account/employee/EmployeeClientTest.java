package uk.co.whitbread.cdh.infrastructure.rest.client.account.employee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.PrematureCloseException;
import uk.co.whitbread.cdh.domain.model.account.in.EmployeeSearchCriteria;
import uk.co.whitbread.cdh.domain.model.account.in.GetEmployeeRequest;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeeResponse;
import uk.co.whitbread.cdh.domain.model.account.out.employee.GetEmployeesResponse;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.CompanyEmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeRequestDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.account.employee.model.EmployeeSearchCriteriaDto;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.CdhApiProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.config.WebClientProperties;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.CDHException;
import uk.co.whitbread.cdh.infrastructure.rest.client.cdh.exception.ErrorCode;
import uk.co.whitbread.cdh.infrastructure.rest.client.oauth.OAuthProvider;
import uk.co.whitbread.cdh.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class EmployeeClientTest {

  private static final String EMAIL = "john.doe@wb.com";

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private CustomTestResponseSpec customResponseSpec;

  @Mock
  private OAuthProvider oAuthProvider;

  @Mock
  CdhApiProperties cdhApiProperties;

  @Mock
  WebClientProperties webClientProperties;

  @InjectMocks
  private EmployeeClient employeeClient;

  @Test
  void getEmployee_success() {
    GetEmployeeRequest getEmployeeRequest = new GetEmployeeRequest();
    String email = "john.doe@wb.com";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeeResponse.class)).thenReturn(
        Mono.just(GetEmployeeResponse.builder().emailAddress(email).build()));

    GetEmployeeResponse employee = employeeClient.getEmployee(getEmployeeRequest);

    assertNotNull(employee);
    assertThat(employee.getEmailAddress(), is(email));
  }

  @Test
  void getEmployee_error() {
    GetEmployeeRequest getEmployeeRequest = new GetEmployeeRequest();
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
        () -> employeeClient.getEmployee(getEmployeeRequest));

    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getEmployeeV2_success() {
    var employeeRequestDto = EmployeeRequestDto.builder()
        .companyAccountId("COMP123").employeeAccountId("EMP456")
        .accessContext("PI").accessedBy(EMAIL).build();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeeResponse.class)).thenReturn(
        Mono.just(GetEmployeeResponse.builder().emailAddress(EMAIL).build()));

    GetEmployeeResponse response = employeeClient.getEmployeeV2(employeeRequestDto);

    assertNotNull(response);
    assertThat(response.getEmailAddress(), is(EMAIL));
  }

  @Test
  void getEmployeeV2_error() {
    var employeeRequestDto = EmployeeRequestDto.builder()
        .companyAccountId("COMP123").employeeAccountId("EMP456")
        .accessContext("PI").accessedBy(EMAIL).build();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> employeeClient.getEmployeeV2(employeeRequestDto));

    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getEmployees_success() {
    var  employeeSearchCriteria = EmployeeSearchCriteria.builder()
        .emailAddress(EMAIL)
        .bartEmployeeId("12345")
        .bartGuestHistoryNumber("67890")
        .globalCompanyId("globalCompanyId")
        .activationKey("activationKey")
        .pageToken("pageToken")
        .pageSize("10")
        .build();

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeesResponse.class)).thenReturn(
        Mono.just(createGetEmployeesResponse()));

    GetEmployeesResponse response = employeeClient.getEmployees(employeeSearchCriteria);

    assertNotNull(response);
    assertThat(response.getResults().get(0).getEmailAddress() , is(EMAIL));
  }


  @Test
  void getEmployees_error() {
    var  employeeSearchCriteria = new EmployeeSearchCriteria();
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
        () -> employeeClient.getEmployees(employeeSearchCriteria));

    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }
  
  @Test
  void getCompanyEmployees_success() {
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(GetEmployeesResponse.class)).thenReturn(
        Flux.just(createGetEmployeesResponse()));
    
    GetEmployeesResponse response = employeeClient.getCompanyEmployees("testCompanyAccountId",
        EMAIL, 200, "testToken", "PI", false);
    
    assertNotNull(response);
  }

  @Test
  void getCompanyEmployees_successAfterPrematureCloseRetry() {
    WebClientRequestException prematureCloseException = prematureCloseException();
    AtomicInteger attempts = new AtomicInteger();
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(GetEmployeesResponse.class)).thenReturn(Flux.defer(() -> {
      if (attempts.getAndIncrement() == 0) {
        return Flux.error(prematureCloseException);
      }
      return Flux.just(createGetEmployeesResponse());
    }));

    GetEmployeesResponse response = employeeClient.getCompanyEmployees("testCompanyAccountId",
        EMAIL, 200, "testToken", "PI", false);

    assertNotNull(response);
    assertEquals(2, attempts.get());
  }
  
  @Test
  void getCompanyEmployees_error() {
    lenient().when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
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
    
    var exception = assertThrows(CDHException.class, () -> employeeClient.getCompanyEmployees(
        "testCompanyAccountId", EMAIL, 200, "testToken", "PI",
        false));
    
    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }
  @Test
  void getCompanyEmployees_errorNotFound() {
    lenient().when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();
    
    var exception = assertThrows(CDHException.class, () -> employeeClient.getCompanyEmployees(
        "testCompanyAccountId", EMAIL, 200, "testToken", "PI",
        false));
    
    assertEquals(ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getCompanyEmployees_errorPrematureClose() {
    WebClientRequestException prematureCloseException = prematureCloseException();
    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestHeadersSpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(
        requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessContext"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(eq("AccessedBy"), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(GetEmployeesResponse.class)).thenReturn(Flux.error(prematureCloseException));

    var exception = assertThrows(CDHException.class, () -> employeeClient.getCompanyEmployees(
        "testCompanyAccountId", EMAIL, 200, "testToken", "PI", false));

    assertEquals(ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getEmployeesV2_success() {
    var employeeSearchCriteriaDto = EmployeeSearchCriteriaDto.builder().build();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(EmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeesResponse.class)).thenReturn(
        Mono.just(createGetEmployeesResponse()));

    GetEmployeesResponse response = employeeClient.getEmployeesV2(employeeSearchCriteriaDto, "abc@test.com", "PI");

    assertNotNull(response);
    assertThat(response.getResults().get(0).getEmailAddress(), is(EMAIL));
  }

  @Test
  void getEmployeesV2_error() {
    var employeeSearchCriteriaDto = EmployeeSearchCriteriaDto.builder().build();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(EmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> employeeClient.getEmployeesV2(employeeSearchCriteriaDto, "abc@test.com", "PI"));

    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getEmployeesV2_notFound_returnsNull() {
    var employeeSearchCriteriaDto = EmployeeSearchCriteriaDto.builder().build();

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(EmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(GetEmployeesResponse.class)).thenReturn(Mono.empty());

    GetEmployeesResponse response = employeeClient.getEmployeesV2(employeeSearchCriteriaDto, "abc@test.com", "PI");

    assertNull(response);
  }

  @Test
  void getCompanyEmployeesV2_success() {
    var companyEmployeeDto = CompanyEmployeeSearchCriteriaDto.builder()
        .pageSize("200").pageToken("testToken").awaitingApproval("false").build();

    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(CompanyEmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(GetEmployeesResponse.class)).thenReturn(
        Flux.just(createGetEmployeesResponse()));

    GetEmployeesResponse response = employeeClient.getCompanyEmployeesV2(
        companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI");

    assertNotNull(response);
    assertThat(response.getResults().get(0).getEmailAddress(), is(EMAIL));
  }

  @Test
  void getCompanyEmployeesV2_error() {
    var companyEmployeeDto = CompanyEmployeeSearchCriteriaDto.builder().build();

    lenient().when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(CompanyEmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> employeeClient.getCompanyEmployeesV2(companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI"));

    assertEquals(ErrorCode.CDH_GET_EMPLOYEE_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getCompanyEmployeesV2_notFound() {
    var companyEmployeeDto = CompanyEmployeeSearchCriteriaDto.builder().build();

    lenient().when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(CompanyEmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    var exception = assertThrows(CDHException.class,
        () -> employeeClient.getCompanyEmployeesV2(companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI"));

    assertEquals(ErrorCode.CDH_GET_COMPANY_EMPLOYEES_EXCEPTION.getCode(), exception.getErrorCode());
  }

  @Test
  void getCompanyEmployeesV2_prematureCloseRetry_success() {
    var companyEmployeeDto = CompanyEmployeeSearchCriteriaDto.builder().build();
    WebClientRequestException prematureCloseException = prematureCloseException();
    AtomicInteger attempts = new AtomicInteger();

    when(webClientProperties.getBlockingTimeout()).thenReturn(120L);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(oAuthProvider.getBearerToken()).thenReturn("test");
    when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessContext"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.header(eq("AccessedBy"), any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), eq(CompanyEmployeeSearchCriteriaDto.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(GetEmployeesResponse.class)).thenReturn(Flux.defer(() -> {
      if (attempts.getAndIncrement() == 0) {
        return Flux.error(prematureCloseException);
      }
      return Flux.just(createGetEmployeesResponse());
    }));

    GetEmployeesResponse response = employeeClient.getCompanyEmployeesV2(
        companyEmployeeDto, "testCompanyAccountId", EMAIL, "PI");

    assertNotNull(response);
    assertEquals(2, attempts.get());
  }

  private GetEmployeesResponse createGetEmployeesResponse() {
    return GetEmployeesResponse.builder()
        .results(List.of(GetEmployeeResponse.builder()
                .emailAddress(EMAIL)
            .build()))
        .build();
  }

  private WebClientRequestException prematureCloseException() {
    return new WebClientRequestException(newPrematureCloseException(), HttpMethod.GET,
        URI.create("https://dummy-env.com/test"), HttpHeaders.EMPTY);
  }

  private PrematureCloseException newPrematureCloseException() {
    try {
      Constructor<PrematureCloseException> constructor =
          PrematureCloseException.class.getDeclaredConstructor(String.class);
      constructor.setAccessible(true);
      return constructor.newInstance("Connection prematurely closed DURING response");
    } catch (ReflectiveOperationException exception) {
      throw new IllegalStateException("Unable to create PrematureCloseException for test", exception);
    }
  }
}
