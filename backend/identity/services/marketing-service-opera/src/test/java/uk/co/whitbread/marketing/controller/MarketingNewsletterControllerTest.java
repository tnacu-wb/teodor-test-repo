package uk.co.whitbread.marketing.controller;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.bart.exceptions.BartServiceException;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.model.RegionSubscription;
import uk.co.whitbread.marketing.service.MarketingNewsletterService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"disable-caching"})
class MarketingNewsletterControllerTest {

    private static final String MARKETING_ENDPOINT = "/marketing/hotels/newsletter";
    private static final String MARKETING_INFO_ENDPOINT = "/marketing/hotels/newsletter/{emailAddress}";
    private static final String MARKETING_STATUS_ENDPOINT = "/marketing/hotels/newsletter/{emailAddress}/status";
    private static final String EMAIL_ADDRESS = "email@email.com";
    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String FIRST_NAME = "Arya";
    private static final String LAST_NAME = "Stark";
    private static final String COUNTRY_CODE = "GB";
    private static final String SESSION_ID = "SessionId";

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private MarketingNewsletterService mockMarketingNewsletterService;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
    }

    @Test
    void subscription_shouldGetOKResponse() {

        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();
        MarketingSubscriptionResponse marketingSubscriptionResponse = new MarketingSubscriptionResponse();
        marketingSubscriptionResponse.setSuccess(true);
        marketingSubscriptionResponse.setEmailAddress(marketingSubscriptionRequest.getEmailAddress());

        when(mockMarketingNewsletterService.subscribeToNewsletters(any(MarketingSubscriptionRequest.class))).
                thenReturn(marketingSubscriptionResponse);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("success", is(equalTo(true))).
                body("emailAddress", is(equalTo(EMAIL_ADDRESS)));

    }

    @Test
    void subscription_shouldGetNOOKResponse() {

        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();

        when(mockMarketingNewsletterService.subscribeToNewsletters(any(MarketingSubscriptionRequest.class))).
                thenThrow(new BartServiceException(ERROR_MESSAGE));

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("100"))).
                body("details", hasSize(1)).
                body("details", hasItem(ERROR_MESSAGE));

    }

    @Test
    void subscription_shouldHandleNoEmailAndNotCustomerIdValidationError() {
        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();
        marketingSubscriptionRequest.setEmailAddress(null);

        MarketingSubscriptionResponse marketingSubscriptionResponse = new MarketingSubscriptionResponse();
        marketingSubscriptionResponse.setSuccess(false);


        when(mockMarketingNewsletterService.subscribeToNewsletters(any(MarketingSubscriptionRequest.class))).
                thenReturn(marketingSubscriptionResponse);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(1)).
                body("details", hasItem("EmailAddress or CustomerId must be present in request"));

    }

    @Test
    void subscription_shouldHandleNoFirstNameOrLastNameValidationError() {
        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();
        marketingSubscriptionRequest.setFirstName(null);
        marketingSubscriptionRequest.setLastName(null);

        MarketingSubscriptionResponse marketingSubscriptionResponse = new MarketingSubscriptionResponse();
        marketingSubscriptionResponse.setSuccess(false);


        when(mockMarketingNewsletterService.subscribeToNewsletters(any(MarketingSubscriptionRequest.class))).
                thenReturn(marketingSubscriptionResponse);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(2)).
                body("details", hasItem("firstName must not be blank")).
                body("details", hasItem("lastName must not be blank"));

    }

    @Test
    void subscription_shouldHandleValidationErrorForRegionSubscription() {
        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();
        RegionSubscription firstRegionSubscription =
                marketingSubscriptionRequest.getRegionSubscriptions().get(0);
        firstRegionSubscription.setRegionId(null);
        firstRegionSubscription.setSubscribed(null);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(2)).
                body("details", hasItem("regionSubscriptions[0].regionId must not be null")).
                body("details", hasItem("regionSubscriptions[0].subscribed must not be null"));

    }

    @Test
    void subscription_shouldHandleInternalServerError() {
        //Given
        MarketingSubscriptionRequest marketingSubscriptionRequest = createSubscriptionRequest();

        when(mockMarketingNewsletterService.subscribeToNewsletters(any(MarketingSubscriptionRequest.class))).
                thenThrow(new RuntimeException("Internal Exception"));

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                body(marketingSubscriptionRequest).
                log().everything().
                when().
                post(MARKETING_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body("code", is(equalTo("999"))).
                body("details", hasSize(1)).
                body("details", hasItem("Internal Exception"));
    }

    @Test
    void subscriptionInfo_shouldGetOKResponse() {

        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, true);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = new MarketingSubscriptionInfoResponse();
        marketingSubscriptionInfoResponse.setSubscribedStatus(true);
        marketingSubscriptionInfoResponse.setEmailAddress(EMAIL_ADDRESS);
        marketingSubscriptionInfoResponse.setFirstName(FIRST_NAME);
        marketingSubscriptionInfoResponse.setLastName(LAST_NAME);
        marketingSubscriptionInfoResponse.setCountryCode(COUNTRY_CODE);
        marketingSubscriptionInfoResponse.setBusinessClient(true);
        marketingSubscriptionInfoResponse.setRestaurantNewsletter(true);
        marketingSubscriptionInfoResponse.setSessionId(SESSION_ID);
        marketingSubscriptionInfoResponse.setRegionSubscriptions(Arrays.asList(new RegionSubscription("1", true), new RegionSubscription("2", false)));

        when(mockMarketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS)).
                thenReturn(marketingSubscriptionInfoResponse);


        RequestSpecification request = given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().
                everything();

        Response response = request.when()
                .get(MARKETING_INFO_ENDPOINT);

        ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.SC_OK)
                .body("subscribedStatus", is(equalTo(true)))
                .body("emailAddress", is(equalTo(emailAddressTest)))
                .body("firstName", is(equalTo(FIRST_NAME)))
                .body("lastName", is(equalTo(LAST_NAME)))
                .body("countryCode", is(equalTo(COUNTRY_CODE)))
                .body("businessClient", is(equalTo(true)))
                .body("sessionId", is(equalTo(SESSION_ID)))
                .body("restaurantNewsletter", is(equalTo(true)));
    }

    @Test
    void subscriptionInfo_shouldGetOKResponse_ButSubscribedFalse() {
        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, true);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = new MarketingSubscriptionInfoResponse();
        marketingSubscriptionInfoResponse.setSubscribedStatus(false);

        when(mockMarketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS)).
                thenReturn(marketingSubscriptionInfoResponse);

        RequestSpecification request = given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().
                everything();

        Response response = request.when()
                .get(MARKETING_INFO_ENDPOINT);

        ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.SC_OK)
                .body("subscribedStatus", is(equalTo(false)));
    }

    @Test
    void subscriptionInfo_shouldGetNoOKResponse() {

        //Given
        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, true);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        when(mockMarketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS)).
                thenThrow(new BartServiceException(ERROR_MESSAGE));

        //When
        given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get(MARKETING_INFO_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("100"))).
                body("details", hasSize(1)).
                body("details", hasItem(ERROR_MESSAGE));

    }

    @Test
    void subscriptionInfo_shouldHandleInternalServerError() {
        //Given
        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, true);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        when(mockMarketingNewsletterService.getSubscriptionInfo(EMAIL_ADDRESS)).
                thenThrow(new RuntimeException("Internal Exception"));

        //When
        given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get(MARKETING_INFO_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body("code", is(equalTo("999"))).
                body("details", hasSize(1)).
                body("details", hasItem("Internal Exception"));

    }

    @Test
    void subscriptionStatus_shouldGetOKResponse() {

        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, false);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = new MarketingSubscriptionInfoResponse();
        marketingSubscriptionInfoResponse.setSubscribedStatus(true);
        marketingSubscriptionInfoResponse.setEmailAddress(EMAIL_ADDRESS);

        when(mockMarketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS)).
                thenReturn(marketingSubscriptionInfoResponse);


        RequestSpecification request = given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().
                everything();

        Response response = request.when()
                .get(MARKETING_STATUS_ENDPOINT);

        ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.SC_OK)
                .body("subscribedStatus", is(equalTo(true)))
                .body("emailAddress", is(equalTo(emailAddressTest)));
    }

    @Test
    void subscriptionStatus_shouldGetOKResponse_ButSubscribedFalse() {
        MarketingSubscriptionInfoRequest marketingSubscriptionInfoRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, false);
        String emailAddressTest = marketingSubscriptionInfoRequest.getEmailAddress();

        MarketingSubscriptionInfoResponse marketingSubscriptionInfoResponse = new MarketingSubscriptionInfoResponse();
        marketingSubscriptionInfoResponse.setSubscribedStatus(false);
        marketingSubscriptionInfoResponse.setEmailAddress(EMAIL_ADDRESS);

        when(mockMarketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS)).
                thenReturn(marketingSubscriptionInfoResponse);

        RequestSpecification request = given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().
                everything();

        Response response = request.when()
                .get(MARKETING_STATUS_ENDPOINT);

        ValidatableResponse validatableResponse = response.then()
                .log()
                .everything();
        validatableResponse.statusCode(HttpStatus.SC_OK)
                .body("subscribedStatus", is(equalTo(false)))
                .body("emailAddress", is(equalTo(EMAIL_ADDRESS)));
    }

    @Test
    void subscriptionStatus_shouldGetNoOKResponse() {

        //Given
        MarketingSubscriptionInfoRequest marketingSubscriptionStatusRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, false);
        String emailAddressTest = marketingSubscriptionStatusRequest.getEmailAddress();

        when(mockMarketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS)).
                thenThrow(new BartServiceException(ERROR_MESSAGE));

        //When
        given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get(MARKETING_STATUS_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("100"))).
                body("details", hasSize(1)).
                body("details", hasItem(ERROR_MESSAGE));

    }

    @Test
    void subscriptionStatus_shouldHandleInternalServerError() {
        //Given
        MarketingSubscriptionInfoRequest marketingSubscriptionStatusRequest = new MarketingSubscriptionInfoRequest(EMAIL_ADDRESS, true);
        String emailAddressTest = marketingSubscriptionStatusRequest.getEmailAddress();

        when(mockMarketingNewsletterService.getSubscriptionStatus(EMAIL_ADDRESS)).
                thenThrow(new RuntimeException("Internal Exception"));

        //When
        given().
                pathParam("emailAddress", emailAddressTest).
                accept(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get(MARKETING_STATUS_ENDPOINT).
                then().
                log().everything().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                body("code", is(equalTo("999"))).
                body("details", hasSize(1)).
                body("details", hasItem("Internal Exception"));

    }


    private MarketingSubscriptionRequest createSubscriptionRequest() {
        MarketingSubscriptionRequest marketingSubscriptionRequest = new MarketingSubscriptionRequest();
        marketingSubscriptionRequest.setFirstName(FIRST_NAME);
        marketingSubscriptionRequest.setLastName(LAST_NAME);
        marketingSubscriptionRequest.setEmailAddress(EMAIL_ADDRESS);
        marketingSubscriptionRequest.setCountryCode(COUNTRY_CODE);
        marketingSubscriptionRequest.setBusinessClient(true);
        marketingSubscriptionRequest.setRegionSubscriptions(newRegionsArraylist());
        return marketingSubscriptionRequest;
    }

    private List<RegionSubscription> newRegionsArraylist() {
        ArrayList<RegionSubscription> arrayList = new ArrayList<>();
        arrayList.add(new RegionSubscription("1", true));
        arrayList.add(new RegionSubscription("2", false));
        arrayList.add(new RegionSubscription("3", true));
        return arrayList;
    }
}
