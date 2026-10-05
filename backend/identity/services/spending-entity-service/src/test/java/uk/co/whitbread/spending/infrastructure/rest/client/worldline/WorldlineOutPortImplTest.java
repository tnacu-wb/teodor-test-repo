package uk.co.whitbread.spending.infrastructure.rest.client.worldline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.in.worldline.WorldLineTcpHeaders;
import uk.co.whitbread.spending.domain.model.in.worldline.TrustedPartnerCredentials;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentData;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.infrastructure.config.WorldlineProperties;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.PropertiesLoader;
import uk.co.whitbread.spending.infrastructure.rest.client.worldline.service.WorldlineClient;

@ExtendWith(MockitoExtension.class)
class WorldlineOutPortImplTest {

  @Mock
  private WorldlineClient worldlineClient;

  @Mock
  private PropertiesLoader propertiesLoader;

  @InjectMocks
  private WorldlineOutPortImpl worldlineOutPort;

  private static final String LOCATION_GB = "gb";
  private static final String COMPANY_NUMBER = "33";
  private static final String IP_ADDRESS = "00.000.00.00";
  private static final String TETHERED_USER_GUID = "1234-2145DD";

  @Test
  void getAccountInfo_ShouldReturnOK() {
    // Arrange
    var properties = new WorldlineProperties.PropertiesByLocation();
    properties.setUsername("user");
    properties.setPassword("pass");
    properties.setUrl("url");
    properties.setCultureCode("en-GB");
    properties.setCompanyNumber(COMPANY_NUMBER);
    when(propertiesLoader.getPropertiesByLocation(LOCATION_GB)).thenReturn(properties);

    AccountInfoResponse expectedResponse = new AccountInfoResponse();
    when(worldlineClient.getAccountInfo(any(WorldLineTcpHeaders.class))).thenReturn(expectedResponse);

    // Act
    var actualResponse = worldlineOutPort.getAccountInfo(LOCATION_GB, IP_ADDRESS, TETHERED_USER_GUID);

    // Assert
    verify(propertiesLoader).getPropertiesByLocation(LOCATION_GB);
    verify(worldlineClient).getAccountInfo(
        new WorldLineTcpHeaders(
              COMPANY_NUMBER,
            TrustedPartnerCredentials.builder()
                .username(properties.getUsername())
                .password(properties.getPassword()).build(),
            properties.getCultureCode(),
            IP_ADDRESS,
            TETHERED_USER_GUID));
    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getPaymentInfo_ShouldReturnOK() {
    // Arrange
    var properties = new WorldlineProperties.PropertiesByLocation();
    properties.setUsername("user");
    properties.setPassword("pass");
    properties.setUrl("url");
    properties.setCultureCode("en-GB");
    properties.setCompanyNumber(COMPANY_NUMBER);
    when(propertiesLoader.getPropertiesByLocation(LOCATION_GB)).thenReturn(properties);
    var response = new PaymentInfoResponse("200", List.of(new PaymentData()), "no errors");
    var request = new PaymentInfoModel("123", 1, 1, true, "token", "1.1.1.1");
    when(worldlineClient.getPaymentInfo(any(),any())).thenReturn(response);
    // Act
    var actualResponse = worldlineOutPort.getPaymentInfo(LOCATION_GB,TETHERED_USER_GUID,request);

    // Assert
    verify(propertiesLoader).getPropertiesByLocation(LOCATION_GB);
    assertNotNull(actualResponse);
    assertEquals("200",actualResponse.getResponseCode());
    assertNotNull(actualResponse.getData());
    assertEquals("no errors",actualResponse.getErrors());
  }
}