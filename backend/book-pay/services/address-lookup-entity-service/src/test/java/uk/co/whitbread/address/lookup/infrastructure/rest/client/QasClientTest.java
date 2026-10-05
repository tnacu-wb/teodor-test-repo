package uk.co.whitbread.address.lookup.infrastructure.rest.client;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockGetAddressRequestWithMonikerId;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockGetAddressResponse;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockQASearchRequest;
import static uk.co.whitbread.address.lookup.utils.TestUtils.mockQASearchResponse;

import javax.xml.namespace.QName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.LoginWebServiceMessageCallback;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.QasClient;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions.InvalidMonikerIdException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.address.exceptions.QasServiceException;
import uk.co.whitbread.address.lookup.infrastructure.rest.client.config.QasProperties;
import uk.co.whitbread.qas.addresslookup.api.Address;
import uk.co.whitbread.qas.addresslookup.api.QAGetAddress;
import uk.co.whitbread.qas.addresslookup.api.QASearch;
import uk.co.whitbread.qas.addresslookup.api.QASearchResult;

@ExtendWith(MockitoExtension.class)
class QasClientTest {

  private static final String MOCK_ADDRESS_LOOKUP_URL = "addressLookupUrl";

  private QasClient qasClient;
  @Mock
  private QasProperties qasProperties;
  @Mock
  private WebServiceTemplate webServiceTemplate;
  @Mock
  private LoginWebServiceMessageCallback loginWebServiceMessageCallback;
  @Mock
  private SoapFaultClientException soapFaultClientException;
  @Mock
  private SoapFaultClientException soapFaultClientExceptionWithIncorrectMonikerid;

  @BeforeEach
  void setup() {
    when(qasProperties.getUrl()).thenReturn(MOCK_ADDRESS_LOOKUP_URL);

    qasClient = new QasClient(qasProperties, webServiceTemplate, loginWebServiceMessageCallback);
  }

  @Test
  void testSearch_returnsSearchResultWithMoniker() {
    QASearch request = mockQASearchRequest();

    final QASearchResult expectedResponse = mockQASearchResponse();

    Mockito.when(webServiceTemplate.marshalSendAndReceive(
            Mockito.eq(MOCK_ADDRESS_LOOKUP_URL),
            Mockito.eq(request),
            Mockito.any(LoginWebServiceMessageCallback.class)))
        .thenReturn(expectedResponse);

    QASearchResult response = qasClient.search(request);

    assertEquals(2, response.getQAPicklist().getPicklistEntry().size());
    assertNotNull(response.getQAPicklist().getPicklistEntry().get(0).getMoniker());
    assertNotNull(response.getQAPicklist().getPicklistEntry().get(1).getMoniker());
    assertEquals("moniker123", response.getQAPicklist().getPicklistEntry().get(0).getMoniker());
    assertEquals("moniker123" + 123,
        response.getQAPicklist().getPicklistEntry().get(1).getMoniker());
    assertEquals(expectedResponse, response);
  }

  @Test
  void testSearch_shouldHandleSoapFaultClientException() {
    when(soapFaultClientException.getMessage()).thenReturn("mockSoapFaultClientException");

    QASearch request = mockQASearchRequest();

    Mockito.when(webServiceTemplate.marshalSendAndReceive(
            Mockito.eq(MOCK_ADDRESS_LOOKUP_URL),
            Mockito.eq(request),
            Mockito.any(LoginWebServiceMessageCallback.class)))
        .thenThrow(soapFaultClientException);

    Exception exception = assertThrows(QasServiceException.class, () -> qasClient.search(request));
    String expectedMessage = "mockSoapFaultClientException";

    assertTrue(exception.getMessage().contains(expectedMessage));
  }

  @Test
  void testGetFormattedAddress_returnsFormattedAddress() {
    QAGetAddress request = mockGetAddressRequestWithMonikerId("moniker123");

    final Address expectedResponse = mockGetAddressResponse();

    Mockito.when(webServiceTemplate.marshalSendAndReceive(
            Mockito.eq(MOCK_ADDRESS_LOOKUP_URL),
            Mockito.eq(request),
            Mockito.any(LoginWebServiceMessageCallback.class)))
        .thenReturn(expectedResponse);

    Address response = qasClient.getFormattedAddress(request);

    assertEquals(8, response.getQAAddress().getAddressLine().size());
    assertEquals(expectedResponse, response);
  }

  @Test
  void testGetFormattedAddress_shouldHandleSoapFaultClientException() {
    when(soapFaultClientException.getMessage()).thenReturn("mockSoapFaultClientException");

    QAGetAddress request = mockGetAddressRequestWithMonikerId("moniker123");

    Mockito.when(webServiceTemplate.marshalSendAndReceive(
            Mockito.eq(MOCK_ADDRESS_LOOKUP_URL),
            Mockito.eq(request),
            Mockito.any(LoginWebServiceMessageCallback.class)))
        .thenThrow(soapFaultClientException);

    Exception exception = assertThrows(QasServiceException.class,
        () -> qasClient.getFormattedAddress(request));
    String expectedMessage = "mockSoapFaultClientException";

    assertTrue(exception.getMessage().contains(expectedMessage));
  }

  @Test
  void testGetFormattedAddress_shouldHandleSoapFaultClientException_forInvalidMonikerId() {
    when(soapFaultClientExceptionWithIncorrectMonikerid.getFaultCode()).thenReturn(
        new QName("InvalidMoniker"));

    QAGetAddress request = mockGetAddressRequestWithMonikerId("invalidMonikerId");

    Mockito.when(webServiceTemplate.marshalSendAndReceive(
            Mockito.eq(MOCK_ADDRESS_LOOKUP_URL),
            Mockito.eq(request),
            Mockito.any(LoginWebServiceMessageCallback.class)))
        .thenThrow(soapFaultClientExceptionWithIncorrectMonikerid);

    assertThrows(InvalidMonikerIdException.class,
        () -> qasClient.getFormattedAddress(request));
  }
}
