package uk.co.whitbread.hotel.account.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;

import io.restassured.RestAssured;
import java.util.List;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.hotel.account.client.worldline.model.AccountInfo;
import uk.co.whitbread.hotel.account.mapper.AccountInfoMapper;
import uk.co.whitbread.hotel.account.model.AccountInfoResponseDto;
import uk.co.whitbread.hotel.account.service.InnBusinessService;
import uk.co.whitbread.hotel.account.service.cdh.CdhService;
import uk.co.whitbread.hotel.account.service.worldline.WorldlineService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.cdh.model.company.GetCompanyResponse;
import uk.co.whitbread.shared.cdh.model.question.GetQuestionResponse;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class InnBusinessControllerTestIT {

  private static final String TETHERED_USER_ID = "DUMMY-ID";
  private static final String AUTHORIZATION_TOKEN = "DUMMY-TOKEN";
  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeID";
  private static final String EMAIL_CURRENT = "email2-test";

  @LocalServerPort
  int serverPort;

  @MockitoSpyBean
  private TokenService tokenService;
  @MockitoSpyBean
  private WorldlineService worldlineService;
  @MockitoSpyBean
  private CdhService cdhService;
  @MockitoSpyBean
  private AccountInfoMapper accountInfoMapper;
  @MockitoSpyBean
  private InnBusinessService innBusinessService;

  @BeforeEach
  void setUp() {
    RestAssured.port = serverPort;
  }

  @Test
  void getNotifications_shouldGetOkResponse() {
    //Given
    CdhEmployeeDetails employeeDetails = getEmployeeDetails();
    doReturn(employeeDetails).when(tokenService)
        .retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN);
    doReturn(new GetCompanyResponse()).when(cdhService).getCompanyDetails(any());
    doReturn(List.of(GetQuestionResponse.class)).when(cdhService).getEmployeeQuestions(any());
    doReturn(true).when(cdhService).isProfileUpdateRequired(any(),anyList(),any());

    //When
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        header("Authorization", AUTHORIZATION_TOKEN).
        params("tetheredUserId", TETHERED_USER_ID).
        params("scheme", "DE").
        log().everything().
        when().
        get("/innb/notifications").
        then().
        log().everything().
        statusCode(HttpStatus.SC_OK).
        body("profileUpdateRequired", is(true))
    ;
  }

  @Test
  void getNotifications_shouldThrowError() {
    //Given
    CdhEmployeeDetails employeeDetails = getEmployeeDetails();
    doReturn(employeeDetails).when(tokenService)
        .retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN);
    doReturn(new GetCompanyResponse()).when(cdhService).getCompanyDetails(any());
    doReturn(List.of(GetQuestionResponse.class)).when(cdhService).getEmployeeQuestions(any());
    doReturn(true).when(cdhService).isProfileUpdateRequired(any(),anyList(),any());

    //When
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        params("tetheredUserId", TETHERED_USER_ID).
        params("scheme", "GB").
        log().everything().
        when().
        get("/innb/notifications").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("details", containsInAnyOrder(
            "Required request header 'Authorization' for method parameter type String is not present"));
  }

  @Test
  void getAccountInfo_shouldReturnOkWithAccountInfo() {
    // Given
    CdhEmployeeDetails employeeDetails = getEmployeeDetails();
    var accountInfoResponse = new AccountInfoResponseDto();
    var accountInfo = new AccountInfo();
    doReturn(employeeDetails).when(tokenService)
        .retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION_TOKEN);
    doReturn(accountInfo).when(innBusinessService)
        .getAccountInfo(any(), any(), any());
    doReturn(accountInfoResponse).when(accountInfoMapper)
        .toResponseDto(any());

    // When / Then
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        header("Authorization", AUTHORIZATION_TOKEN).
        params("tetheredUserId", TETHERED_USER_ID).
        params("scheme", "GB").
        log().everything().
        when().
        get("/v1/hotel-account/innb/account").
        then().
        log().everything().
        statusCode(HttpStatus.SC_OK);
  }

  @Test
  void getAccountInfo_shouldReturnBadRequestWhenAuthorizationMissing() {
    // When / Then
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        params("tetheredUserId", TETHERED_USER_ID).
        log().everything().
        when().
        get("/v1/hotel-account/innb/account").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("details", containsInAnyOrder(
            "Required request header 'Authorization' for method parameter type String is not present"));
  }

  @Test
  void getAccountInfo_shouldReturnBadRequestWhenTetheredUserIdMissing() {
    given().
        contentType(MediaType.APPLICATION_JSON_VALUE).
        header("Authorization", AUTHORIZATION_TOKEN).
        params("scheme", "GB").
        log().everything().
        when().
        get("/v1/hotel-account/innb/account").
        then().
        log().everything().
        statusCode(HttpStatus.SC_BAD_REQUEST).
        body("details", containsInAnyOrder(
            "Required request parameter 'tetheredUserId' for method parameter type String is not present"));
  }

  private static CdhEmployeeDetails getEmployeeDetails() {
    return CdhEmployeeDetails.builder()
        .employeeAccountId(EMPLOYEE_ID)
        .companyAccountId(COMPANY_ID)
        .userEmail(EMAIL_CURRENT)
        .build();
  }
}
