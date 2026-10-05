package uk.co.whitbread.hotel.card.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.helper.AuthTestHelper.configureAuth0Context;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CdhEmployeeFilterTest {

  private static final String COMPANY_ID = "24";
  private static final String ADD_CARD_ENDPOINT = "/companies/admin/{companyId}/cards";

  private static final String AUTHORIZATION = "Bearer token";
  private static final String AUTHORIZATION_PARAM = "Authorization";

  @LocalServerPort
  private int serverPort;

  @MockitoBean
  private TokenService mockTokenService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @Autowired
  private TenantRepository tenantRepository;

  private final TestRestTemplate restTemplate = new TestRestTemplate();

  @BeforeEach
  public void setUp() {
    configureAuth0Context(tenantRepository, jwtDecoder);
  }

  @Test
  public void missingTokenHeader_shouldGetUnauthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(null))
        .thenReturn(CdhEmployeeDetails.builder().build());
    ResponseEntity<String> response = restTemplate.exchange(
        url(ADD_CARD_ENDPOINT),
        HttpMethod.POST,
        new HttpEntity<>(getNewCard(), createHeaders(null)),
        String.class,
        COMPANY_ID);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  public void missingCdhIdsFromToken_shouldGetUnauthorizedResponse() {
    when(mockTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(AUTHORIZATION))
        .thenReturn(CdhEmployeeDetails.builder().build());

    ResponseEntity<String> response = restTemplate.exchange(
        url(ADD_CARD_ENDPOINT),
        HttpMethod.POST,
        new HttpEntity<>(getNewCard(), createHeaders(AUTHORIZATION)),
        String.class,
        COMPANY_ID);

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  private HttpHeaders createHeaders(String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    if (authorization != null) {
      headers.set(AUTHORIZATION_PARAM, authorization);
    }
    return headers;
  }

  private String url(String path) {
    return "http://localhost:" + serverPort + path;
  }

  private PaymentCard getNewCard() {
    PaymentCard firstCard = new PaymentCard();
    firstCard.setCardId("3");
    firstCard.setCardLabel("Spare card");
    firstCard.setCardType("AT");
    firstCard.setCardNumber("************3333");
    firstCard.setStartDate("");
    firstCard.setExpiryDate("0422");
    firstCard.setCardHolderName("Homer Simpson");
    Address billingAddress = new Address();
    billingAddress.setLine1("120 Holborn");
    billingAddress.setLine2("");
    billingAddress.setLine3("");
    billingAddress.setLine4("LONDON");
    billingAddress.setLine5("");
    billingAddress.setPostCode("EC1N 2TD");
    billingAddress.setCountryCode("GB");
    firstCard.setBillingAddress(billingAddress);
    firstCard.setCnpRequired(false);

    return firstCard;
  }
}
