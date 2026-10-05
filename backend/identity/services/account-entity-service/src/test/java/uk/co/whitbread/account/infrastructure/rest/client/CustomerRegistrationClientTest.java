package uk.co.whitbread.account.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.in.CustomerRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.out.CustomerRegistrationResponseDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.CustomerRegistrationClient;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.properties.CustomerRegistrationClientProperties;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactDetailDto;

@ExtendWith(MockitoExtension.class)
public class CustomerRegistrationClientTest {

  private static final String COUNTRY = "GB";
  private static final String LANGUAGE = "EN";
  public static final String CAPTCHA = "CAPTCHA";
  public static final String PASSWORD = "PASSWORD";
  public static final String BASKET_REFERENCE = "ASH-0ebfc2bf-8d0e-40d7-a420-6d0d43ee9e4c";
  private final boolean SUCCESS_RESPONSE = true;
  private final boolean FAILED_RESPONSE = false;
  private final String SESSION_ID = "SESSION_ID";
  private final String CUSTOMER_ID = "CUSTOMER_ID";
  private final boolean IS_NOT_EXISTING_EMPLOYEE = false;
  private final boolean IS_EXISTING_EMPLOYEE = true;
  private final boolean IS_NOT_EXISTING_COMPANY = false;

  @Mock
  private WebClient webClient;
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
  private CustomerRegistrationClientProperties registrationServiceClientProperties;

  @InjectMocks
  private CustomerRegistrationClient customerRegistrationClient;

  @Test
  void registerCustomer_success() {
    // Arrange

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        registrationServiceClientProperties.getRegistrationEndpoint())).thenReturn(
            requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("country", COUNTRY)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("language", LANGUAGE)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CustomerRegistrationResponseDto.class)).thenReturn(
        mockCustomerRegistrationSuccessResponse());

    // Act
    var customerResponse = this.customerRegistrationClient.registerCustomer(
        validRegistrationRequest(), COUNTRY, LANGUAGE);

    // Assert
    assertThat(customerResponse, notNullValue());
    // as the BASKET_REFERENCE in PI follows a pattern (initital 3 `chars-` +
    // `uuid`)
    String uuidPattern = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
    String basketReferencePattern = "^ASH-" + uuidPattern + "$";

    assertThat(BASKET_REFERENCE, matchesPattern(basketReferencePattern));
  }

  @Test
  void registerCustomer_failure() {
    // Arrange

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(
        registrationServiceClientProperties.getRegistrationEndpoint())).thenReturn(
            requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("country", COUNTRY)).thenReturn(requestBodySpec);
    when(requestBodySpec.header("language", LANGUAGE)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CustomerRegistrationResponseDto.class)).thenReturn(
        mockCustomerRegistrationFailureResponse());

    // Act
    var customerResponse = this.customerRegistrationClient.registerCustomer(
        validRegistrationRequest(), COUNTRY, LANGUAGE);

    // Assert
    assertThat(customerResponse, notNullValue());
  }

  private CustomerRegistrationRequestDto validRegistrationRequest() {
    return new CustomerRegistrationRequestDto(CAPTCHA, PASSWORD, new ContactDetailDto(), BASKET_REFERENCE);
  }

  private Mono<CustomerRegistrationResponseDto> mockCustomerRegistrationSuccessResponse() {
    CustomerRegistrationResponseDto response = new CustomerRegistrationResponseDto(SUCCESS_RESPONSE, SESSION_ID,
        CUSTOMER_ID,
        IS_NOT_EXISTING_COMPANY, IS_NOT_EXISTING_EMPLOYEE);
    return Mono.just(response);
  }

  private Mono<CustomerRegistrationResponseDto> mockCustomerRegistrationFailureResponse() {
    CustomerRegistrationResponseDto response = new CustomerRegistrationResponseDto(FAILED_RESPONSE, SESSION_ID,
        CUSTOMER_ID,
        IS_NOT_EXISTING_COMPANY, IS_NOT_EXISTING_EMPLOYEE);
    return Mono.just(response);
  }
}
