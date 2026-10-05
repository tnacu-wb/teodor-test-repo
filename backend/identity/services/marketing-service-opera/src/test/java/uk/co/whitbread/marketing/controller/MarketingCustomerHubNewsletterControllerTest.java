package uk.co.whitbread.marketing.controller;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.marketing.model.ContactChannel;
import uk.co.whitbread.marketing.model.ContactType;
import uk.co.whitbread.marketing.model.EditSubscription;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.model.Subscription;
import uk.co.whitbread.marketing.service.CustomerHubService;

import java.util.ArrayList;
import uk.co.whitbread.shared.auth.service.ManagementService;
import uk.co.whitbread.shared.auth.service.TokenService;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"disable-caching"})
class MarketingCustomerHubNewsletterControllerTest {

    private static final String MARKETING_ENDPOINT = "/marketing/hotels/newsletter";
    private static final String EMAIL_ADDRESS = "email@email.com";
    private static final String CUSTOMER_ID = "customer-id-test";
    static final String[] BRAND_CODES = {"PINN", "WINN"};
    static final String CORRELATION_ID = "correlation-id-test";

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private CustomerHubService customerHubService;

    @MockitoBean
    private TokenService authTokenService;

    @MockitoBean
    private ManagementService authManagementService;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.port = serverPort;
    }

    @Test
    void editNewsletterPreferences_shouldGetOKResponse() {

        //Given

        NewsletterPreferencesEditRequest newsletterPreferencesEditRequest = createNewsletterPreferencesEditRequest();

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(newsletterPreferencesEditRequest).
                header(new Header("authenticationKey", "key")).
                log().everything().
                when().
                put(MARKETING_ENDPOINT +"/edit").
                then().
                log().everything().
                statusCode(HttpStatus.SC_NO_CONTENT);

    }

    private NewsletterPreferencesEditRequest createNewsletterPreferencesEditRequest() {

        NewsletterPreferencesEditRequest newsletterPreferencesEditRequest = new NewsletterPreferencesEditRequest();

        final EditSubscription subscription = EditSubscription.builder()
                .contactChannelType(ContactType.Email)
                .contactChannelSubType("")
                .contactChannelValue(EMAIL_ADDRESS)
                .contactChannelPermission(true)
                .brandCodes(BRAND_CODES)
                .contentPermission(new EditSubscription.ContentPermission(true, false))
                .build();


        final ArrayList<EditSubscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        newsletterPreferencesEditRequest.setSubscriptionData(subscriptions);

        return newsletterPreferencesEditRequest;
    }


    @Test
    void updateNewsletterPreferences_shouldGetOKResponse() {

        //Given

        NewsletterPreferencesUpdateRequest newsletterPreferencesUpdateRequest = createNewsletterPreferencesUpdateRequest();

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(newsletterPreferencesUpdateRequest).
                header(new Header("authenticationKey", "key")).
                log().everything().
                when().
                put(MARKETING_ENDPOINT + "/" + CUSTOMER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_NO_CONTENT);

    }

    private NewsletterPreferencesUpdateRequest createNewsletterPreferencesUpdateRequest() {

        NewsletterPreferencesUpdateRequest newsletterPreferencesUpdateRequest = new NewsletterPreferencesUpdateRequest();
        newsletterPreferencesUpdateRequest.setCorrelationId(CORRELATION_ID);

        final Subscription subscription = new Subscription();
        subscription.setContactType(ContactType.Email);
        subscription.setContactValue(EMAIL_ADDRESS);
        subscription.setSubscribe(true);

        final ArrayList<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);

        newsletterPreferencesUpdateRequest.setSubscriptions(subscriptions);

        return newsletterPreferencesUpdateRequest;
    }

    @Test
    void getNewsletterPreferences_shouldGetOKResponse() {

        //Given
        ContactChannel contactChannel = ContactChannel.builder().contactChannelType(ContactType.Email)
                .contactChannelId("contact-id").build();
        NewsletterPreferencesGetRequest newsletterPreferencesGetRequest = new NewsletterPreferencesGetRequest();
        newsletterPreferencesGetRequest.setRequestId("request-id");
        newsletterPreferencesGetRequest.setContactChannel(contactChannel);
        newsletterPreferencesGetRequest.setBrandCodes(new String[]{"PINN","WINN"});


        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(newsletterPreferencesGetRequest).
                header(new Header("authenticationKey", "key")).
                log().everything().
                when().
                post(MARKETING_ENDPOINT +"/get").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);

    }


}
