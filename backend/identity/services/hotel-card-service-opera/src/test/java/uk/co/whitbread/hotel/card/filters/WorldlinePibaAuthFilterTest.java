package uk.co.whitbread.hotel.card.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.helper.AuthTestHelper.configureAuth0Context;

import org.apache.catalina.core.ApplicationFilterChain;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class WorldlinePibaAuthFilterTest {

  private static final String ID = "9B1B3ED5";
  private static final String USER_ID = "g5453g5";
  private static final String ENDPOINT = "/innb/account/{userId}/cards/{id}/users";
  private static final String WRONG_ENDPOINT = "/innb/test/users/{id}";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String AUTHORIZATION = "Bearer token";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";

  @LocalServerPort
  private int serverPort;

  @MockitoBean
  private TokenService mockTokenService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @Mock
  private ApplicationFilterChain filterChain;

  @Autowired
  private TenantRepository tenantRepository;

  private final TestRestTemplate restTemplate = new TestRestTemplate();

  @BeforeEach
  void setUp() {
    configureAuth0Context(tenantRepository, jwtDecoder);
  }

  @Test
  void doFilterInternal_shouldGetUnauthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = restTemplate.exchange(
        url(ENDPOINT),
        HttpMethod.GET,
        new HttpEntity<>(createHeaders(AUTHORIZATION)),
        String.class,
        USER_ID,
        ID);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void doFilterInternal_shouldGetAuthorizedResponseUsingWrongEndpoint() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = restTemplate.exchange(
        url(WRONG_ENDPOINT) + "?countryCode=GB",
        HttpMethod.GET,
        new HttpEntity<>(createHeaders(AUTHORIZATION)),
        String.class,
        ID);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
  }

  @Test
  void doFilterInternal_shouldGetAuthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder()
            .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
            .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
            .userEmail(USER_EMAIL_FROM_TOKEN)
            .build());

    ResponseEntity<String> response = restTemplate.exchange(
        url(ENDPOINT),
        HttpMethod.GET,
        new HttpEntity<>(createHeaders(AUTHORIZATION)),
        String.class,
        USER_ID,
        ID);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NOT_FOUND);
    assertThat(response.getBody()).contains("No static resource innb/account/g5453g5/cards/9B1B3ED5/users");
  }

  private HttpHeaders createHeaders(String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(AUTHORIZATION_PARAM, authorization);
    return headers;
  }

  private String url(String path) {
    return "http://localhost:" + serverPort + path;
  }
}
