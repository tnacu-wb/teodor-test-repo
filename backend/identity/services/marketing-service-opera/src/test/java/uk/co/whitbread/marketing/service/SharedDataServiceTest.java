package uk.co.whitbread.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.bart.unified.api.SharedDataRequest;
import uk.co.whitbread.marketing.properties.BartProperties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.marketing.service.MarketingNewsletterServiceTest.MOCK_SERVICE_URL;

@ExtendWith(MockitoExtension.class)
class SharedDataServiceTest {


    @InjectMocks
    private SharedDataService sharedDataService;

    @Mock
    private WebServiceTemplate webServiceTemplate;

    @Mock
    private BartProperties bartProperties;

    @Mock
    private LoginWebServiceMessageCallback loginServiceCallback;

    @BeforeEach
    void setUp() throws Exception {
        when(bartProperties.getSharedDataServiceUrl()).thenReturn(MOCK_SERVICE_URL);
    }

    @Test
    void regions_shouldHandleSoapFaultClientException() {
        SoapFaultClientException mockSoapFaultException = Mockito.mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = Mockito.mock(SoapFault.class);
        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);
        when(webServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(SharedDataRequest.class),
                eq(loginServiceCallback))).thenThrow(mockSoapFaultException);

        assertThrows(BartServiceException.class,
            () -> sharedDataService.getRegions());
    }


}
