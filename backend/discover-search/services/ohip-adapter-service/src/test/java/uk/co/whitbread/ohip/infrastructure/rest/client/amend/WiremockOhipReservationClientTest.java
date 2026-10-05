package uk.co.whitbread.ohip.infrastructure.rest.client.amend;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import org.junit.jupiter.api.AfterAll;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Status;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.properties.AvailabilityOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.properties.ChangeLogOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremockOhipReservationClientTest {

  @Mock
  private ReservationOhipProperties reservationOhipProperties;
  @Mock
  private AvailabilityOhipProperties availabilityOhipProperties;
  @Mock
  private ChangeLogOhipProperties changeLogOhipProperties;
  private WireMockServer wm;
  private String path;
  private Profile profile;
  private WebClient webClient;
  private final ChangeReservation reservation = new ChangeReservation();
  private final Map<String, List<String>> map = Map.of();

  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
    webClient = WebClient.create(path);
  }

  @BeforeEach
  void setUp() {

    profile = new Profile();
    profile.setProfileDetails(new ProfileType());
    profile.setProfileIdList(List.of(new UniqueIDType()));

    wm.resetAll();

    wm.stubFor(get(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.stubFor(post(anyUrl())
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));

    when(reservationOhipProperties.getRateInfoEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/rateInfo");
    when(reservationOhipProperties.getDepositsEndpoint()).thenReturn(
        "/csh/v1/hotels/{hotelId}/depositFolio");
    when(reservationOhipProperties.getReservationEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations?Reservation=&externalReferenceIds=refId&limit=1&offset=1");
    when(reservationOhipProperties.getCancellationEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations");
    when(reservationOhipProperties.getCancellationPoliciesEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellationPolicies");
    when(reservationOhipProperties.getReservationIdEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/{reservationId}?Reservation=&ReservationPaymentMethods=&ReservationPolicies=");
    when(reservationOhipProperties.getCancellationPoliciesEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations");
    when(reservationOhipProperties.getRoutingInstructions()).thenReturn(
        "/csh/v1/hotels/{HotelId}/reservations/{ReservationId}/routingInstructions");
    when(reservationOhipProperties.getDepositFoliosEndpoint()).thenReturn(
        "/csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios");
    when(reservationOhipProperties.getExternalReservationEndpoint()).thenReturn(
        "/rsv/v1/reservations?"
            + "reservationIdList=1&externalReferenceIds=1&arrivalDate=1&cancelledOn=1&surname=a&offset=1&limit=1"
            + "&orderBy=ArrivalDate&sortOrder=ASC");
    when(reservationOhipProperties.getHotelConfig()).thenReturn("/ent/config/v1/hotels/{hotelId}");
    when(changeLogOhipProperties.getActivityLogEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/activityLog");
    when(reservationOhipProperties.getDeleteReservationEndpoint()).thenReturn(
        "/rsv/v1/reservations/{reservationId}");
    when(reservationOhipProperties.getProfilesEndpoint())
        .thenReturn(
            "/crm/v1/profiles/test?Profile=&Address=&Communication=&Correspondence=&FutureReservation=&HistoryReservation=");
  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipClientRateInfo_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    //Act
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getRateInfo("hotelId", "reservationId", "1", "1"));
  }

  @Test
  void testOhipClientDeposits_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getDepositsByReservationId("hotelId", "reservationId"));
  }

  @Test
  void testOhipClientReservations_ShouldReturnException() {
    final Set<String> set = Set.of("reservationId");
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.getReservations("hotelId", set).collectList();
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }

  @Test
  void testOhipClient_CancelShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    final CancelReservation res = new CancelReservation();
    var response = ohipClient.sendPostCancelReservationRequest("hotelId", "reservationId", res);
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientGetCancellationPolicies_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetCancellationPoliciesRequest("hotelId"));
  }

  @Test
  void testOhipClientGetReservationsByExternal_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetReservationsByExternalReferenceId("refId"));
  }

  @Test
  void testOhipClientExternalReferenceIds_ShouldReturnException() {
    final List<String> refs = List.of("refId");
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetReservationsByExternalReferenceIdsRequest("hotelId",
            refs, 1, 1));
  }

  @Test
  void testOhipClientPaymentMethods_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.getReservationPaymentMethods("hotelId", "resId");
    assertThrows(HotelReservationException.class, response::block);
  }

  @Test
  void testOhipClientExternalReferenceIdsNoReference_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetReservationsByExternalReferenceIdsRequest("hotelId",
            null, 1, 1));
  }

  @Test
  void testOhipClientDepositFolios_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var deposit = new DepositFolioCriteria();
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendDepositFoliosRequest("hotelId", "resId",
            deposit));
  }

  @Test
  void testOhipClientReservationWithPreferences_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getReservationWithPreferences("hotelId", "resId"));
  }

  @Test
  void testOhipClientGetReservation_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.getReservation("hotelId", "resId");
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientGetReservationAmounts_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.getReservationAmounts("hotelId", "resId");
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }


  @Test
  void testOhipClientHotelConfig_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getHotelConfig("hotelId"));
  }


  @Test
  void testOhipClientWithRoutingIns_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getReservationWithRoutingInstructions("hotelId", "resId"));
  }


  @Test
  void testOhipClientFoliosAciAmount_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.getFoliosAciAmount("hotelId", "resId"));
  }

  @Test
  void testOhipClientDelReservation_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.deleteReservationRequest("hotelId", "resId"));
  }

  @Test
  void testOhipClientChangeRes_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    ohipClient.sendPutReservationsGuestRequest("hotelId", "id", reservation);
    var response = ohipClient.sendChangeReservationRequest("hotelId", "resId", reservation);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    assertThrows(HotelReservationException.class,
        response::block);
  }

  @Test
  void testOhipClientChangeRes_ShouldRetryCall() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    var response = ohipClient.sendChangeReservationRequest("hotelId", "resId", reservation).block();

    // Act
    assertNotNull(response);
    assertInstanceOf(ChangeReservationDetails.class, response);

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId")));
  }

  @Test
  void testOhipClientChangeRes_ShouldReturnException_WhenRetriesExhausted() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    var response = ohipClient.sendChangeReservationRequest("hotelId", "resId", reservation);
    assertThrows(HotelReservationException.class,
        response::block);

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId")));
  }

  @Test
  void testOhipClientChangeRes_ShouldReturnException_WhenRetryNotNeeded() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Forbidden\"}")));

    var response = ohipClient.sendChangeReservationRequest("hotelId", "resId", reservation);
    assertThrows(HotelReservationException.class,
        response::block);
  }

  @Test
  void testOhipClientChangeRes_ShouldReturnException_WhenErrorTypeIsNull() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation="
        + "&externalReferenceIds=refId&limit=1&offset=1/resId"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Forbidden\"}")));

    var response = ohipClient.sendChangeReservationRequest("hotelId", "resId", reservation);
    assertThrows(HotelReservationException.class,
        response::block);
  }

  @Test
  void testOhipClientPostProfil_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendPostProfileRequest("hotelId", profile));
  }

  @Test
  void testOhipClientActivityLog_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(ChangeLogException.class,
        () -> ohipClient.getActivityLog("hotelId", "resId", 1, 1));
  }

  @Test
  void testOhipClientProfileIds_ShouldReturnException() {
    var ids = Set.of("id");
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetProfilesByProfileIds(ids));
  }

  @Test
  void testOhipClientUpdateProfile_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendUpdateProfileRequest("hotelId", profile));
  }

  @Test
  void testOhipClientUpdateProfile_ShouldRetryCall() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    var response = ohipClient.sendUpdateProfileRequest("hotelId", profile);

    // Act
    assertNotNull(response);
    assertInstanceOf(Status.class, response);

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/")));
  }

  @Test
  void testOhipClientUpdateProfile_ShouldReturnException_WhenRetriesExhausted() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendUpdateProfileRequest("hotelId", profile));

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/crm/v1/profiles/test%3FProfile=&Address=&Communication="
        + "&Correspondence=&FutureReservation=&HistoryReservation=/")));
  }

  @Test
  void testOhipClientdPutReservations_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.sendPutReservationsGuestRequest("hotelId", "id", reservation);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    assertThrows(HotelReservationException.class,
        response::block);
  }

  @Test
  void testOhipClientdPutReservations_ShouldRetryCall() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    var response = ohipClient.sendPutReservationsGuestRequest("hotelId", "id", reservation).block();

    // Act
    assertNotNull(response);
    assertInstanceOf(ChangeReservationDetails.class, response);

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id")));
  }

  @Test
  void testOhipClientdPutReservations_ShouldReturnException_WhenRetriesExhausted() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    var response = ohipClient.sendPutReservationsGuestRequest("hotelId", "id", reservation);
    assertThrows(HotelReservationException.class,
        response::block);

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations%3FReservation=&"
        + "externalReferenceIds=refId&limit=1&offset=1/id")));
  }

  @Test
  void testOhipClientRelatedFields_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    Map<String, List<String>> searchInputParams = new HashMap<>();
    searchInputParams.put("reservationIdList",List.of("1"));
    searchInputParams.put("externalReferenceIds",List.of("1"));
    searchInputParams.put("arrivalDate",List.of("1"));
    searchInputParams.put("cancelledOn",List.of("1"));
    searchInputParams.put("surname",List.of("a"));
    searchInputParams.put("offset",List.of("1"));
    searchInputParams.put("limit",List.of("1"));

    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetReservationsByReservationRelatedFields(searchInputParams));
  }


  @Test
  void testOhipClientBooker_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendBookerGetProfileSummaries(map));
  }


  @Test
  void testOhipClientPolicySchedules_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetPolicySchedulesRequest("hotelId", "code"));
  }


  @Test
  void testOhipClientCreateRes_ShouldReturnException() {
    final CreateReservation createRes = new CreateReservation();
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.sendCreateReservationRequest("hotelId", createRes);
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }


  @Test
  void testOhipClientdeleteRoutingIns_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    var response = ohipClient.deleteRoutingInstruction("hotelId", "resId", new LinkedMultiValueMap<>());
    assertThrows(HotelReservationException.class,
        () -> response.block());
  }

  @Test
  void testOhipClientDeleteCancellationPolicy_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.deleteCancellationPolicy("hotelId", "resId", "policy"));
  }

  @Test
  void testOhipClientSendGetReservationsByReservationId_ShouldReturnException() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetReservationsByReservationId("hotelId", "resId"));
  }

  @Test
  void testOhipClientSendGetGuestProfilesByProfileIds() {
    final OhipReservationClient ohipClient = new OhipReservationClient(webClient,
        reservationOhipProperties, availabilityOhipProperties, changeLogOhipProperties);
    Set<String> profiles = Set.of("test");
    assertThrows(HotelReservationException.class,
        () -> ohipClient.sendGetGuestProfilesByProfileIds(profiles));
  }
}
