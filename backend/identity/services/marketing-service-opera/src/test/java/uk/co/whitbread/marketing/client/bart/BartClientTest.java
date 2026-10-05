package uk.co.whitbread.marketing.client.bart;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.bart.security.LoginWebServiceMessageCallback;
import uk.co.whitbread.marketing.properties.BartProperties;
import uk.co.whitbread.marketing.utils.BartResponseValidator;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BartClientTest {

    public static final String MOCK_SERVICE_URL = "mockServiceUrl";

    @Mock
    private WebServiceTemplate mockWebServiceTemplate;

    @Mock
    private BartProperties mockBartProperties;

    @Mock
    private BartResponseValidator mockBartResponseValidator;

    @Mock
    private LoginWebServiceMessageCallback mockLoginWebServiceMessageCallback;

    @InjectMocks
    private BartClient bartClient;

    @BeforeEach
    void setUp() {
        when(mockBartProperties.getSubscriptionServiceUrl()).thenReturn(MOCK_SERVICE_URL);
    }

    @Test
    void subscription_ShouldMakeRequest() {
        //Given
        Subscribe subscriptionRequest = new Subscribe();

        SubscribeResponse mockSubscriptionResponse = Mockito.mock(SubscribeResponse.class);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(Subscribe.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockSubscriptionResponse);

        when(mockBartResponseValidator.validate(mockSubscriptionResponse)).thenReturn(Optional.empty());

        //When
        SubscribeResponse marketingSubscriptionResponse = bartClient.subscribeToNewsletters(subscriptionRequest);

        //Then
        assertThat(marketingSubscriptionResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionResponse, is(equalTo(mockSubscriptionResponse)));
    }

    @Test
    void subscription_shouldHandleSoapFaultClientException() {
        //Given
        Subscribe mockMarketingSubscriptionRequest = Mockito.mock(Subscribe.class);

        SoapFaultClientException mockSoapFaultException = Mockito.mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = Mockito.mock(SoapFault.class);
        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(Subscribe.class),
                any(WebServiceMessageCallback.class))).
                thenThrow(mockSoapFaultException);

        assertThrows(BartServiceException.class,
            () -> bartClient.subscribeToNewsletters(mockMarketingSubscriptionRequest));
    }

    @Test
    void subscription_shouldHandleErrorFromBart() {
        //Given
        Subscribe subscriptionRequest = new Subscribe();

        SubscribeResponse mockSubscriptionResponse = Mockito.mock(SubscribeResponse.class);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(Subscribe.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockSubscriptionResponse);


        when(mockBartResponseValidator.validate(mockSubscriptionResponse)).thenReturn(Optional.of("Error"));

        //When
        assertThrows(BartServiceException.class,
            () -> bartClient.subscribeToNewsletters(subscriptionRequest),
            "Error");
    }


    @Test
    void subscriptionInfo_ShouldMakeRequest() {
        //Given
        SubscriptionStatus subscriptionStatus = new SubscriptionStatus();

        SubscriptionStatusResponse mockSubscriptionStatusResponse = Mockito.mock(SubscriptionStatusResponse.class);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(SubscriptionStatus.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockSubscriptionStatusResponse);

        when(mockBartResponseValidator.validate(mockSubscriptionStatusResponse)).thenReturn(Optional.empty());

        //When
        SubscriptionStatusResponse marketingSubscriptionStatusResponse = bartClient.retrieveSubscriptionInfo(subscriptionStatus);

        //Then
        assertThat(marketingSubscriptionStatusResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionStatusResponse, is(equalTo(mockSubscriptionStatusResponse)));
    }

    @Test
    void subscriptionInfo_shouldHandleSoapFaultClientException() {
        //Given
        SoapFaultClientException mockSoapFaultException = Mockito.mock(SoapFaultClientException.class);
        SoapFault mockSoapFault = Mockito.mock(SoapFault.class);
        when(mockSoapFaultException.getSoapFault()).thenReturn(mockSoapFault);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(SubscriptionStatus.class),
                any(WebServiceMessageCallback.class))).
                thenThrow(mockSoapFaultException);
        var subscriptionStatus = new SubscriptionStatus();

        assertThrows(BartServiceException.class,
            () -> bartClient.retrieveSubscriptionInfo(subscriptionStatus));
    }

    @Test
    void subscriptionInfo_shouldHandleErrorFromBart() throws Exception {
        //Given
        SubscriptionStatusResponse mockSubscriptionStatusResponse = Mockito.mock(SubscriptionStatusResponse.class);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(SubscriptionStatus.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockSubscriptionStatusResponse);


        when(mockBartResponseValidator.validate(mockSubscriptionStatusResponse)).thenReturn(Optional.of("Error"));

        assertThrows(BartServiceException.class,
            () -> bartClient.retrieveSubscriptionInfo(new SubscriptionStatus()),
            "Error");
    }

    @Test
    void subscriptionStatus_ShouldMakeRequest() {
        //Given
        SubscriptionStatusResponse mockSubscriptionStatusResponse = Mockito.mock(SubscriptionStatusResponse.class);

        when(mockWebServiceTemplate.marshalSendAndReceive(eq(MOCK_SERVICE_URL),
                any(SubscriptionStatus.class),
                any(WebServiceMessageCallback.class))).thenReturn(mockSubscriptionStatusResponse);

        when(mockBartResponseValidator.validate(mockSubscriptionStatusResponse)).thenReturn(Optional.empty());

        //When
        SubscriptionStatusResponse marketingSubscriptionStatusResponse = bartClient.retrieveSubscriptionInfo(new SubscriptionStatus());

        //Then
        assertThat(marketingSubscriptionStatusResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionStatusResponse, is(equalTo(mockSubscriptionStatusResponse)));
    }
}
