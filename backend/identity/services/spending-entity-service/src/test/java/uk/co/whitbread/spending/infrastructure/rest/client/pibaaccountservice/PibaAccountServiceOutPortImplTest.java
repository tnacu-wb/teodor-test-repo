package uk.co.whitbread.spending.infrastructure.rest.client.pibaaccountservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.CustomerAccountsResponse;
import uk.co.whitbread.spending.domain.model.out.pibaaccountservice.TetheredUserDetailsResponse;

@ExtendWith(MockitoExtension.class)
class PibaAccountServiceOutPortImplTest {

  @Mock
  PibaAccountServiceClient pibaAccountServiceClient;

  @InjectMocks
  private PibaAccountServiceOutPortImpl pibaAccountServiceOutPort;

  @Test
  void getTetheredUserDetails_WhenInvoked_ThenParametersAreProperlyPassedToClient() {
    var infraResponse =  new TetheredUserDetailsResponse();
    when(pibaAccountServiceClient.getTetheredUserDetails("auth", "tg", "schema"))
          .thenReturn(infraResponse);

    var tetheredUserDetails = pibaAccountServiceOutPort.getTetheredUserDetails(
          "auth", "tg", "schema");

    assertEquals(infraResponse, tetheredUserDetails);
  }

  @Test
  void getAccounts_WhenInvoked_ThenParametersAreProperlyPassedToClient() {
    var infraResponse =  new CustomerAccountsResponse();
    when(pibaAccountServiceClient.getAccounts("auth"))
        .thenReturn(infraResponse);

    var tetheredUserDetails = pibaAccountServiceOutPort.getAccounts(
        "auth");

    assertEquals(infraResponse, tetheredUserDetails);
  }
}