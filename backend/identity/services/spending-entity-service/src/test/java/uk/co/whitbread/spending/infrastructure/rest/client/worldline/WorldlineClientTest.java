package uk.co.whitbread.spending.infrastructure.rest.client.worldline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.worldline.TrustedPartnerCredentials;
import uk.co.whitbread.spending.domain.model.in.worldline.WorldLineTcpHeaders;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentData;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.WorldlineClient;

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
  void testGetAc() {
    String serializedCredentials = "{\"Username\":\"user\",\"Password\":\"pass\"}";

    AccountInfoResponse expectedResponse = new AccountInfoResponse();
    when(client.getAccountInfo(requestHeaders.companyNumber(), serializedCredentials, requestHeaders.cultureCode(), requestHeaders.ipAddress(), requestHeaders.tetheredUserGuid())).thenReturn(expectedResponse);

    AccountInfoResponse actualResponse = client.getAccountInfo(requestHeaders);

    verify(client).getAccountInfo(requestHeaders.companyNumber(), serializedCredentials, requestHeaders.cultureCode(), requestHeaders.ipAddress(), requestHeaders.tetheredUserGuid());
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void shouldGetPaymentInfo() {
    // Given
    Map<String, String> headers = Map.of("Authorization", "Bearer token");
    Map<String, String> queryParams = Map.of("id", "123");

    var expectedResponse = new PaymentInfoResponse("200",
        List.of(new PaymentData()), "no errors");

    when(client.getPaymentInfo(headers, queryParams))
        .thenReturn(expectedResponse);

    // When
    var result = client.getPaymentInfo(headers, queryParams);

    // Then
    assertEquals("200", result.getResponseCode());
    assertEquals("no errors", result.getErrors());

    verify(client).getPaymentInfo(headers, queryParams);
  }
}
