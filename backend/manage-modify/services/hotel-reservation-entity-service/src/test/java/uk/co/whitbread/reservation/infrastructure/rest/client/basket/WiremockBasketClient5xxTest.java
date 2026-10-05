package uk.co.whitbread.reservation.infrastructure.rest.client.basket;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.*;
import uk.co.whitbread.reservation.domain.exceptions.InvalidTokenException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketDigitalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockBasketClient5xxTest {

  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  @Mock
  private AuthenticatedUserService authenticatedUserService;

  @Mock
  private BasketProperties basketProperties;
  private WireMockServer wm;
  private Body body = Body.fromJsonBytes(
      "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(post(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(put(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(delete(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withResponseBody(body)));

    wm.stubFor(get(urlMatching("^.*Test.*$"))
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(200)
            .withResponseBody(Body.none())));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testBasketClientAdd_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final AddBasketItemRequestDto addBasketItemRequestDto = new AddBasketItemRequestDto();
    when(basketProperties.getAddItemEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}/items");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.addBasketItems("basketRef", "etag",
            addBasketItemRequestDto));
  }

  @Test
  void testBasketClientByReference_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketByReferenceEndpoint()).thenReturn(
        "/v1/baskets/");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendGetBasketByReference("bookingRef"));
  }

  @Test
  void testBasketClientCreate_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final CreateBasketRequestDto createBasketRequestDto = new CreateBasketRequestDto();
    when(basketProperties.getCreateEndpoint()).thenReturn(
        "/v1/baskets/");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.createBasket(createBasketRequestDto));
  }

  @Test
  void testBasketClientLinkAmendReservations_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final AddAmendedReservationsRequestDto amendedReservationsRequestDto = new AddAmendedReservationsRequestDto();
    when(basketProperties.getLinkAmendReservations()).thenReturn(
        "/v1/baskets/{basket-reference}/linkAmendReservations");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.linkAmendReservationsInBasket("basketRef", "etag",
            amendedReservationsRequestDto));
  }

  @Test
  void testBasketClientRefund_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final RefundRequestDto refundRequestDto = new RefundRequestDto();
    when(basketProperties.getRefundEndpoint()).thenReturn(
        "/v1/baskets/{basketReference}/refund");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.triggerRefund("basketRef", refundRequestDto));
  }

  @Test
  void testBasketClientRemove_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getRemoveItemEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}/items/{itemId}");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.removeItem("basketRef", "basketItem",
            "etag"));
  }

  @Test
  void testBasketClient_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendGetBasket("basketRef"));
  }

  @Test
  void testGetBasketFromBody_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}");
    //Act
   var ex = assertThrows(BasketDigitalException.class,
        () -> basketClient.sendGetBasket("Test"));
    assertEquals(114, ex.getErrorCode());
    assertEquals("Error while trying to extract basket from body", ex.getMessage());
  }

  @Test
  void testBasketClientCancel_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final CancelBasketDto cancelBasketDto = new CancelBasketDto();
    when(basketProperties.getCancelEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}/cancel");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.sendCancelBasket("basketRef", cancelBasketDto));
  }

  @Test
  void testBasketClientErroredBooking_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final ErroredBookingDto erroredBookingDto = new ErroredBookingDto();
    when(basketProperties.getErroredBookingEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}/erroredBooking");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.setErroredBooking("basketRef", erroredBookingDto));
  }

  @Test
  void testBasketClientSaveCharges_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final PrepaidDepositsRequestDto prepaidDepositsRequestDto = new PrepaidDepositsRequestDto();
    when(basketProperties.getSaveChargesEndpoint()).thenReturn(
        "/v1/baskets/deposit-folios");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.saveCharges(prepaidDepositsRequestDto));
  }

  @Test
  void testBasketClientCharges_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getChargesByReservationIdEndpoint()).thenReturn(
        "/v1/baskets/deposit-folios/{reservationId}");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.getCharges("resId"));
  }

  @Test
  void testBasketClientChargesByReservationIds_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final List<String> ids = List.of("resId");
    when(basketProperties.getChargesByReservationIdsEndpoint()).thenReturn(
        "/v1/baskets/deposit-folios?reservationId=resId");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.getChargesByReservationIds(ids));
  }

  @Test
  void testBasketClientDelete_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketEndpoint()).thenReturn(
        "/v1/baskets/{basket-reference}");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.deleteBasket("basketRef", "etag"));
  }

  @Test
  void testBasketClientEmailConfirmation_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final EmailRequestDto emailRequestDto = new EmailRequestDto();
    when(basketProperties.getEmailConfirmationEndpoint()).thenReturn(
        "v1/baskets/email");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.triggerEmailConfirmation(emailRequestDto));
  }

  @Test
  void testBasketClientInitiatePayment_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
    when(basketProperties.getInitiatePaymentEndpoint()).thenReturn(
        "/v1/baskets/{basketReference}/pay");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.initiatePayment("basketRef", paymentRequestDto));
  }

  @Test
  void testBasketClientProcessAmend_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final ProcessAmendRequestDto processAmendRequestDto = new ProcessAmendRequestDto();
    when(basketProperties.getProcessAmendEndpoint()).thenReturn(
        "/v1/baskets/{basketReference}/process-amend");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.processAmend("basketRef", processAmendRequestDto));
  }

  @Test
  void testBasketClientCcuiPaymentInvalidToken_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final CcuiPaymentRequestDto ccuiPaymentRequestDto = new CcuiPaymentRequestDto();
    when(basketProperties.getInitiateCcuiPaymentProcess()).thenReturn(
        "/v1/baskets/ccui/{basketReference}/pay");
    //Act
    assertThrows(InvalidTokenException.class,
        () -> basketClient.initiateCcuiPayment("basketRef", ccuiPaymentRequestDto));
  }

  @Test
  void testBasketClientCcuiPayment_ShouldReturnException() {

    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final CcuiPaymentRequestDto ccuiPaymentRequestDto = new CcuiPaymentRequestDto();

    Authentication authentication = mock(Authentication.class);
    var customJwt = mock(CustomJwtAuthenticationToken.class);
    var securityContext = mock(SecurityContext.class);
    var mockedToken = mock(CustomJwtAuthenticationToken.class);
    var mockedJwt = mock(Jwt.class);
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(customJwt);
    when(customJwt.getToken()).thenReturn(mockedJwt);
    when(mockedJwt.getTokenValue()).thenReturn("test");
    when(mockedToken.getToken()).thenReturn(mockedJwt);
    when(basketProperties.getInitiateCcuiPaymentProcess()).thenReturn(
        "/v1/baskets/ccui/{basketReference}/pay");
    when(mockedJwt.getClaimAsStringList("permissions")).thenReturn(List.of("test"));
    when(mockedJwt.getClaimAsString("scope")).thenReturn("test");
    when(authenticatedUserService.getAuthenticatedUser())
        .thenReturn(mockedToken);
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.initiateCcuiPayment("basketRef", ccuiPaymentRequestDto));
  }


}
