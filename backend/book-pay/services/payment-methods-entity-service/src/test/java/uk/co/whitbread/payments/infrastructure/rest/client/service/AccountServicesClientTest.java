package uk.co.whitbread.payments.infrastructure.rest.client.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.domain.exception.AccountsServiceException;
import uk.co.whitbread.payments.domain.exception.PaymentMethodsException;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.model.out.*;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelCardClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.CompanyServiceClient;
import uk.co.whitbread.payments.infrastructure.rest.client.customers.service.HotelAccountClient;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class AccountServicesClientTest {

  private static final String CUSTOMER_ID = "111";
  private static final String CHANNEL = "BB";
  private static final String TOKEN = "token";
  private static final String COMPANY_ID = "86";
  private static final String SESSION_ID = "session id";
  private static final String EMPLOYEE_ID = "222";

  @InjectMocks
  private HotelCardClient hotelCardClient;
  @InjectMocks
  private HotelAccountClient hotelAccountClient;
  @InjectMocks
  private CompanyServiceClient companyServiceClient;
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void findCustomerAccount_success() {
    //Arrange

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CustomerAccountDto.class)).thenReturn(mockCustomerAccount());

    //Act
    var customerResponse = this.hotelAccountClient.findCustomerAccount(CUSTOMER_ID, false, CHANNEL,
        TOKEN);

    //Assert
    assertThat(customerResponse, notNullValue());
  }

  @Test
  void findCustomerAccount_4xx_exception() {
    String errorMessage = "An error was returned by Hotel Account Service";
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(AccountsServiceException.class,
        () -> hotelAccountClient.findCustomerAccount(CUSTOMER_ID, true, CHANNEL, TOKEN));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  @Test
  void findCustomerAccount_5xx_exception() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(PaymentMethodsException.class,
        () -> hotelAccountClient.findCustomerAccount(CUSTOMER_ID, true, CHANNEL, TOKEN));

    //Assert
    String expectedMessage = "An error was returned by Hotel Account Service";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void findCentralStoredCard_success() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(new ParameterizedTypeReference<List<PaymentCardDto>>() {
    })).thenReturn(mockPaymentCards());

    //Act
    var centralStoredCards = this.hotelCardClient.findCentralStoredCard(COMPANY_ID, CHANNEL,
        SESSION_ID, TOKEN);

    //Assert
    assertThat(centralStoredCards, notNullValue());
  }

  @Test
  void findCentralStoredCard_4xx_exception() {
    String errorMessage = "Improper call of Accounts Service";
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(AccountsServiceException.class,
        () -> hotelCardClient.findCentralStoredCard(COMPANY_ID, CHANNEL, SESSION_ID, TOKEN));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  @Test
  void findCentralStoredCard_5xx_exception() {
    String errorMessage = "An error was returned by Hotel Card Service";
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.accept(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(PaymentMethodsException.class,
        () -> hotelCardClient.findCentralStoredCard(COMPANY_ID, CHANNEL, SESSION_ID, TOKEN));

    //Assert

    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(errorMessage));
  }

  @Test
  void findCompany_success() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CompanyDetailsResponseDto.class)).thenReturn(
        mockCompanyDetailsResponse());

    //Act
    var customerResponse = this.companyServiceClient.findCompany(COMPANY_ID, SESSION_ID, EMPLOYEE_ID,
        TOKEN);

    //Assert
    assertThat(customerResponse, notNullValue());
  }

  @Test
  void findCompany_4xx_exception() {
    String expectedMessage = "An error was returned by Company Microservice";
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(AccountsServiceException.class,
        () -> companyServiceClient.findCompany(COMPANY_ID, SESSION_ID, EMPLOYEE_ID, TOKEN));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void findCompany_5xx_exception() {
    //Arrange
    String expectedMessage = "An error was returned by Company Microservice";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.header(any(), any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(PaymentMethodsException.class,
        () -> companyServiceClient.findCompany(COMPANY_ID, SESSION_ID, EMPLOYEE_ID, TOKEN));

    //Assert
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  private Mono<List<PaymentCardDto>> mockPaymentCards() {
    PaymentCardDto paymentCardDto = new PaymentCardDto();
    paymentCardDto.setBillingAddress(new BillingAddressDto());
    paymentCardDto.setCardHolderName("Saved card 1");
    paymentCardDto.setCardId("1L");
    paymentCardDto.setCardNumber("1");
    paymentCardDto.setCardToken("1111222244445555");
    paymentCardDto.setIssueNumber("1");
    paymentCardDto.setCardType("BUSINESS_CENTRALLY_STORED_CARD");
    paymentCardDto.setCnpBusinessAccountPassword("password");
    paymentCardDto.setCnpBusinessAccountUsername("userName");
    paymentCardDto.setCnpRequired(false);
    paymentCardDto.setExpiryDate("05/26");
    paymentCardDto.setStartDate("05/22");

    return Mono.just(List.of(paymentCardDto));
  }

  private Mono<CustomerAccountDto> mockCustomerAccount() {
    CustomerAccountDto customerAccountDto = new CustomerAccountDto();
    customerAccountDto.setAdditionalGuests(List.of());
    customerAccountDto.setBookingPreference(new BookingPreferenceDto());
    customerAccountDto.setBusiness(new BusinessDto());
    customerAccountDto.setContactDetail(new ContactDetailDto());
    customerAccountDto.setPaymentPreference(new PaymentPreferenceDto());
    customerAccountDto.setBusinessUse(true);
    customerAccountDto.setCompanyId("11L");
    customerAccountDto.setCompanyName("companyName");
    customerAccountDto.setGuestHistoryNumber("1");
    customerAccountDto.setSessionId("sessionId");

    return Mono.just(customerAccountDto);
  }

  private Mono<CompanyDetailsResponseDto> mockCompanyDetailsResponse() {
    CompanyDetailsResponseDto companyDetailsResponseDto = new CompanyDetailsResponseDto();
    companyDetailsResponseDto.setRequestedCompany(new CompanyDto());
    companyDetailsResponseDto.setAllowCentralCreditCard(true);
    companyDetailsResponseDto.setMarketingAllowed(true);
    companyDetailsResponseDto.setCompanyLockedForEditing(true);
    companyDetailsResponseDto.setSuccess(true);

    return Mono.just(companyDetailsResponseDto);
  }
}
