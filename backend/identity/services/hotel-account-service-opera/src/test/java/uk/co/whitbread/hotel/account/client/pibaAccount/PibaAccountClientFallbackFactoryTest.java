package uk.co.whitbread.hotel.account.client.pibaAccount;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.hotel.account.exceptions.PibaAccountServiceException;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

class PibaAccountClientFallbackFactoryTest {

  @Mock
  private Throwable throwable;
  @Mock
  private TokenService tokenService;

  private PibaAccountClientFallbackFactory fallbackFactory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fallbackFactory = new PibaAccountClientFallbackFactory(tokenService);
  }

  @Test
  void testCreate() {
    doReturn(
        CdhEmployeeDetails.builder().companyAccountId("compAccId").employeeAccountId("empAccId").build())
        .when(tokenService).retrieveCdhEmployeeDetailsAndVerifyToken(anyString());
    var fallbackClient = fallbackFactory.create(throwable);
    assertThrows(PibaAccountServiceException.class,
        () -> fallbackClient.getPibaAccounts("JWT", true));
  }
}
