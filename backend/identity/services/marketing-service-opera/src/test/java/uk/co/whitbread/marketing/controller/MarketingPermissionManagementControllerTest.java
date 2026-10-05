package uk.co.whitbread.marketing.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.marketing.helper.AuthTestHelper.configureAuth0Context;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.marketing.model.newsletter.ContactType;
import uk.co.whitbread.marketing.model.newsletter.Permission;
import uk.co.whitbread.marketing.model.newsletter.PreferencesAnonymousGetResponse;
import uk.co.whitbread.marketing.model.newsletter.PreferencesGetResponse;
import uk.co.whitbread.marketing.model.permissionmanagement.ConfirmDoubleOptInRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.Customer;
import uk.co.whitbread.marketing.model.permissionmanagement.UnsubscribeRequest;
import uk.co.whitbread.marketing.model.permissionmanagement.UpdatePreferencesRequest;
import uk.co.whitbread.marketing.service.AuthenticationService;
import uk.co.whitbread.marketing.service.PermissionManagementService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"disable-caching"})
class MarketingPermissionManagementControllerTest {

    private static final String MARKETING_ENDPOINT = "/marketing/newsletter";

    @LocalServerPort
    int serverPort;

    @MockitoBean
    private PermissionManagementService permissionManagementService;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
        configureAuth0Context(tenantRepository, jwtDecoder);
    }

    @Test
    void getNewsletterPreferences_shouldGetOKResponse() {
        //When
        PreferencesGetResponse preferencesGetResponse = preferencesGetResponse();
        when(permissionManagementService.getPreferences(any())).thenReturn(preferencesGetResponse);
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key")).
                log().everything().
                when().
                get(MARKETING_ENDPOINT + "/email/liam.wilson@whitbread.com?brandCodes=PINN").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK)
                .body("contactChannelId", is(equalTo("1234")))
                .body("permissions[0].optIn", is(equalTo(true)))
                .body("permissions[0].brand", is(equalTo("PINN")));

    }

    @Test
    void getNewsletterPreferencesAnonymous_shouldGetOKResponse() {
        //When
        PreferencesAnonymousGetResponse response = preferencesAnonymousGetResponse();
        when(permissionManagementService.getPreferencesAnonymous(any())).thenReturn(response);
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(new Header("Authentication", "key"))
            .log()
            .everything()
            .when()
            .get(MARKETING_ENDPOINT + "/john.doe@whitbread.com?brandCode=PINN")
            .then()
            .log()
            .everything()
            .statusCode(HttpStatus.SC_OK)
            .body("optIn", is(equalTo(true)))
            .body("secondOptIn", is(equalTo(true)))
            .body("secondOptInReq", is(equalTo(true)))
            .body("secondPartyOptIn", is(equalTo(true)))
            .body("thirdPartyVendorsOptIn", is(equalTo(true)));
    }

    @Test
    void getNewsletterPreferencesWithChannelId_shouldGetOKResponse() {
        //When
        PreferencesGetResponse preferencesGetResponse = preferencesGetResponse();
        when(permissionManagementService.getPreferencesUsingContactChannelId(any())).thenReturn(preferencesGetResponse);
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get(MARKETING_ENDPOINT + "/email/channel/CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3?brandCodes=PINN").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK)
                .body("contactChannelId", is(equalTo("1234")))
                .body("permissions[0].optIn", is(equalTo(true)))
                .body("permissions[0].brand", is(equalTo("PINN")));

    }

    @Test
    void updateNewsletterPreferences_shouldGetNoContentResponse() {

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key")).
                body(createUpdatePreferencesRequest())
                .log().everything().
                when().
                put(MARKETING_ENDPOINT + "/email/liam.wilson@whitbread.com").
                then().
                log().everything().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void confirmDoubleOptIn_shouldGetNotContentResponse() {
        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key"))
                .body(createConfirmDoubleOptInRequest())
                .log().everything().
                when().
                post(MARKETING_ENDPOINT + "/channel/CHNL_2321-123/confirm").
                then().
                log().everything().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void confirmDoubleOptIn_shouldGetBadRequestError() {
        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key"))
                .body(createConfirmDoubleOptInRequest())
                .log().everything().
                when().
                post(MARKETING_ENDPOINT + "/channel/CHNL_2321-@123/confirm").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void unsubscribe_shouldGetNotContentResponse() {
        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key"))
                .body(createUnsubscribeRequest())
                .log().everything().
                when().
                delete(MARKETING_ENDPOINT + "/channel/CHNL_2321-123/unsubscribe").
                then().
                log().everything().
                statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void unsubscribe_shouldGetBadRequestError() {
        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(new Header("Authentication", "key"))
                .body(createUnsubscribeRequest())
                .log().everything().
                when().
                delete(MARKETING_ENDPOINT + "/channel/CHNL_2321-@123/unsubscribe").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    @Test
    void updateEmailPermissions_shouldGetNoContentResponse() {
        String jwt = "Bearer token";
        String email = "test@whitbread.com";
        UpdatePreferencesRequest request = createUpdatePreferencesRequest();

        when(authenticationService.extractAndValidateEmailFromJwt(jwt)).thenReturn(email);

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .header(new Header("Authorization", jwt))
            .body(request)
            .log().everything()
            .when()
            .put(MARKETING_ENDPOINT + "/email")
            .then()
            .log().everything()
            .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void updatePermissionsByContactChannelId_shouldGetNoContentResponse() {
        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(new Header("Authentication", "key")).
            body(createUpdatePreferencesRequest())
            .log().everything().
            when().
            put(MARKETING_ENDPOINT + "/channel/CHNL97722_8415a943-6800-4fbb-808d-0ce878554e64").
            then().
            log().everything().
            statusCode(HttpStatus.SC_NO_CONTENT);
    }

    private PreferencesGetResponse preferencesGetResponse () {
        PreferencesGetResponse preferencesGetResponse = new PreferencesGetResponse();
        preferencesGetResponse.setContactChannelId("1234");
        preferencesGetResponse.setDeleted(false);
        preferencesGetResponse.setValid(true);
        Permission permission = new Permission();
        permission.setBrand("PINN");
        permission.setOptIn(true);
        permission.setSecondOptIn(false);
        permission.setSecondOptInReq(false);
        permission.setSecondPartyOptIn(false);
        permission.setThirdPartyVendorsOptIn(false);
        preferencesGetResponse.setPermissions(List.of(permission));
        return preferencesGetResponse;
    }

    private PreferencesAnonymousGetResponse preferencesAnonymousGetResponse () {
        return PreferencesAnonymousGetResponse.builder()
            .optIn(true)
            .secondOptIn(true)
            .secondOptInReq(true)
            .secondPartyOptIn(true)
            .thirdPartyVendorsOptIn(true)
            .build();
    }

    private UpdatePreferencesRequest createUpdatePreferencesRequest () {
        return  UpdatePreferencesRequest.builder()
                .brandCodes(new String []{"PINN"})
                .optIn(true)
                .secondPartyOptIn(true)
                .thirdPartyVendorsOptIn(false)
                .doubleOptIn(true)
                .customer(Customer.builder().firstName("Liam").language("en").countryOfResidence("GB").nationality("GB").build())
                .build();
    }

    private ConfirmDoubleOptInRequest createConfirmDoubleOptInRequest () {
        return  ConfirmDoubleOptInRequest.builder()
                .brandCodes(new String [] {"PINN"})
                .customerId("432")
                .contactType(ContactType.email)
                .build();
    }

    private UnsubscribeRequest createUnsubscribeRequest () {
        return  UnsubscribeRequest.builder()
                .brandCodes(new String [] {"PINN"})
                .contactType(ContactType.email)
                .customerId("432")
                .build();
    }
}
