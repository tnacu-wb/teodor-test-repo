package uk.co.whitbread.piba.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import uk.co.whitbread.piba.account.client.WorldlineClient;
import uk.co.whitbread.piba.account.converter.WorldlineAccountTransformer;
import uk.co.whitbread.piba.account.model.AccountInfo;
import uk.co.whitbread.piba.account.model.AccountInfoResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.TrustedPartnerCredentials;
import uk.co.whitbread.piba.account.model.WorldLineTcpHeaders;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.WorldlineRestProperties;
import uk.co.whitbread.piba.account.util.TestUtil;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGet;
import worldline.mst.bsm.api.b2b.pi.data.TetheredUserDetailsGetResponse;

@ExtendWith(MockitoExtension.class)
class WorldLineServiceTest {

  private final TestUtil testUtil = new TestUtil();
  @Mock
  private WebServiceTemplate mockWorldlineWebServiceTemplate;
  @Mock
  private WorldLineProperties mockWorldLineProperties;
  @Mock
  private WorldlineAccountTransformer mockWorldlineAccountTransformer;
  @Mock
  private WorldLineWebServiceMessageCallback mockWorldLineWebServiceMessageCallback;
  @Mock
  private TetheredUserDetailsGet mockTetheredUserDetailsGet1;
  @Mock
  private TetheredUserDetailsGetResponse mockRegisteredUserDetailsResponse1;
  @Mock
  private WorldLineProperties.Piba mockPibaProperties;
  @Mock
  private WorldLineProperties.Piba.Service mockPibaServiceProperties;
  @Mock
  private WorldLineAccountResponseValidator worldLineResponseValidator;
  @InjectMocks
  private WorldLineService worldLineService;
  @Mock
  private PropertiesLoader propertiesLoader;
  @Mock
  private WorldlineClient worldlineClient;
  @Mock
  private WorldlineUtils worldlineUtils;

  private static final String LOCATION_GB = "GB";
  private static final String COMPANY_NUMBER = "33";
  private static final String IP_ADDRESS = "00.000.00.00";
  private static final String TETHERED_USER_GUID = "1234-2145DD";

  @Test
  void getUserDetails() {
    when(mockWorldLineProperties.getPiba()).thenReturn(mockPibaProperties);
    when(mockPibaProperties.getService()).thenReturn(mockPibaServiceProperties);
    when(mockPibaServiceProperties.getUrl()).thenReturn("url");
    when(mockWorldlineAccountTransformer.toTetheredUserDetailsRequest(any(), any()))
        .thenReturn(mockTetheredUserDetailsGet1);
    TetheredUserDetailsResponse tetheredUserDetailsResponse = testUtil.createTetheredUserDetailsResponse();
    when(mockWorldlineWebServiceTemplate.marshalSendAndReceive(
        mockWorldLineProperties.getPiba().getService().getUrl(),
        mockWorldlineAccountTransformer.toTetheredUserDetailsRequest(any(), any()),
        mockWorldLineWebServiceMessageCallback
    )).thenReturn(mockRegisteredUserDetailsResponse1);
    tetheredUserDetailsResponse.getTetheredUserOverview().setUserRole(any());
    when(mockWorldlineAccountTransformer.toTetheredUserDetailsResponse(
        mockRegisteredUserDetailsResponse1))
        .thenReturn(tetheredUserDetailsResponse);
    doNothing().when(worldLineResponseValidator).validate(any(TetheredUserDetailsGetResponse.class));
    when(worldlineUtils.serializeObject(any())).thenReturn("mockedSerializedObject");

    var tetherUserGuid = worldLineService.getUserDetails("tetherUserGuid", Scheme.GB);
    assertNotNull(tetherUserGuid);
  }

  @Test
  void getAccountInfo_ShouldReturnOK() {
    // Arrange
    var properties = new WorldlineRestProperties.PropertiesByLocation();
    properties.setUsername("user");
    properties.setPassword("pass");
    properties.setUrl("url");
    properties.setCultureCode("en-GB");
    properties.setCompanyNumber(COMPANY_NUMBER);
    when(propertiesLoader.getPropertiesByLocation(LOCATION_GB)).thenReturn(properties);

    AccountInfoResponse expectedResponse = new AccountInfoResponse();
    expectedResponse.setData(new AccountInfo());
    when(worldlineClient.getAccountInfo(any(WorldLineTcpHeaders.class))).thenReturn(expectedResponse);

    // Act
    var actualResponse = worldLineService.getAccountInfo(Scheme.GB, IP_ADDRESS, TETHERED_USER_GUID);

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
  void getAccountInfo_ShouldReturnNull_WhenResponseIsNull() {
    // Arrange
    var properties = new WorldlineRestProperties.PropertiesByLocation();
    properties.setUsername("user");
    properties.setPassword("pass");
    properties.setCultureCode("en-GB");
    properties.setCompanyNumber(COMPANY_NUMBER);
    when(propertiesLoader.getPropertiesByLocation(LOCATION_GB)).thenReturn(properties);

    when(worldlineClient.getAccountInfo(any(WorldLineTcpHeaders.class))).thenReturn(null);

    // Act
    var actualResponse = worldLineService.getAccountInfo(Scheme.GB, IP_ADDRESS, TETHERED_USER_GUID);

    // Assert
    assertNull(actualResponse);
    verify(propertiesLoader).getPropertiesByLocation(LOCATION_GB);
    verify(worldlineClient).getAccountInfo(any(WorldLineTcpHeaders.class));
  }

  @Test
  void getAccountInfo_ShouldThrowException_WhenClientThrowsException() {
    // Arrange
    var properties = new WorldlineRestProperties.PropertiesByLocation();
    properties.setUsername("user");
    properties.setPassword("pass");
    properties.setCultureCode("en-GB");
    properties.setCompanyNumber(COMPANY_NUMBER);
    when(propertiesLoader.getPropertiesByLocation(LOCATION_GB)).thenReturn(properties);

    when(worldlineClient.getAccountInfo(any(WorldLineTcpHeaders.class)))
        .thenThrow(new RuntimeException("Test Client error"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class, () ->
        worldLineService.getAccountInfo(Scheme.GB, IP_ADDRESS, TETHERED_USER_GUID)
    );
    assertEquals("Test Client error", exception.getMessage());
    verify(propertiesLoader).getPropertiesByLocation(LOCATION_GB);
    verify(worldlineClient).getAccountInfo(any(WorldLineTcpHeaders.class));
  }

}