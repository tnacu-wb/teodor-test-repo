package uk.co.whitbread.hotel.card.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.helper.AuthTestHelper.configureAuth0Context;

import java.util.Map;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
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
import org.springframework.web.util.UriComponentsBuilder;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.service.CdhHotelCardsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ExtendWith(SpringExtension.class)
public class CdhHotelCardsControllerIT {

  @MockitoBean
  private CdhHotelCardsService mockCdhHotelCardsService;
  @MockitoBean
  private TokenService mockTokenService;
  @MockitoBean
  private JwtDecoder jwtDecoder;

  @LocalServerPort
  int serverPort;
  @Autowired
  private TenantRepository tenantRepository;
  @Autowired
  private TestRestTemplate restTemplate;

  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";
  private static final String COMPANY_ACCOUNT_ID_FROM_TOKEN = "companyAccountId";
  private static final String EMPLOYEE_ACCOUNT_ID_FROM_TOKEN = "employeeAccountId";
  private static final String CUSTOMER_ID_PARAM = "customer-id";
  private static final String BUSINESS_PARAM = "business";
  private static final String CARD_ID = "123";
  private static final String USER_EMAIL_FROM_TOKEN = "a@b.c";
  private static final String ADD_CARD_ENDPOINT = "/customers/hotels/{customer-id}/cards";
  private static final String UPDATE_AND_DELETE_CARD_ENDPOINT = "/customers/hotels/{customer-id}/cards/{card-id}";
  private static final String CARD_BY_CARD_ID_ENDPOINT = "/customers/hotels/{customer-id}/cards/{card-id}";

  @BeforeEach
  public void setUp() {
    configureAuth0Context(tenantRepository, jwtDecoder);
  }

  @Test
  void getPaymentCard_ShouldReturnPaymentCard() {
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    ResponseEntity<String> response = exchange(HttpMethod.GET, CARD_BY_CARD_ID_ENDPOINT, null,
        headers(AUTHORIZATION), pathVariablesWithCardId(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);

    verify(mockCdhHotelCardsService).getPaymentCard(cdhEmployeeDetails, true);
  }

  @Test
  void addPaymentCard_ShouldReturnCREATED() {
    PaymentCard paymentCard = buildPaymentCard();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN).userEmail(USER_EMAIL_FROM_TOKEN).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    ResponseEntity<String> response = exchange(HttpMethod.POST, ADD_CARD_ENDPOINT, paymentCard,
        headers(AUTHORIZATION), pathVariables(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_CREATED);

    verify(mockCdhHotelCardsService).addOrUpdatePaymentCard(paymentCard, cdhEmployeeDetails, true);
  }

  @Test
  void addPaymentCard_hasNoAuthorization_shouldReturnUNAUTHORIZED() {

    PaymentCard paymentCard = buildPaymentCard();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = exchange(HttpMethod.POST, ADD_CARD_ENDPOINT, paymentCard,
        headers(null), pathVariables(), null);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);

    verifyNoInteractions(mockCdhHotelCardsService);
  }


  @Test
  void updatePaymentCard_ShouldReturnUPDATED() {
    PaymentCard paymentCard = buildPaymentCard();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    ResponseEntity<String> response = exchange(HttpMethod.PUT, UPDATE_AND_DELETE_CARD_ENDPOINT, paymentCard,
        headers(AUTHORIZATION), pathVariablesWithCardId(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);

    verify(mockCdhHotelCardsService).addOrUpdatePaymentCard(paymentCard, cdhEmployeeDetails, true);
  }

  @Test
  void updatePaymentCard_hasNoAuthorization_shouldReturnUNAUTHORIZED() {

    PaymentCard paymentCard = buildPaymentCard();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = exchange(HttpMethod.PUT, UPDATE_AND_DELETE_CARD_ENDPOINT, paymentCard,
        headers(null), pathVariablesWithCardId(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);

    verifyNoInteractions(mockCdhHotelCardsService);
  }

  @Test
  void deletePaymentCard_ShouldReturnDeleted() {
    PaymentCard paymentCard = buildPaymentCard();
    CdhEmployeeDetails cdhEmployeeDetails = CdhEmployeeDetails.builder()
        .companyAccountId(COMPANY_ACCOUNT_ID_FROM_TOKEN)
        .employeeAccountId(EMPLOYEE_ACCOUNT_ID_FROM_TOKEN)
        .userEmail(USER_EMAIL_FROM_TOKEN).build();

    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(cdhEmployeeDetails);

    ResponseEntity<String> response = exchange(HttpMethod.DELETE, UPDATE_AND_DELETE_CARD_ENDPOINT, paymentCard,
        headers(AUTHORIZATION), pathVariablesWithCardId(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);

    verify(mockCdhHotelCardsService).deletePaymentCard(cdhEmployeeDetails);
  }

  @Test
  void deletePaymentCard_hasNoAuthorization_shouldReturnUNAUTHORIZED() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = exchange(HttpMethod.PUT, UPDATE_AND_DELETE_CARD_ENDPOINT,
        buildPaymentCard(), headers(null), pathVariablesWithCardId(), true);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);

    verifyNoInteractions(mockCdhHotelCardsService);
  }

  private ResponseEntity<String> exchange(HttpMethod method, String path, Object body,
      HttpHeaders headers, Map<String, ?> pathVariables, Boolean business) {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromUriString("http://localhost:" + serverPort)
        .path(path);
    if (business != null) {
      builder.queryParam(BUSINESS_PARAM, business);
    }
    return restTemplate.exchange(
        builder.buildAndExpand(pathVariables).toUriString(),
        method,
        new HttpEntity<>(body, headers),
        String.class);
  }

  private HttpHeaders headers(String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (authorization != null) {
      headers.set(AUTHORIZATION_PARAM, authorization);
    }
    return headers;
  }

  private Map<String, String> pathVariables() {
    return Map.of(CUSTOMER_ID_PARAM, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN);
  }

  private Map<String, String> pathVariablesWithCardId() {
    return Map.of(CUSTOMER_ID_PARAM, EMPLOYEE_ACCOUNT_ID_FROM_TOKEN, "card-id", CARD_ID);
  }

  private PaymentCard buildPaymentCard() {
    PaymentCard paymentCard = new PaymentCard();
    paymentCard.setCardLabel("Card 1");
    return paymentCard;
  }

}
