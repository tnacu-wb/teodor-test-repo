package uk.co.whitbread.company.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.whitbread.company.config.TestApplication;
import uk.co.whitbread.company.config.TestSecurityConfig;
import uk.co.whitbread.company.model.BookingAlerts;
import uk.co.whitbread.company.model.BookingAllowances;
import uk.co.whitbread.company.service.cdh.CdhAuthorizationService;
import uk.co.whitbread.company.service.cdh.CdhBookingAlertsService;
import uk.co.whitbread.company.service.cdh.CdhBookingAllowancesService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;

@SpringBootTest(
    classes = {TestApplication.class, TestSecurityConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("testCdh")
class CdhCompaniesControllerTest {

  private static final String COMPANY_ID = "companyId";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";

  @LocalServerPort
  private int serverPort;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private CdhAuthorizationService cdhAuthorizationService;
  @MockitoBean
  private CdhBookingAlertsService cdhBookingAlertsService;
  @MockitoBean
  private CdhBookingAllowancesService cdhBookingAllowancesService;
  @MockitoBean
  private TokenService tokenService;

  private final HttpClient httpClient = HttpClient.newHttpClient();

  @Test
  void updateBookingAlerts_shouldGetNoContentResponse() throws Exception {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    BookingAlerts bookingAlerts = new BookingAlerts();

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(cdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + serverPort + "/companies/admin/" + COMPANY_ID + "/booking-alerts"))
        .header("Content-Type", "application/json")
        .header("Authorization", AUTHORIZATION)
        .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bookingAlerts)))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(204);

    verify(cdhBookingAlertsService).updateBookingAlerts(COMPANY_ID, bookingAlerts,
        USER_EMAIL_FROM_TOKEN);
  }

  @Test
  void updateBookingAllowances_shouldGetNoContentResponse() throws Exception {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN)
        .build();
    BookingAllowances bookingAllowances = new BookingAllowances();
    bookingAllowances.setExtrasCodes(List.of("1", "2"));

    when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);
    when(cdhAuthorizationService.isSameCompany(COMPANY_ACCOUNT_ID_FROM_TOKEN, COMPANY_ID))
        .thenReturn(true);
    when(cdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
        .thenReturn(true);

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + serverPort + "/companies/admin/" + COMPANY_ID + "/booking-allowances"))
        .header("Content-Type", "application/json")
        .header("Authorization", AUTHORIZATION)
        .PUT(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bookingAllowances)))
        .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(204);

    verify(cdhBookingAllowancesService).updateBookingAllowances(eq(COMPANY_ID), any(BookingAllowances.class),
        eq(USER_EMAIL_FROM_TOKEN));
  }
}
