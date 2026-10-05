package uk.co.whitbread.account.infrastructure.rest.client.customers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.in.ContactDetail;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.infrastructure.rest.client.customers.mapper.CustomerRegistrationMapper;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.in.CustomerRegistrationRequestDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.model.out.CustomerRegistrationResponseDto;
import uk.co.whitbread.account.infrastructure.rest.client.customers.service.CustomerRegistrationClient;
import uk.co.whitbread.account.infrastructure.rest.controller.account.model.in.ContactDetailDto;

@ExtendWith(MockitoExtension.class)
class CustomerRegistrationPortImplTest {

  private static final String COUNTRY = "GB";
  private static final String LANGUAGE = "EN";
  public static final String CAPTCHA = "CAPTCHA";
  public static final String PASSWORD = "PASSWORD";
  public static final String BASKET_REFERENCE = "ASH-0ebfc2bf-8d0e-40d7-a420-6d0d43ee9e4c";
  public static final String SESSION_ID = "SESSOION";
  public static final String CUSTOMER_ID = "CUST";
  public static final boolean EXISTING_COMPANY = false;
  public static final boolean EXISTING_EMPLOYEE = false;
  public static final boolean SUCCESS = true;

  @Mock
  private ContactDetail contactDetail;
  @Mock
  private ContactDetailDto contactDetailDto;
  @Mock
  private CustomerRegistrationClient customerRegistrationClient;
  @Mock
  private CustomerRegistrationMapper custRegRespMapper;
  @InjectMocks
  private CustomerRegistrationPortImpl underTest;

  @Test
  void registerCustomer() {
    // Arrange
    var response = registrationResponse();
    lenient().when(customerRegistrationClient.registerCustomer(registrationRequestDto(), COUNTRY, LANGUAGE))
        .thenReturn(registrationResponseDto());
    given(custRegRespMapper.toModel(any())).willReturn(response);

    // Act
    var result = underTest.registerCustomer(registrationRequest(), COUNTRY, LANGUAGE);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getCustomerId(), is(CUSTOMER_ID));
    // as the BASKET_REFERENCE in PI follows a pattern (initital 3 `chars-` +
    // `uuid`)
    String uuidPattern = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
    String basketReferencePattern = "^ASH-" + uuidPattern + "$";

    assertThat(BASKET_REFERENCE, matchesPattern(basketReferencePattern));
    verify(custRegRespMapper).toModel(any());
  }

  private CustomerRegistrationResponseDto registrationResponseDto() {
    return new CustomerRegistrationResponseDto(SUCCESS, SESSION_ID, CUSTOMER_ID, EXISTING_COMPANY,
        EXISTING_EMPLOYEE);
  }

  private AccountRegistrationResponse registrationResponse() {
    return new AccountRegistrationResponse(SUCCESS, SESSION_ID, CUSTOMER_ID, EXISTING_COMPANY,
        EXISTING_EMPLOYEE);
  }

  private AccountRegistrationRequest registrationRequest() {
    return new AccountRegistrationRequest(CAPTCHA, PASSWORD, contactDetail, null, BASKET_REFERENCE);
  }

  private CustomerRegistrationRequestDto registrationRequestDto() {
    return new CustomerRegistrationRequestDto(CAPTCHA, PASSWORD, contactDetailDto, BASKET_REFERENCE);
  }
}
