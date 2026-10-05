package uk.co.whitbread.piba.account.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.TrustedPartnerCredentials;
import uk.co.whitbread.piba.account.model.WorldLineTcpHeaders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorldlineClientTest {

  @Mock
  private WorldlineClient client;

  private WorldLineTcpHeaders requestHeaders;

  @BeforeEach
  void setUp() {
    client = mock(WorldlineClient.class, CALLS_REAL_METHODS);
    var credentials = TrustedPartnerCredentials.builder()
        .username("user")
        .password("pass").build();
    requestHeaders = new WorldLineTcpHeaders("companyNumber", credentials, "cultureCode", "ipAddress", "guid");
  }

  @Test
  void testGetAccountInfo() {
    String serializedCredentials = "{\"Username\":\"user\",\"Password\":\"pass\"}";

    AccountInfoResponse expectedResponse = new AccountInfoResponse();
    when(client.getAccountInfo(requestHeaders.companyNumber(), serializedCredentials, requestHeaders.cultureCode(), requestHeaders.ipAddress(), requestHeaders.tetheredUserGuid())).thenReturn(expectedResponse);

    AccountInfoResponse actualResponse = client.getAccountInfo(requestHeaders);

    verify(client).getAccountInfo(requestHeaders.companyNumber(), serializedCredentials, requestHeaders.cultureCode(), requestHeaders.ipAddress(), requestHeaders.tetheredUserGuid());
    assertEquals(expectedResponse, actualResponse);
  }
}
