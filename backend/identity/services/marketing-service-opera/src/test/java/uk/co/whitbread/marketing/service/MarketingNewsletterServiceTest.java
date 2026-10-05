package uk.co.whitbread.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.bart.marketing.api.Subscribe;
import uk.co.whitbread.bart.marketing.api.SubscribeResponse;
import uk.co.whitbread.bart.marketing.api.SubscriptionElement;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatus;
import uk.co.whitbread.bart.marketing.api.SubscriptionStatusResponse;
import uk.co.whitbread.marketing.client.bart.BartClient;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.properties.BartProperties;
import uk.co.whitbread.marketing.utils.BartResponseValidator;
import uk.co.whitbread.marketing.utils.Converter;
import uk.co.whitbread.marketing.utils.RegionSubscriptionUtils;

import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MarketingNewsletterServiceTest {

    public static final String MOCK_SERVICE_URL = "mockServiceUrl";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EMAIL_ADDRESS = "email@email.com";
    public static final String CUSTOMER_ID = "customer_id";
    public static final String COUNTRY_CODE = "GB";

    @InjectMocks
    private MarketingNewsletterService marketingNewsletterService;

    @Mock
    private Converter converter;

    @Mock
    private BartProperties mockBartProperties;

    @Mock
    private BartResponseValidator mockBartResponseValidator;

    @Mock
    private BartClient bartClient;

    @Mock
    private RegionSubscriptionUtils regionSubscriptionUtils;

    @Mock
    private SubscriptionStatus subscriptionStatus;

    @Mock
    private Subscribe subscribe;

    @BeforeEach
    void setUp() {

        when(converter.convert(any(MarketingSubscriptionInfoRequest.class))).thenReturn(subscriptionStatus);
        when(converter.convert(any(MarketingSubscriptionRequest.class))).thenReturn(subscribe);
    }

    @Test
    void subscription_ShouldMakeRequest() {

        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();

        SubscribeResponse mockSubscriptionResponse = Mockito.mock(SubscribeResponse.class);
        MarketingSubscriptionResponse mockMarketingSubscriptionResponse = Mockito.mock(MarketingSubscriptionResponse.class);

        when(bartClient.subscribeToNewsletters(any(Subscribe.class))).thenReturn(mockSubscriptionResponse);

        when(regionSubscriptionUtils.populateSubscriptionsFromRegions(any(MarketingSubscriptionRequest.class))).thenReturn(new ArrayList<>());

        when(converter.convert(any(SubscribeResponse.class))).
                thenReturn(mockMarketingSubscriptionResponse);

        //When
        MarketingSubscriptionResponse marketingSubscriptionResponse = marketingNewsletterService.subscribeToNewsletters(marketingSubscriptionRequest);

        //Then
        assertThat(marketingSubscriptionResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionResponse, is(equalTo(mockMarketingSubscriptionResponse)));
    }

    @Test
    void subscription_shouldHandleSoapFaultClientException() {

        //Given
        MarketingSubscriptionRequest mockMarketingSubscriptionRequest = Mockito.mock(MarketingSubscriptionRequest.class);

        when(regionSubscriptionUtils.populateSubscriptionsFromRegions(mockMarketingSubscriptionRequest)).thenReturn(new ArrayList<>());

        when(bartClient.subscribeToNewsletters(any(Subscribe.class))).thenThrow(BartServiceException.class);

        assertThrows(BartServiceException.class,
            () -> marketingNewsletterService.subscribeToNewsletters(mockMarketingSubscriptionRequest));
    }

    private MarketingSubscriptionRequest createSubscriptionRequest() {

        MarketingSubscriptionRequest marketingSubscriptionRequest = new MarketingSubscriptionRequest();
        SubscriptionElement subscriptionElement = new SubscriptionElement();
        subscriptionElement.setRegionId("1");
        subscriptionElement.setSubscribed(true);

        ArrayList subscribeElementList = new ArrayList<>();
        subscribeElementList.add(subscriptionElement);

        marketingSubscriptionRequest.setFirstName(FIRST_NAME);
        marketingSubscriptionRequest.setLastName(LAST_NAME);
        marketingSubscriptionRequest.setEmailAddress(EMAIL_ADDRESS);
        marketingSubscriptionRequest.setCountryCode(COUNTRY_CODE);
        marketingSubscriptionRequest.setBusinessClient(true);
        marketingSubscriptionRequest.setRegions(subscribeElementList);

        return marketingSubscriptionRequest;
    }

    @Test
    void subscriptionInfo_ShouldMakeRequest() {

        //Given
        SubscriptionStatusResponse mockSubscriptionStatusResponse = Mockito.mock(SubscriptionStatusResponse.class);
        MarketingSubscriptionInfoResponse mockMarketingSubscriptionInfoResponse = Mockito.mock(MarketingSubscriptionInfoResponse.class);

        when(bartClient.retrieveSubscriptionInfo(any(SubscriptionStatus.class))).thenReturn(mockSubscriptionStatusResponse);

        when(regionSubscriptionUtils.extractRegions(anyList())).thenReturn(new ArrayList());

        when(converter.convert(any(SubscriptionStatusResponse.class))).
                thenReturn(mockMarketingSubscriptionInfoResponse);

        //When
        MarketingSubscriptionInfoResponse marketingSubscriptionStatusResponse = marketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS);

        //Then
        assertThat(marketingSubscriptionStatusResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionStatusResponse, is(equalTo(mockMarketingSubscriptionInfoResponse)));
    }

    @Test
    void subscriptionInfo_shouldHandleSoapFaultClientException() {

        //Given
        when(bartClient.retrieveSubscriptionInfo(any(SubscriptionStatus.class))).thenThrow(BartServiceException.class);

        assertThrows(BartServiceException.class,
            () -> marketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS));
    }

    @Test
    void subscriptionStatus_ShouldMakeRequest() {

        //Given
        SubscriptionStatusResponse mockSubscriptionStatusResponse = Mockito.mock(SubscriptionStatusResponse.class);
        MarketingSubscriptionInfoResponse mockMarketingSubscriptionInfoResponse = Mockito.mock(MarketingSubscriptionInfoResponse.class);

        when(bartClient.retrieveSubscriptionInfo(any(SubscriptionStatus.class))).thenReturn(mockSubscriptionStatusResponse);

        when(converter.convert(any(SubscriptionStatusResponse.class))).
                thenReturn(mockMarketingSubscriptionInfoResponse);

        //When
        MarketingSubscriptionInfoResponse marketingSubscriptionStatusResponse = marketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS);

        //Then
        assertThat(marketingSubscriptionStatusResponse, is(not(nullValue())));
        assertThat(marketingSubscriptionStatusResponse, is(equalTo(mockMarketingSubscriptionInfoResponse)));
    }

    @Test
    void subscriptionStatus_shouldHandleSoapFaultClientException() {

        //Given
        when(bartClient.retrieveSubscriptionInfo(any(SubscriptionStatus.class))).thenThrow(BartServiceException.class);

        assertThrows(BartServiceException.class,
            () -> marketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS));
    }
}
