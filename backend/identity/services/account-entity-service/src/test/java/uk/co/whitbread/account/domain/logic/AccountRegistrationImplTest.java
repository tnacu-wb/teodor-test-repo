package uk.co.whitbread.account.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.lenient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.co.whitbread.account.domain.model.in.AccountRegistrationRequest;
import uk.co.whitbread.account.domain.model.in.ContactDetail;
import uk.co.whitbread.account.domain.model.out.AccountRegistrationResponse;
import uk.co.whitbread.account.domain.ports.secondary.CustomerRegistrationPort;

@ExtendWith(MockitoExtension.class)
public class AccountRegistrationImplTest {
  private static final String COUNTRY = "GB";
  private static final String LANGUAGE = "EN";
  public static final String CAPTCHA = "CAPTCHA";
  public static final String PASSWORD = "PASSWORD";
  public static final String BASKET_REFERENCE = "ASH-0ebfc2bf-8d0e-40d7-a420-6d0d43ee9e4c";
  public static final boolean SUCCESS = true;
  public static final String SESSION_ID = "SESSOION";
  public static final String CUSTOMER_ID = "CUST";
  public static final boolean EXISTING_COMPANY = false;
  public static final boolean EXISTING_EMPLOYEE = false;
  @Mock
  private ContactDetail contactDetails;
  @Mock
  private CustomerRegistrationPort customerRegistrationPort;
  @InjectMocks
  private AccountRegistrationPortImpl underTest;

  @Test
  void registerAccount() {
    // Arrange
    lenient().when(customerRegistrationPort.registerCustomer(registrationRequest(), COUNTRY, LANGUAGE))
        .thenReturn(registrationResponse());

    // Act
    var result = underTest.registerAccount(registrationRequest(), COUNTRY, LANGUAGE);

    // Assert
    assertThat(result, notNullValue());
    assertThat(result.getCustomerId(), is(CUSTOMER_ID));

    // as the BASKET_REFERENCE in PI follows a pattern (initital 3 `chars-` +
    // `uuid`)
    String uuidPattern = "[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}";
    String basketReferencePattern = "^ASH-" + uuidPattern + "$";

    assertThat(BASKET_REFERENCE, matchesPattern(basketReferencePattern));
  }

  private AccountRegistrationRequest registrationRequest() {
    return new AccountRegistrationRequest(CAPTCHA, PASSWORD, contactDetails, null,
        BASKET_REFERENCE);
  }

  private AccountRegistrationResponse registrationResponse() {
    return new AccountRegistrationResponse(SUCCESS, SESSION_ID, CUSTOMER_ID, EXISTING_COMPANY, EXISTING_EMPLOYEE);
  }
}
