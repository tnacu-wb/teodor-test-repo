package uk.co.whitbread.reservation.infrastructure.rest.client.basket;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.Body;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.*;
import uk.co.whitbread.reservation.domain.exceptions.BasketNotFoundException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.exceptions.BasketInternalException;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.BasketClient;
import uk.co.whitbread.reservation.infrastructure.rest.client.basket.service.properties.BasketProperties;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.cloud.contract.wiremock.WireMockSpring.options;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class WiremockBasketClient4xxTest {
  @Mock
  private BasketProperties basketProperties;
  private WireMockServer wm;
  private final String path = "http://localhost:8080";
  private final WebClient webClient = WebClient.create(path);
  private Body body = Body.fromJsonBytes(
          "{\"message\":\"message\", \"debugMessage\":\"message\", \"clause\":\"message\", \"errorCode\":900}".getBytes());

  @BeforeEach
  void setUp() {
    wm = new WireMockServer(options().port(8080));

    wm.stubFor(get(anyUrl())
            .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(404)
                    .withResponseBody(body)));

    wm.stubFor(post(anyUrl())
            .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(404)
                    .withResponseBody(body)));

    wm.stubFor(delete(anyUrl())
            .willReturn(aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withStatus(404)
                    .withResponseBody(body)));

    wm.stubFor(put(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(404)
            .withResponseBody(body)));

    wm.start();
  }

  @AfterEach
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testBasketClientByReference_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketByReferenceEndpoint()).thenReturn(
            "/v1/baskets/");

    //Act
    ResponseEntity<BasketDto> responseEntity = basketClient.sendGetBasketByReference("bookingRef");

    assertNotNull(responseEntity.getBody());
    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
  }

  @Test
  void testBasketClientRemove_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getRemoveItemEndpoint()).thenReturn(
            "/v1/baskets/{basket-reference}/items/{itemId}");
    //Act
    assertThrows(BasketNotFoundException.class,
            () -> basketClient.removeItem("basketRef", "basketItem",
                    "etag"));
  }

  @Test
  void testBasketClient_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketEndpoint()).thenReturn(
            "/v1/baskets/{basket-reference}");
    //Act
    assertThrows(BasketNotFoundException.class,
            () -> basketClient.sendGetBasket("basketRef"));
  }

  @Test
  void testBasketClientCharges_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getChargesByReservationIdEndpoint()).thenReturn(
            "/v1/baskets/deposit-folios/{reservationId}");
    //Act
    PrepaidDepositsDto response = basketClient.getCharges("resId");

    assertNull(response);
  }

  @Test
  void testBasketClientChargesByReservationIds_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final List<String> ids = List.of("resId");
    when(basketProperties.getChargesByReservationIdsEndpoint()).thenReturn(
        "/v1/baskets/deposit-folios?reservationId=resId");
    //Act
    PrepaidDepositsDto response = basketClient.getChargesByReservationIds(ids);

    assertNull(response);
  }

  @Test
  void testBasketClientDelete_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    when(basketProperties.getBasketEndpoint()).thenReturn(
            "/v1/baskets/{basket-reference}");
    //Act
    assertThrows(BasketNotFoundException.class,
            () -> basketClient.deleteBasket("basketRef", "etag"));
  }

  @Test
  void testBasketClientEmailConfirmation_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final EmailRequestDto emailRequestDto = new EmailRequestDto();
    when(basketProperties.getEmailConfirmationEndpoint()).thenReturn(
            "v1/baskets/email");
    //Act
    assertThrows(BasketNotFoundException.class,
            () -> basketClient.triggerEmailConfirmation(emailRequestDto));
  }
  @Test
  void testBasketChangeIdContext_ShouldReturnException() {
    final BasketClient basketClient = new BasketClient(webClient, basketProperties);
    final ChangeBasketIdContextDto req = new ChangeBasketIdContextDto();
    when(basketProperties.getChangeIdContextEndpoint()).thenReturn(
        "v1/baskets/changeIdContext");
    //Act
    assertThrows(BasketInternalException.class,
        () -> basketClient.changeIdContext("ref", req));
  }



}
