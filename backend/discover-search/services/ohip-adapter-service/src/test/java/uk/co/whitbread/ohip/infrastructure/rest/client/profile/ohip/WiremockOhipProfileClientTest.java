package uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.profile.in.*;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.exception.ProfileException;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.properties.ProfileOhipProperties;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.http.Fault.EMPTY_RESPONSE;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WiremockOhipProfileClientTest {

  @Mock
  private ProfileOhipProperties profileOhipProperties;
  private WireMockServer wm;
  private String path;
  private WebClient webClient;

  @BeforeAll
  void startWiremock() {
    wm = new WireMockServer(options().dynamicPort());
    wm.start();
    path = wm.baseUrl();
    webClient = WebClient.create(path);
  }

  @BeforeEach
  void setUp() {
    wm.resetAll();

    wm.stubFor(get(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.TEXT_PLAIN_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.stubFor(post(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));

    wm.stubFor(put(path)
        .willReturn(aResponse()
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withStatus(500)
            .withBody("exception")));

  }

  @AfterAll
  void cleanUp() {
    wm.stop();
  }

  @Test
  void testOhipCompanies_shouldReturnException() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getCompaniesEndpoint()).thenReturn(
        "/crm/v1/companies/{corporateId}");

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.getCompanyByCorporateId("TestGlobalCompanyId"));

  }

  @Test
  void testOhipProfile_shouldReturnException() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    final Profile profile = mockProfile();
    when(profileOhipProperties.getProfilesEndpoint()).thenReturn(
        "/crm/v1/profiles");

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.createProfile(profile, "hotelId"));

  }

  @Test
  void testOhipAddProfile_shouldReturnException() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    final AddProfileRequest request = mockAddProfileRequest();
    when(profileOhipProperties.getAddProfileEndpoint()).thenReturn(
        "/rsv/v1/hotels/{HotelId}/reservations/{ReservationId}");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.addProfile(request, "hotelId"));
  }

  @Test
  void testOhipAddProfile_shouldRetryCall() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    final AddProfileRequest request = mockAddProfileRequest();
    when(profileOhipProperties.getAddProfileEndpoint()).thenReturn(
        "/rsv/v1/hotels/{HotelId}/reservations/{ReservationId}");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    ohipProfileClient.addProfile(request, "hotelId");

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345")));
  }

  @Test
  void testOhipAddProfile_shouldReturnException_WhenRetriesExhausted() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    final AddProfileRequest request = mockAddProfileRequest();
    when(profileOhipProperties.getAddProfileEndpoint()).thenReturn(
        "/rsv/v1/hotels/{HotelId}/reservations/{ReservationId}");

    wm.stubFor(put(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.addProfile(request, "hotelId"));

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/rsv/v1/hotels/hotelId/reservations/12345")));
  }

  @Test
  void testOhipReservation_shouldReturnException() {
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getReservationGuestEndpoint()).thenReturn(
        "/rsv/v1/hotels/{hotelId}/reservations/{reservationId}");

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.getProfileIdByReservation("reservationId", "hotelId"));
  }

  @Test
  void testOhipUpdateProfile_shouldReturnException() {
    final Profile profile = mockProfile();
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getProfilesEndpoint()).thenReturn(
        "/crm/v1/profiles");

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.updateProfile(profile, "1"));
  }

  @Test
  void testOhipUpdateProfile_shouldRetryCall() {
    final Profile profile = mockProfile();
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getProfilesEndpoint()).thenReturn(
        "/crm/v1/profiles");

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}"))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    ohipProfileClient.updateProfile(profile, "1");

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/crm/v1/profiles/1")));
  }

  @Test
  void testOhipUpdateProfile_shouldReturnException_WhenRetriesExhausted() {
    final Profile profile = mockProfile();
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getProfilesEndpoint()).thenReturn(
        "/crm/v1/profiles");

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .willReturn(aResponse()
            .withStatus(400)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"type\": \"Bad Request\"}")));

    //Act
    assertThrows(ProfileException.class,
        () -> ohipProfileClient.updateProfile(profile, "1"));

    // Verify retry logic
    wm.verify(4, putRequestedFor(urlEqualTo("/crm/v1/profiles/1")));
  }

  @Test
  void testOhipUpdateProfile_shouldRetryOnPrematureCloseException() {
    final Profile profile = mockProfile();
    final OhipProfileClient ohipProfileClient = new OhipProfileClient(webClient,
        profileOhipProperties);
    when(profileOhipProperties.getProfilesEndpoint()).thenReturn(
        "/crm/v1/profiles");

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs(Scenario.STARTED)
        .willReturn(aResponse().withFault(EMPTY_RESPONSE))
        .willSetStateTo("Second Call"));

    wm.stubFor(put(urlEqualTo("/crm/v1/profiles/1"))
        .inScenario("Retry Scenario")
        .whenScenarioStateIs("Second Call")
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .withBody("{\"status\": \"Success\"}")));

    ohipProfileClient.updateProfile(profile, "1");

    // Verify retry logic
    wm.verify(2, putRequestedFor(urlEqualTo("/crm/v1/profiles/1")));
  }

  private Profile mockProfile() {
    var profile = new Profile();
    profile.setProfileIdList(new ArrayList<>());
    var idType = new UniqueIDType();
    idType.setId("1");
    idType.setType("type");
    profile.getProfileIdList().add(idType);
    return profile;
  }

  private AddProfileRequest mockAddProfileRequest() {
    return AddProfileRequest.builder()
        .reservations(List.of(ProfileReservations.builder().reservationIdList(List.of(
            ProfileReservationIdList.builder().id("12345").build())).reservationGuests(List.of(
            ProfileReservationGuests.builder().profileInfo(
                KioskProfileInfo.builder().profileIdList(List.of(ProfileIdList.builder().id("112")
                    .build())).build()).build())).build())).build();
  }
}
