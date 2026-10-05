package uk.co.whitbread.hotel.account.controller;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.account.helper.AuthTestHelper.configureAuth0Context;
import static uk.co.whitbread.hotel.account.model.BookingStatus.FUTURE;
import static uk.co.whitbread.hotel.account.model.SortOrder.DEFAULT;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.hotel.account.model.SearchCustomerRequest;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysFilterType;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.StaysTypesTotals;
import uk.co.whitbread.hotel.account.service.HotelAccountsService;
import uk.co.whitbread.hotel.account.service.cdh.CdhBbBookingsService;
import uk.co.whitbread.hotel.account.service.cdh.CdhPiBookingsService;
import uk.co.whitbread.shared.auth.account.CCUIDetails;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;
import uk.co.whitbread.shared.cdh.CustomerDataService;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class HotelAccountsControllerTestIT {

    private static final String CCUI_BOOKING_FLOW = "CCUI";
    private static final String EMAIL = "dummy@email.com";
    private static final String COMPANY_ID = "companyId";
    private static final String EMPLOYEE_ID = "employeeId";
    public static final String COMPANY_ACCOUNT_ID = "c7ab3e7c-54fd-4011-961e-05b468176c1b";
    public static final String EMPLOYEE_ACCOUNT_ID = "8d2786bc-7efa-4ecd-9d3f-277757c096eb";
    private static final String MY_USER_ID = "any.user@email.com";
    private static final String CDH_CUSTOMER_ACCOUNT_ID = "6fabfb69-e2ab-4705-bd7e-8828cd208aea";
    private static final String COMPANY_ID_PARAM = "companyId";
    private static final String EMPLOYEE_ID_PARAM = "employeeId";
    private static final String TYPE_OF_BOOKING_PARAM = "typeOfBooking";
    private static final String SORT_ORDER = "sortOrder";
    private static final String BUSINESS_PARAM = "business";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String AUTHORIZATION_TOKEN = "AuthorizationToken";
    private static final String BEARER_TOKEN = "Bearer " + AUTHORIZATION_TOKEN;
    private static final String PAGE_INDEX = "pageIndex";
    private static final String PAGE_SIZE = "pageSize";
    private static final String TOTAL_SIZE = "totalSize";

    private static final int DEFAULT_PAGE_INDEX = 1;
    private static final int EMPTY_PAGE_SIZE = 0;
    private static final int EMPTY_LIST_SIZE = 0;

    @LocalServerPort
    int serverPort;

    @MockitoSpyBean
    private HotelAccountsService mockHotelAccountsService;
    @MockitoBean
    private TokenService mockAuthTokenService;
    @MockitoBean
    private CustomerDataService mockCustomerDataService;
    @MockitoBean
    private CdhPiBookingsService mockCdhPiBookingsService;
    @MockitoBean
    private CdhBbBookingsService mockCdhBbBookingsService;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @Autowired
    private TenantRepository tenantRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = serverPort;
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        configureAuth0Context(tenantRepository, jwtDecoder);
    }

    private StaysResponse mockStaysResponse() {
        return StaysResponse.builder()
            .pageIndex(1)
            .build();
    }

    @Test
    void futureStays_shouldGetOKResponse() {
        //Given
        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(anyString()))
            .thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockCdhPiBookingsService.retrieveCdhPiBookingsV2(any(BBStaysRequest.class),
            anyString(), anyString(), anyInt(), any())).thenReturn(mockStaysResponse());

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, BEARER_TOKEN).
                queryParam(TYPE_OF_BOOKING_PARAM, FUTURE).
                queryParam(SORT_ORDER, DEFAULT).
                log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", "myUserId").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void futureStays_shouldHandleMissingHeader() {
        //Given

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", "myUserId").
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("013"))).
                body("details", hasSize(1)).
                body("details", hasItem("Required request header 'Authorization' for method parameter type String is not present"));
    }

    @Test
    void getPiCdhBookings_shouldGetOKResponse() {
        //Given
        int pageSize = 20;
        StaysResponse response = new StaysResponse();
        response.setPageSize(pageSize);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
                AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockCdhPiBookingsService.retrieveCdhPiBookingsV2(any(BBStaysRequest.class),
                eq(CDH_CUSTOMER_ACCOUNT_ID), eq(MY_USER_ID), anyInt(), anyInt())).thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
                queryParam(BUSINESS_PARAM, false).
                queryParam(PAGE_SIZE, pageSize).
                log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("pageSize", equalTo(20));
    }

    @Test
    void bbStays_shouldGetOKResponse() {
        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequest.class),
            any(CdhEmployeeDetails.class), anyInt(), any())).thenReturn(mockStaysResponse());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("pageIndex", is(equalTo(DEFAULT_PAGE_INDEX))).
                body("pageSize", is(equalTo(EMPTY_PAGE_SIZE))).
                body("totalSize", is(equalTo(EMPTY_LIST_SIZE)));
    }

    @Test
    void bbStays_shouldThrowValidationExceptionWhenEmployeeIdIsNull() {

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST).
                body("code", is(equalTo("001"))).
                body("details", hasSize(1)).
                body("details", hasItem("employeeId must not be null"));
    }

    @Test
    void bbStays_NoQueryParameters_DoNotThrowNullPointerException() {

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockCdhPiBookingsService.retrieveCdhPiBookingsV2(any(BBStaysRequest.class),
            anyString(), anyString(), anyInt(), any())).thenReturn(mockStaysResponse());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .log().everything().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void bbPaginatedStays_NegativeIndex_ValidationError() {

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .queryParam(PAGE_INDEX, -5)
                .log().everything().
        when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
        then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("code", is("001"));
    }

    @Test
    void bbPaginatedStays_InvalidPageSize_ValidationError() {

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .queryParam(PAGE_SIZE, -5)
                .log().everything().
        when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
        then().
                log().everything().
                statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("code", is("001"));
    }

    @Test
    void bbPaginatedStays_WithoutPaginationParams_ShouldReturnOk() {
        List<Stay> stays = Collections.nCopies(5, new Stay());
        stays.forEach(stay -> stay.setBookingStatus(BookingStatus.FUTURE));

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);
        staysResponse.setPageIndex(1);
        staysResponse.setPageSize(5);
        staysResponse.setTotalSize(5);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequest.class),
            any(CdhEmployeeDetails.class), anyInt(), any())).thenReturn(staysResponse);

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .log().everything().
        when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
        then().
                log().everything().
                statusCode(HttpStatus.SC_OK)
                .body(PAGE_INDEX, is(DEFAULT_PAGE_INDEX))
                .body(PAGE_SIZE, is(5))
                .body(TOTAL_SIZE, is(5));

    }

    @Test
    void bbPaginatedStays_ValidRequest_ShouldReturnOk() {
        List<Stay> stays = Collections.nCopies(5, new Stay());
        stays.forEach(stay -> stay.setBookingStatus(BookingStatus.FUTURE));

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);
        staysResponse.setPageIndex(1);
        staysResponse.setPageSize(5);
        staysResponse.setTotalSize(5);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequest.class),
            any(CdhEmployeeDetails.class), anyInt(), any())).thenReturn(staysResponse);

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
                .queryParam(COMPANY_ID_PARAM, COMPANY_ID)
                .queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID)
                .queryParam(TYPE_OF_BOOKING_PARAM, FUTURE)
                .queryParam(BUSINESS_PARAM, true)
                .queryParam(PAGE_INDEX, 1)
                .queryParam(PAGE_SIZE, 7)
                .log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK)
                .body(PAGE_INDEX, is(DEFAULT_PAGE_INDEX))
                .body(PAGE_SIZE, is(5))
                .body(TOTAL_SIZE, is(5));
    }

    @Test
    void getCustomer_hasNoTokenOrSessionID_ShouldThrowException() {
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.empty());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                log().everything().
                get("/customers/hotels/{customer-id}", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    @Test
    void getCustomer_tokenHasNoSessionId_ShouldThrowException() {
        when(mockAuthTokenService.retrieveAndVerifyToken(anyString())).thenReturn(Optional.empty());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, "invalid_token").
                log().everything().
                get("/customers/hotels/{customer-id}", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    void getCustomer_hasValidToken_ShouldNotThrowException() {

        doReturn(new Customer()).when(mockHotelAccountsService)
            .getCustomer(any(HotelCustomerRequest.class));

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, "Bearer random_token").
                log().everything().
                get("/customers/hotels/{customer-id}", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void getCustomer_hasTrueBusinessParam_ShouldNotThrowException() {

        doReturn(new Customer()).when(mockHotelAccountsService)
            .getCustomer(any(HotelCustomerRequest.class));

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, "Bearer random_token").
            queryParam(BUSINESS_PARAM, true).
            log().everything().
            get("/customers/hotels/{customer-id}", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK);

    }

    @Test
    void searchCustomer_hasValidToken_ShouldNotThrowException() {

        when(mockAuthTokenService.retrieveAndVerifyCCUIToken(AUTHORIZATION_TOKEN))
                .thenReturn(CCUIDetails.builder().bookingFlow(CCUI_BOOKING_FLOW).email(EMAIL).build());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
                body(new SearchCustomerRequest()).
                log().everything().
                post("/customers/hotels/search").
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);

        verify(mockHotelAccountsService).getCustomer(any(SearchCustomerRequest.class), anyString());
    }

    @Test
    void searchCustomer_hasInvalidToken_ShouldThrowException() {

        when(mockAuthTokenService.retrieveAndVerifyCCUIToken(AUTHORIZATION_TOKEN))
                .thenReturn(CCUIDetails.builder().build());

        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
                body(new SearchCustomerRequest()).
                log().everything().
                post("/customers/hotels/search").
                then().
                log().everything().
                statusCode(HttpStatus.SC_UNAUTHORIZED).
                body("code", is("7101")).
                body("details", hasItem("Provided token is invalid or expired"));

        verify(mockHotelAccountsService, never()).getCustomer(any(SearchCustomerRequest.class), anyString());
    }

    @Test
    void deleteCustomer_cdhEnabled_success() {
        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(AUTHORIZATION_TOKEN))
            .thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockAuthTokenService.retrieveEmailAndVerifyToken(AUTHORIZATION_TOKEN))
            .thenReturn(Optional.of(EMAIL));
        doNothing().when(mockHotelAccountsService)
            .deleteCdhPICustomer(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            log().everything().
            delete("/customers/hotels/{customer-id}", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("customerId", equalTo(MY_USER_ID)).
            body("success", is(true));


        verify(mockAuthTokenService).retrieveCustomerAccountIdAndVerifyToken(AUTHORIZATION_TOKEN);
        verify(mockAuthTokenService).retrieveEmailAndVerifyToken(AUTHORIZATION_TOKEN);
        verify(mockHotelAccountsService).deleteCdhPICustomer(CDH_CUSTOMER_ACCOUNT_ID, EMAIL);

    }

    @Test
    void filtered_retrieveStays_shouldGetOKResponse() {
        //Given
        StaysResponse response = new StaysResponse();
        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setFilterType(StaysFilterType.NAME);
        bbStaysRequest.setFilterValue("Katya");

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequest.class),
            any(CdhEmployeeDetails.class), anyInt(), any())).thenReturn(response);

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
                queryParam(COMPANY_ID_PARAM, COMPANY_ID).
                queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID).
                queryParam(TYPE_OF_BOOKING_PARAM, FUTURE).
                queryParam(BUSINESS_PARAM, true).
                log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK);
    }

    @Test
    void retrieveStays_ShouldReturnStaysTypesTotals() throws Exception {

        //Given
        StaysResponse response = getExpectedResponseWithCheckedin();
        response.setTotals(StaysTypesTotals.builder()
                .checkedIn(1)
                .past(1)
                .cancelled(1)
                .upcoming(3)
            .build());
        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequest.class),
            any(CdhEmployeeDetails.class), anyInt(), any())).thenReturn(response);
        

        //When
        given().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
                queryParam(COMPANY_ID_PARAM, COMPANY_ID).
                queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID).
                queryParam(TYPE_OF_BOOKING_PARAM, FUTURE).
                queryParam(BUSINESS_PARAM, true).
                log().everything().
                when().
                get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
                then().
                log().everything().
                statusCode(HttpStatus.SC_OK).
                body("totals.checkedIn", is(1)).
                body("totals.past", is(1)).
                body("totals.cancelled", is(1)).
                body("totals.upcoming", is(3));
    }

    @Test
    void getBbCdhBookings_shouldGetOKResponse() {
        //Given
        int pageSize = 20;
        int pageIndex = 1;
        StaysResponse response = new StaysResponse();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID).employeeAccountId(EMPLOYEE_ACCOUNT_ID).build();
        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequest.setCompanyId(COMPANY_ID);
        bbStaysRequest.setBusiness(true);
        response.setPageSize(pageSize);

        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(cdhEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(bbStaysRequest,
            cdhEmployeeDetails, pageIndex, pageSize)).thenReturn(response);

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            queryParam(BUSINESS_PARAM, true).
            queryParam(COMPANY_ID_PARAM, COMPANY_ID).
            queryParam(EMPLOYEE_ID_PARAM, EMPLOYEE_ID).
            queryParam(PAGE_SIZE, pageSize).
            log().everything().
            when().
            get("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("pageSize", equalTo(20));
    }

    @Test
    void postFutureStays_shouldGetOKResponse() {
        //Given
        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setSortOrder(DEFAULT);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(anyString()))
            .thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockCdhPiBookingsService.retrieveCdhPiBookingsV2(any(BBStaysRequestV2.class),
            anyString(), anyString(), any(), any())).thenReturn(mockStaysResponse());

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            body(bbStaysRequestV2).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", "myUserId").
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK);
    }

    @Test
    void postFutureStays_shouldHandleMissingHeader() {
        //Given

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            body(new BBStaysRequestV2()).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", "myUserId").
            then().
            log().everything().
            statusCode(HttpStatus.SC_BAD_REQUEST).
            body("code", is(equalTo("013"))).
            body("details", hasSize(1)).
            body("details",
                hasItem("Required request header 'Authorization' for method parameter type String is not present"));
    }

    @Test
    void postPiCdhBookings_shouldGetOKResponse() {
        //Given
        int pageSize = 20;
        StaysResponse response = new StaysResponse();
        response.setPageSize(pageSize);

        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setBusiness(false);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        when(mockCdhPiBookingsService.retrieveCdhPiBookingsV2(any(BBStaysRequest.class),
            eq(CDH_CUSTOMER_ACCOUNT_ID), anyString(), any(), any())).thenReturn(response);

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            body(bbStaysRequest).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", "myUserId").
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK);
    }

    @Test
    void postBbStays_shouldGetOKResponse() {

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequestV2.class),
            any(CdhEmployeeDetails.class), any(), any())).thenReturn(mockStaysResponse());

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("pageIndex", is(equalTo(DEFAULT_PAGE_INDEX))).
            body("pageSize", is(equalTo(EMPTY_PAGE_SIZE))).
            body("totalSize", is(equalTo(EMPTY_LIST_SIZE)));
    }

    @Test
    void postBbStays_shouldThrowValidationExceptionWhenEmployeeIdIsNull() {

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_BAD_REQUEST).
            body("code", is(equalTo("001"))).
            body("details", hasSize(1)).
            body("details", hasItem("employeeId must not be null"));
    }

    @Test
    void postBbPaginatedStays_NegativeIndex_ValidationError(){
        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);
        bbStaysRequestV2.setPageIndex(-5);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("code", is("001"));
    }

    @Test
    void postBbPaginatedStays_InvalidPageSize_ValidationError() {
        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);
        bbStaysRequestV2.setPageSize(-5);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_BAD_REQUEST)
            .body("code", is("001"));
    }

    @Test
    void postBbPaginatedStays_WithoutPaginationParams_ShouldReturnOk() {
        List<Stay> stays = Collections.nCopies(5, new Stay());
        stays.forEach(stay -> stay.setBookingStatus(BookingStatus.FUTURE));

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);
        staysResponse.setPageIndex(1);
        staysResponse.setPageSize(5);
        staysResponse.setTotalSize(5);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequestV2.class),
            any(CdhEmployeeDetails.class), any(), any())).thenReturn(staysResponse);

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK)
            .body(PAGE_INDEX, is(DEFAULT_PAGE_INDEX))
            .body(PAGE_SIZE, is(5))
            .body(TOTAL_SIZE, is(5));

    }

    @Test
    void postBbPaginatedStays_ValidRequest_ShouldReturnOk() {
        List<Stay> stays = Collections.nCopies(5, new Stay());
        stays.forEach(stay -> stay.setBookingStatus(BookingStatus.FUTURE));

        StaysResponse staysResponse = new StaysResponse();
        staysResponse.setStays(stays);
        staysResponse.setPageIndex(1);
        staysResponse.setPageSize(5);
        staysResponse.setTotalSize(5);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequestV2.class),
            any(CdhEmployeeDetails.class), any(), any())).thenReturn(staysResponse);

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);
        bbStaysRequestV2.setPageIndex(1);
        bbStaysRequestV2.setPageSize(7);

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN)
            .body(bbStaysRequestV2)
            .log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK)
            .body(PAGE_INDEX, is(DEFAULT_PAGE_INDEX))
            .body(PAGE_SIZE, is(5))
            .body(TOTAL_SIZE, is(5));
    }

    @Test
    void filtered_postRetrieveStays_shouldGetOKResponse() {
        //Given
        BBStaysRequest bbStaysRequest = new BBStaysRequest();
        bbStaysRequest.setFilterType(StaysFilterType.NAME);
        bbStaysRequest.setFilterValue("Katya");

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequestV2.class),
            any(CdhEmployeeDetails.class), any(), any())).thenReturn(mockStaysResponse());

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            body(bbStaysRequestV2).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK);
    }

    @Test
    void postRetrieveStays_ShouldReturnStaysTypesTotals() throws Exception {

        //Given
        StaysResponse response = getExpectedResponseWithCheckedin();
        response.setTotals(StaysTypesTotals.builder()
            .checkedIn(1)
            .past(1)
            .cancelled(1)
            .upcoming(3)
            .build());

        BBStaysRequestV2 bbStaysRequestV2 = new BBStaysRequestV2();
        bbStaysRequestV2.setCompanyId(COMPANY_ID);
        bbStaysRequestV2.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequestV2.setTypeOfBooking(FUTURE);
        bbStaysRequestV2.setBusiness(true);

        when(mockAuthTokenService.retrieveCustomerAccountIdAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(Optional.of(CDH_CUSTOMER_ACCOUNT_ID));
        CdhEmployeeDetails mockEmployeeDetails = CdhEmployeeDetails.builder()
            .employeeAccountId("test-employee-id")
            .companyAccountId("test-company-id")
            .build();
        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString()))
            .thenReturn(mockEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(any(BBStaysRequestV2.class),
            any(CdhEmployeeDetails.class), any(), any())).thenReturn(response);

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            body(bbStaysRequestV2).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK).
            body("totals.checkedIn", is(1)).
            body("totals.past", is(1)).
            body("totals.cancelled", is(1)).
            body("totals.upcoming", is(3));
    }

    @Test
    void postBbCdhBookings_shouldGetOKResponse() {
        //Given
        int pageSize = 20;
        int pageIndex = 1;
        StaysResponse response = new StaysResponse();
        CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID).employeeAccountId(EMPLOYEE_ACCOUNT_ID).build();
        BBStaysRequestV2 bbStaysRequest = new BBStaysRequestV2();
        bbStaysRequest.setEmployeeId(EMPLOYEE_ID);
        bbStaysRequest.setCompanyId(COMPANY_ID);
        bbStaysRequest.setBusiness(true);
        bbStaysRequest.setPageIndex(pageIndex);
        bbStaysRequest.setPageSize(pageSize);

        response.setPageSize(pageSize);

        when(mockAuthTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            AUTHORIZATION_TOKEN)).thenReturn(cdhEmployeeDetails);
        when(mockCdhBbBookingsService.retrieveCdhBbBookingsV2(bbStaysRequest,
            cdhEmployeeDetails, pageIndex, pageSize)).thenReturn(response);

        //When
        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            body(bbStaysRequest).
            log().everything().
            when().
            post("/customers/hotels/{customer-id}/stays", MY_USER_ID).
            then().
            log().everything().
            statusCode(HttpStatus.SC_OK);
    }

    @Test
    void putUpdateCustomer_ProfileIsUpdated() {
        var oldCustomerProfile = random(Customer.class);

        var cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID).employeeAccountId(EMPLOYEE_ACCOUNT_ID).build();

        doReturn(cdhEmployeeDetails).when(mockAuthTokenService)
            .retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN);
        doReturn(oldCustomerProfile).when(mockHotelAccountsService).getCustomer(any());
        doReturn(null).when(mockHotelAccountsService).updateBbCdhEmployee(any(), any());

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            param(BUSINESS_PARAM, true).
            body(new CustomerRequest()).
            log().everything().
            when().
            put("/customers/hotels/{customer-id}", MY_USER_ID);

        verify(mockHotelAccountsService)
            .updateWLContactDetails(eq(AUTHORIZATION_TOKEN), any(), eq(cdhEmployeeDetails));
    }

    @Test
    void putUpdateCustomer_ProfileIsNotUpdated() {
        var cdhEmployeeDetails = CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID).employeeAccountId(EMPLOYEE_ACCOUNT_ID).build();

        doReturn(cdhEmployeeDetails).when(mockAuthTokenService)
            .retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN);
        doReturn(new Customer()).when(mockHotelAccountsService).getCustomer(any());
        doReturn(null).when(mockHotelAccountsService).updateBbCdhEmployee(any(), any());

        given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            header(AUTHORIZATION_HEADER, AUTHORIZATION_TOKEN).
            param(BUSINESS_PARAM, true).
            body(new CustomerRequest()).
            log().everything().
            when().
            put("/customers/hotels/{customer-id}", MY_USER_ID);

        verify(mockHotelAccountsService, times(0)).updateWLContactDetails(any(), any(), any());
    }

    private StaysResponse getExpectedResponseWithCheckedin() throws IOException {
        return objectMapper.readValue(
                new File("src/test/resources/mapping/stays/StaysResponseAllBookingCheckedIn.json"),
                StaysResponse.class);
    }
}
