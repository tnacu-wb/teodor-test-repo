package uk.co.whitbread.marketing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.marketing.client.customerhub.CustomerHubClient;
import uk.co.whitbread.marketing.client.customerhub.CustomerHubClientLegacy;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.client.customerhub.model.CustomerHubNewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.exception.CDHException;
import uk.co.whitbread.marketing.exception.OauthClientException;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetRequest;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.properties.CustomerHubProperties;
import uk.co.whitbread.marketing.utils.Converter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerHubServiceTest {

    public static final String CUSTOMER_ID = "customer001";

    private CustomerHubService target;

    @BeforeEach
    public void setup() {
        target = new CustomerHubService(converter, customerHubClientLegacy, customerHubClient);
    }

    @Mock
    private CustomerHubClientLegacy customerHubClientLegacy;

    @Mock
    private CustomerHubProperties customerHubProperties;

    @Mock
    private CustomerHubClient customerHubClient;

    @Mock
    private Converter converter;

    @Mock
    private CustomerHubNewsletterPreferencesEditRequest customerHubNewsletterPreferencesEditRequestMock;

    @Mock
    private CustomerHubNewsletterPreferencesUpdateRequest customerHubNewsletterPreferencesUpdateRequest;

    @Mock
    private CustomerHubNewsletterPreferencesGetRequest customerHubNewsletterPreferencesGetRequest;

    @Mock
    private NewsletterPreferencesGetResponse newsletterPreferencesGetResponse;

    @Mock
    private PreferencesGetResponse preferencesGetResponse;

    @Test
    public void editNewsletterPreferences_ShouldCallCustomerHubClientWithOauth() {
        NewsletterPreferencesEditRequest newsletterPreferencesEditRequest = new NewsletterPreferencesEditRequest();

        when(converter.convertToCustomerHubNewsletterPrefEditReq(newsletterPreferencesEditRequest)).
                thenReturn(customerHubNewsletterPreferencesEditRequestMock);


        target.editNewsletterPreferences(newsletterPreferencesEditRequest);

        //Then
        verify(customerHubClient).editNewsletterPreferences(customerHubNewsletterPreferencesEditRequestMock);
    }

    @Test
    public void updateNewsletterPreferences_ShouldCallCustomerHubClient() {

        //Given
        NewsletterPreferencesUpdateRequest newsletterPreferencesUpdateRequest = new NewsletterPreferencesUpdateRequest();

        //When
        doNothing().when(customerHubClientLegacy).updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequest);


        when(converter.convertToCustomerHubNewsletterPrefUpdateReq(CUSTOMER_ID, newsletterPreferencesUpdateRequest)).
                thenReturn(customerHubNewsletterPreferencesUpdateRequest);


        target.updateNewsletterPreferences(CUSTOMER_ID, newsletterPreferencesUpdateRequest);

        //Then
        verify(customerHubClientLegacy).updateNewsletterPreferences(customerHubNewsletterPreferencesUpdateRequest);
    }

    @Test
    public void getNewsletterPreferences_ShouldThrowOauthException() {

        NewsletterPreferencesGetRequest newsletterPreferencesGetRequest = new NewsletterPreferencesGetRequest();

        when(converter.convertToCustomerHubNewsletterPreferencesGetRequest(newsletterPreferencesGetRequest)).
                thenReturn(customerHubNewsletterPreferencesGetRequest);

        when(customerHubClient.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest))
                .thenThrow(OauthClientException.class);

        assertThrows(OauthClientException.class,
            () -> target.getNewsletterPreferences(newsletterPreferencesGetRequest));

    }

    @Test
    public void getNewsletterPreferences_ShouldThrowCDHException() {
        NewsletterPreferencesGetRequest newsletterPreferencesGetRequest = new NewsletterPreferencesGetRequest();

        when(converter.convertToCustomerHubNewsletterPreferencesGetRequest(newsletterPreferencesGetRequest)).
                thenReturn(customerHubNewsletterPreferencesGetRequest);

        when(customerHubClient.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest))
                .thenThrow(CDHException.class);

        assertThrows(CDHException.class,
            () -> target.getNewsletterPreferences(newsletterPreferencesGetRequest));
    }

    @Test
    public void getPreferences_ShouldCallCustomerHubClientWithOauth() {

        PreferencesGetRequest newsletterPreferencesGetRequest = new PreferencesGetRequest();

        when(converter.convertToCustomerHubPreferencesGetRequest(newsletterPreferencesGetRequest)).
                thenReturn(customerHubNewsletterPreferencesGetRequest);

        when(customerHubClient.getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest))
                .thenReturn(newsletterPreferencesGetResponse);

        when(converter.convertCustomerHubToPreferencesGetResponse(newsletterPreferencesGetResponse)).
                thenReturn(preferencesGetResponse);

        target.getPreferences(newsletterPreferencesGetRequest);

        //Then
        verify(customerHubClient).getNewsletterPreferences(customerHubNewsletterPreferencesGetRequest);
        verify(converter).convertCustomerHubToPreferencesGetResponse(newsletterPreferencesGetResponse);
    }
}
