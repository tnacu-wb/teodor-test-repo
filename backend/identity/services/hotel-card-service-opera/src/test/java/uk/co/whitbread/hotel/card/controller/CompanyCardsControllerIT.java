package uk.co.whitbread.hotel.card.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.hotel.card.helper.AuthTestHelper.configureAuth0Context;

import java.util.List;
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
import uk.co.whitbread.hotel.card.model.Address;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.service.CdhAuthorizationService;
import uk.co.whitbread.hotel.card.service.CdhCompanyCardsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import uk.co.whitbread.shared.auth.tenant.TenantRepository;
import uk.co.whitbread.shared.cdh.model.card.GetPaymentCardDetailsResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ExtendWith(SpringExtension.class)
class CompanyCardsControllerIT {

    private static final String COMPANY_ID = "24";
    private static final String EMPLOYEE_ID = "5";
    private static final String EMPLOYEE_EMAIL = "a@b.com";
    private static final String COMPANY_ID_PATH = "companyId";
    private static final String EMPLOYEE_ID_HEADER = "employee-id";
    private static final String CARD_ID_PATH = "card-id";
    private static final String CARD_ID = "1";

    private static final String ADD_CARD_ENDPOINT = "/companies/admin/{companyId}/cards";
    private static final String GET_CARDS_ENDPOINT = "/companies/{companyId}/cards";
    private static final String UPDATE_OR_DELETE_CARD_ENDPOINT = "/companies/admin/{companyId}/cards/{card-id}";

    private static final String AUTHORIZATION = "Bearer token";
    private static final String AUTHORIZATION_PARAM = "Authorization";

    @LocalServerPort
    private int serverPort;

    @MockitoBean
    private CdhAuthorizationService mockCdhAuthorizationService;

    @MockitoBean
    private CdhCompanyCardsService cdhCompanyCardsService;

    @MockitoBean
    CdhEmployeeDetails cdhEmployeeDetails;

    @MockitoBean
    TokenService tokenService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        configureAuth0Context(tenantRepository, jwtDecoder);
    }

    @Test
    void nonSuperUserIsNotAllowedToAddCard() {
        when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
            .thenReturn(false);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
            CdhEmployeeDetails.builder().companyAccountId(COMPANY_ID).employeeAccountId(EMPLOYEE_ID).build());

        ResponseEntity<String> response = exchange(HttpMethod.POST, ADD_CARD_ENDPOINT, getNewCard(),
            headers(AUTHORIZATION, EMPLOYEE_ID), companyPathVariables());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(response.getBody())
            .contains("\"status\":401")
            .contains("\"error\":\"Unauthorized\"")
            .contains("\"path\":\"/companies/admin/" + COMPANY_ID + "/cards\"");
    }

    @Test
    void superUserIsAllowedToAddCard() {
      when(mockCdhAuthorizationService.isSuperAccessLevelUser(any()))
          .thenReturn(true);
      when(mockCdhAuthorizationService.isSameEntity(any(), any()))
          .thenReturn(true);
      when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
          CdhEmployeeDetails.builder()
              .companyAccountId(COMPANY_ID)
              .employeeAccountId(EMPLOYEE_ID)
              .userEmail(EMPLOYEE_EMAIL)
              .build());
      when(cdhCompanyCardsService.addCdhPaymentCard(any(), any(), any())).thenReturn(
          GetPaymentCardDetailsResponse.builder()
              .cardId(CARD_ID)
              .build()
      );

      ResponseEntity<String> response = exchange(HttpMethod.POST, ADD_CARD_ENDPOINT, getNewCard(),
          headers(AUTHORIZATION, EMPLOYEE_ID), companyPathVariables());

      assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_CREATED);
    }

    @Test
    void nonSuperUserIsNotAllowedToDeleteCard() {
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
            CdhEmployeeDetails.builder().companyAccountId(COMPANY_ID).employeeAccountId(EMPLOYEE_ID).build());

        ResponseEntity<String> response = exchange(HttpMethod.DELETE, UPDATE_OR_DELETE_CARD_ENDPOINT, null,
            headers(AUTHORIZATION, EMPLOYEE_ID), companyCardPathVariables());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(response.getBody())
            .contains("\"status\":401")
            .contains("\"error\":\"Unauthorized\"")
            .contains("\"path\":\"/companies/admin/" + COMPANY_ID + "/cards/" + CARD_ID + "\"");
    }

    @Test
    void superUserIsAllowedToDeleteCard() {
      when(mockCdhAuthorizationService.isSuperAccessLevelUser(any()))
          .thenReturn(true);
      when(mockCdhAuthorizationService.isSameEntity(any(), any()))
          .thenReturn(true);
      when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
          CdhEmployeeDetails.builder()
              .companyAccountId(COMPANY_ID)
              .employeeAccountId(EMPLOYEE_ID)
              .userEmail(EMPLOYEE_EMAIL)
              .build());

      ResponseEntity<String> response = exchange(HttpMethod.DELETE, UPDATE_OR_DELETE_CARD_ENDPOINT, null,
          headers(AUTHORIZATION, EMPLOYEE_ID), companyCardPathVariables());

      assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void getCompanyPaymentCards() {
      when(mockCdhAuthorizationService.isSuperAccessLevelUser(cdhEmployeeDetails))
          .thenReturn(false);
      when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
          CdhEmployeeDetails.builder()
              .companyAccountId(COMPANY_ID)
              .employeeAccountId(EMPLOYEE_ID)
              .userEmail(EMPLOYEE_EMAIL)
              .build());
      when(cdhCompanyCardsService.getPaymentCards(anyString(), anyString())).thenReturn(
          List.of(getNewCard())
      );

      ResponseEntity<String> response = exchange(HttpMethod.GET, GET_CARDS_ENDPOINT, null,
          headers(AUTHORIZATION, null), companyPathVariables());

      assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_OK);
      assertThat(response.getBody()).contains(getNewCard().getCardHolderName());
    }

    @Test
    void nonSuperUserIsNotAllowedToRemoveCard() {
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
                CdhEmployeeDetails.builder().companyAccountId(COMPANY_ID).employeeAccountId(EMPLOYEE_ID).build());

        ResponseEntity<String> response = exchange(HttpMethod.POST, UPDATE_OR_DELETE_CARD_ENDPOINT, null,
            headers(AUTHORIZATION, EMPLOYEE_ID), companyCardPathVariables());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
        assertThat(response.getBody())
            .contains("\"status\":401")
            .contains("\"error\":\"Unauthorized\"")
            .contains("\"path\":\"/companies/admin/" + COMPANY_ID + "/cards/" + CARD_ID + "\"");
    }

    @Test
    void superUserIsAllowedToRemoveCard() {
        when(mockCdhAuthorizationService.isSuperAccessLevelUser(any()))
                .thenReturn(true);
        when(mockCdhAuthorizationService.isSameEntity(any(), any()))
                .thenReturn(true);
        when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
                CdhEmployeeDetails.builder()
                        .companyAccountId(COMPANY_ID)
                        .employeeAccountId(EMPLOYEE_ID)
                        .userEmail(EMPLOYEE_EMAIL)
                        .build());

        ResponseEntity<String> response = exchange(HttpMethod.POST, UPDATE_OR_DELETE_CARD_ENDPOINT, null,
            headers(AUTHORIZATION, EMPLOYEE_ID), companyCardPathVariables());

        assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void superUserIsAllowedToUpdateCard() {
      when(mockCdhAuthorizationService.isSuperAccessLevelUser(any()))
          .thenReturn(true);
      when(mockCdhAuthorizationService.isSameEntity(any(), any()))
          .thenReturn(true);
      when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
          CdhEmployeeDetails.builder()
              .companyAccountId(COMPANY_ID)
              .employeeAccountId(EMPLOYEE_ID)
              .userEmail(EMPLOYEE_EMAIL)
              .build());
      when(cdhCompanyCardsService.addCdhPaymentCard(any(), any(), any())).thenReturn(
          GetPaymentCardDetailsResponse.builder()
              .cardId(CARD_ID)
              .build()
      );

      ResponseEntity<String> response = exchange(HttpMethod.PUT, UPDATE_OR_DELETE_CARD_ENDPOINT, getNewCard(),
          headers(AUTHORIZATION, EMPLOYEE_ID), companyCardPathVariables());

      assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    @Test
    void userFromDifferentCompanyGetsForbiddenOnGetCards() {
      when(tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(anyString())).thenReturn(
          CdhEmployeeDetails.builder()
              .companyAccountId(COMPANY_ID + "1") // Different company ID
              .employeeAccountId(EMPLOYEE_ID)
              .userEmail(EMPLOYEE_EMAIL)
              .build()
      );

      ResponseEntity<String> response = exchange(HttpMethod.GET, GET_CARDS_ENDPOINT, null,
          headers(AUTHORIZATION, null), companyPathVariables());

      assertThat(response.getStatusCode().value()).isEqualTo(HttpStatus.SC_FORBIDDEN);
    }

    private ResponseEntity<String> exchange(HttpMethod method, String path, Object body,
        HttpHeaders headers, Map<String, ?> pathVariables) {
        return restTemplate.exchange(
            buildUrl(path, pathVariables),
            method,
            new HttpEntity<>(body, headers),
            String.class);
    }

    private HttpHeaders headers(String authorization, String employeeId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authorization != null) {
            headers.set(AUTHORIZATION_PARAM, authorization);
        }
        if (employeeId != null) {
            headers.set(EMPLOYEE_ID_HEADER, employeeId);
        }
        return headers;
    }

    private Map<String, String> companyPathVariables() {
        return Map.of(COMPANY_ID_PATH, COMPANY_ID);
    }

    private Map<String, String> companyCardPathVariables() {
        return Map.of(COMPANY_ID_PATH, COMPANY_ID, CARD_ID_PATH, CARD_ID);
    }

    private String buildUrl(String path, Map<String, ?> pathVariables) {
        return UriComponentsBuilder.fromUriString("http://localhost:" + serverPort)
            .path(path)
            .buildAndExpand(pathVariables)
            .toUriString();
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
