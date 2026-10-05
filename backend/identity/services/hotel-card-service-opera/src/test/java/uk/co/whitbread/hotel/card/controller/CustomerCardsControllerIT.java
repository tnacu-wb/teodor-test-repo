package uk.co.whitbread.hotel.card.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.helper.AuthTestHelper.configureAuth0Context;

import java.util.List;
import java.util.UUID;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import uk.co.whitbread.hotel.card.exceptions.ThreeCPClientException;
import uk.co.whitbread.hotel.card.helper.PaymentCardHelper;
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.AuthorizeScaRequest;
import uk.co.whitbread.hotel.card.model.CardType;
import uk.co.whitbread.hotel.card.model.PaymentCardBBCentral;
import uk.co.whitbread.hotel.card.model.PaymentCardBBPersonal;
import uk.co.whitbread.hotel.card.model.PaymentCardDTO;
import uk.co.whitbread.hotel.card.model.PaymentCardPIPersonal;
import uk.co.whitbread.hotel.card.model.SaveCardDetails;
import uk.co.whitbread.hotel.card.model.SaveCardPurpose;
import uk.co.whitbread.hotel.card.model.SaveCardRequest;
import uk.co.whitbread.hotel.card.service.CustomerCardsService;
import uk.co.whitbread.shared.auth.exception.TokenVerificationException;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class CustomerCardsControllerIT {

  @MockitoBean
  private CustomerCardsService mockCustomerCardsService;

  @MockitoBean
  private TokenService tokenService;

  @MockitoBean
  private JwtDecoder jwtDecoder;

  @LocalServerPort
  private int serverPort;

  @Autowired
  private TenantRepository tenantRepository;
  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  public void setUp() {
    configureAuth0Context(tenantRepository, jwtDecoder);
  }

  @Test
  void badRequest() {
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/session", "{}",
        headers("Bearer token"));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
  }

  @Test
  void unauthorized() {
    when(mockCustomerCardsService.initiateSave(any(), any()))
        .thenThrow(new TokenVerificationException("Provided token was invalid or expired"));
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/session",
        newSaveCardDetails(), headers("Bearer token"));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
  }

  @Test
  void successfulInitialization() {
    var payload = newSaveCardDetails();

    when(mockCustomerCardsService.initiateSave(eq(payload), any()))
        .thenReturn(null);
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/session", payload,
        headers("Bearer token"));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void throws3CPException() {
    var payload = newSaveCardDetails();

    when(mockCustomerCardsService.initiateSave(eq(payload), any()))
        .thenThrow(new ThreeCPClientException("There is no 3CP response"));
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/session", payload,
        headers("Bearer token"));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  void createOrUpdatePaymentCard_PIPersonalCardSuccess() {
    PaymentCardDTO paymentCard = PaymentCardHelper.buildPIPaymentCard();
    PaymentCardPIPersonal card = new PaymentCardPIPersonal();

    when(mockCustomerCardsService.mapPaymentCard(SaveCardPurpose.PI_PERSONAL, paymentCard)).thenReturn(card);
    doNothing().when(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.PI_PERSONAL, card);

    ResponseEntity<String> response = exchange(HttpMethod.PUT, "/v2/customers/cards", paymentCard,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);

    verify(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.PI_PERSONAL, card);
  }

  @Test
  void createOrUpdatePaymentCard_BBPersonalCardSuccess() {
    PaymentCardDTO paymentCard = PaymentCardHelper.buildBBPaymentCardPersonal();
    PaymentCardBBPersonal card = new PaymentCardBBPersonal();

    when(mockCustomerCardsService.mapPaymentCard(SaveCardPurpose.BB_PERSONAL, paymentCard)).thenReturn(card);
    doNothing().when(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.BB_PERSONAL, card);

    ResponseEntity<String> response = exchange(HttpMethod.PUT, "/v2/customers/cards", paymentCard,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);

    verify(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.BB_PERSONAL, card);
  }

  @Test
  void createOrUpdatePaymentCard_BBCentralCardSuccess() {
    PaymentCardDTO paymentCard = PaymentCardHelper.buildBBPaymentCardCentral();
    PaymentCardBBCentral card = new PaymentCardBBCentral();

    when(mockCustomerCardsService.mapPaymentCard(SaveCardPurpose.BB_CENTRAL, paymentCard)).thenReturn(card);
    doNothing().when(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.BB_CENTRAL, card);

    ResponseEntity<String> response = exchange(HttpMethod.PUT, "/v2/customers/cards", paymentCard,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);

    verify(mockCustomerCardsService).updatePaymentCard(SaveCardPurpose.BB_CENTRAL, card);
  }

  @Test
  void shouldReturnA400BadRequestWhenRequiredFieldsAreMissing() {
    PaymentCardDTO paymentCard = new PaymentCardDTO();

    ResponseEntity<String> response = exchange(HttpMethod.PUT, "/v2/customers/cards", paymentCard,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
    assertThat(response.getBody()).contains(
        "cardType must not be empty",
        "expiryDate must not be empty",
        "business must not be null",
        "cardToken must not be empty",
        "personalCard must not be null",
        "cardNumberLast4Digits must not be empty",
        "userEmail must not be empty",
        "cardHolderName must not be empty");
  }

  @Test
  void successfulAuthorizeScaInitialization() {
    var payload = mockAuthorizeScaRequest();

    when(mockCustomerCardsService.initiateAuthorizeSca(eq(payload)))
            .thenReturn(null);
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/sca", payload,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
  }

  @Test
  void authorizeScathrows3CPException() {
    var payload = mockAuthorizeScaRequest();

    when(mockCustomerCardsService.initiateAuthorizeSca(eq(payload)))
            .thenThrow(new ThreeCPClientException("There is no 3CP response"));
    ResponseEntity<String> response = exchange(HttpMethod.POST, "/v2/customers/cards/sca", payload,
        headers(null));

    assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<String> exchange(HttpMethod method, String path, Object body,
      HttpHeaders headers) {
    return restTemplate.exchange(
        "http://localhost:" + serverPort + path,
        method,
        new HttpEntity<>(body, headers),
        String.class);
  }

  private HttpHeaders headers(String authorization) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    if (authorization != null) {
      headers.set("Authorization", authorization);
    }
    return headers;
  }

  private SaveCardRequest newSaveCardDetails() {
    Address billingAddress = new Address();
    billingAddress.setCountryCode("GB");
    billingAddress.setLine1("123 Main St");
    billingAddress.setPostCode("E1 6AN");
    billingAddress.setType("BUSINESS");

    return SaveCardRequest.builder()
        .requestId("requestId")
        .environment("http://localhost")
        .language("en")
        .cardDetails(SaveCardDetails.builder()
            .cardType(CardType.CARD)
            .cardId("cardId")
            .cardLabel("cardLabel")
            .memorableWord("memorable")
            .business(true)
            .personalCard(false)
            .cnpRequired(false)
            .build())
        .billingAddress(billingAddress)
        .build();
  }

  private AuthorizeScaRequest mockAuthorizeScaRequest() {
    return AuthorizeScaRequest.builder()
            .environment("http://localhost").language("en").requestId(UUID.randomUUID().toString())
            .build();
  }
}
