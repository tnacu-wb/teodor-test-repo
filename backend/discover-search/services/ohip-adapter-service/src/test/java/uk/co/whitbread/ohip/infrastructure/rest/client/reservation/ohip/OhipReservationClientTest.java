package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip;


import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLog;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLogListType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ActivityLogType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PostedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RetrievedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Status;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.CancellationPolicyDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicySchedulesDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.med.FileToUpload;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.changelog.exception.ChangeLogException;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.FolioWindowsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.FoliosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.ReservationFolioInformationDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.ReservationStatusOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipReservationClientTest {

  @InjectMocks
  OhipReservationClient ohipReservationClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void sendCreateReservationRequest__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationStatusOhipDto.class)).thenReturn(
        mockReservationResponse());

    //Act
    ReservationStatusOhipDto resStatus =
        ohipReservationClient.sendCreateReservationRequest("TestHotelId", new CreateReservation())
            .block();

    //Assert
    assertThat(resStatus, notNullValue());
  }

  @Test
  void getReservationPaymentMethods__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    Mono<Reservation> reservation = Mono.just(new Reservation());
    when(responseSpec.bodyToMono(Reservation.class)).thenReturn(reservation);

    //Act
    var resResponse =
        ohipReservationClient.getReservationPaymentMethods("TestHotelId", "reservationId");

    //Assert
    assertThat(resResponse.block(), notNullValue());
  }

  @Test
  void sendGetReservationsByExternalReferenceIdRequest__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsDetailsResponse());

    //Act
    ReservationsDetails foundReservations =
        ohipReservationClient.sendGetReservationsByExternalReferenceIdsRequest("TestHotelId",
            Collections.singletonList("123"), 20, 0);

    //Assert
    assertThat(foundReservations, notNullValue());

  }


  @Test
  void sendGetReservationsByReservationIdRequest__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Reservation.class)).thenReturn(
        mockGetReservationsDetailsIdResponse());

    //Act
    Reservation foundReservations =
        ohipReservationClient.sendGetReservationsByReservationId("TestHotelId", "12345");

    //Assert
    assertThat(foundReservations, notNullValue());

  }

  @Test
  void sendGetReservationsByReservationIdRequest__error_4xx() {
    // Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get reservations by reservationId=1234",
        debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetReservationsByExternalReferenceId__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsDetailsResponse());

    //Act
    ReservationsDetails foundReservations =
        ohipReservationClient.sendGetReservationsByExternalReferenceId("123");

    //Assert
    assertThat(foundReservations, notNullValue());

  }

  @Test
  void sendGetReservationAmounts__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PriceBreakdownDto.class)).thenReturn(mockPriceBreakdownDto());

    //Act
    var prices = ohipReservationClient.getReservationAmounts("TKINPT", "123").block();

    //Assert
    assertThat(prices.getT2(), notNullValue());

  }

  @Test
  void sendGetBookerProfileRequest__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Profile.class)).thenReturn(mockProfile());

    //Act
    Profile profile = ohipReservationClient.sendGetProfilesByProfileIds(Set.of("78651")).get(0);

    //Assert
    assertThat(profile, notNullValue());

  }

  @Test
  void sendGetReservationById_ShouldReturnOk() {

    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Reservation.class)).thenReturn(
        mockGetReservationResponse());

    //Act
    List<Reservation> foundReservation =
        ohipReservationClient.getReservations("TestHotelId",
            Collections.singleton("TestReservationById")).collectList().block();

    //Assert
    assertThat(foundReservation, notNullValue());
  }

  @Test
  void sendCreateReservationGuestRequest__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChangeReservationDetails.class)).thenReturn(
        mockReservationGuestResponse());

    //Act
    ChangeReservationDetails resStatus = ohipReservationClient.sendPutReservationsGuestRequest(
        "TestHotelId",
        "testReservationID", new ChangeReservation()).block();

    //Assert
    assertThat(resStatus, notNullValue());
  }

  @Test
  void sendCreateBookerProfileRequest__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Status.class)).thenReturn(
        mockBookerProfileResponse());

    //Act
    Status resStatus =
        ohipReservationClient.sendPostProfileRequest("TestHotelId", new Profile());

    //Assert
    assertThat(resStatus, notNullValue());
  }

  @Test
  void sendChangeReservationRequest_ShouldReturnOk() {

    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChangeReservationDetails.class)).thenReturn(
        mockGetChangeReservation());

    //Act
    ChangeReservationDetails changedReservation =
        ohipReservationClient.sendChangeReservationRequest("LONEUS", "35107",
            new ChangeReservation()).block();

    //Assert
    assertThat(changedReservation, notNullValue());
  }

  @Test
  void sendDepositFoliosRequest_ShouldReturnOk() {

    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PostedDepositFolio.class)).thenReturn(
        mockPostedDepositFolio());

    //Act
    PostedDepositFolio changedReservation =
        ohipReservationClient.sendDepositFoliosRequest("HOTELID", "12345",
            new DepositFolioCriteria());

    //Assert
    assertThat(changedReservation, notNullValue());
  }

  @Test
  void sendCancelReservationRequest_ShouldReturnOk() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancelReservationDetails.class)).thenReturn(
        mockPostCancelReservation());

    //Act
    CancelReservationDetails cancelReservationDetails =
        ohipReservationClient.sendPostCancelReservationRequest("LONEUS", "35107",
            new CancelReservation()).block();

    //Assert
    assertThat(cancelReservationDetails, notNullValue());
  }

  @Test
  void sendGetCancelInformationRequest__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Reservation.class)).thenReturn(mockGetCancelInformationResponse());

    //Act
    Reservation reservation =
        ohipReservationClient.getReservations("TestHotelId", Set.of("12345")).collectList().block()
            .get(0);

    //Assert
    assertThat(reservation, notNullValue());
  }

  @Test
  void sendGetReservationsByHotelId__shouldReturnOK() {
    //Arrange
    Map<String, List<String>> searchInputParams = new HashMap<>();
    searchInputParams.put(OhipConstants.HOTEL_ID_PARAM, Collections.singletonList("DHAMME"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsByHotelId());

    //Act
    ReservationsDetails reservations = ohipReservationClient.sendGetReservationsByHotelRelatedFields(
        searchInputParams);

    //Assert
    assertThat(reservations, notNullValue());

  }

  @Test
  void sendGetReservationsByGuestRelatedFields__shouldReturnOK() {
    //Arrange
    Map<String, List<String>> searchInputParams = new HashMap<>();
    searchInputParams.put(OhipConstants.GUEST_LAST_NAME_PARAM, Collections.singletonList("Doe"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsByGuestRelatedFields());

    //Act
    ReservationsDetails reservations = ohipReservationClient.sendGetReservationsByReservationRelatedFields(
        searchInputParams);

    //Assert
    assertThat(reservations, notNullValue());

  }

  @Test
  void sendGetProfileSummaries__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ProfileSummaries.class)).thenReturn(
        Mono.just(new ProfileSummaries()));

    //Act
    Map<String, List<String>> searchInputParams = new HashMap<>();
    searchInputParams.put("bookerEmail", Collections.singletonList("pod6@mailinator.com"));
    searchInputParams.put("offset", Collections.singletonList("0"));
    searchInputParams.put("limit", Collections.singletonList("20"));

    ProfileSummaries profileSummaries = ohipReservationClient.sendBookerGetProfileSummaries(
        searchInputParams);

    //Assert
    assertThat(profileSummaries, notNullValue());
  }

  @Test
  void sendGetProfilesByProfileIds__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Profile.class)).thenReturn(
        Mono.just(new Profile()));

    //Act
    List<Profile> profiles =
        ohipReservationClient.sendGetProfilesByProfileIds(
            Collections.singleton("123456"));

    //Assert
    assertThat(profiles, notNullValue());
  }

  @Test
  void sendGetProfilesByProfileIds__error_4xx() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.sendGetProfilesByProfileIds(
            Collections.singleton("123456")));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get profiles for profileId=123456",
        debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendUpdateProfileRequest__Success() {
    //Arrange
    var profileRequest = mockProfileRequest();
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Status.class)).thenReturn(
        Mono.just(new Status()));

    //Act
    Status status =
        ohipReservationClient.sendUpdateProfileRequest("HOTEL_ID", profileRequest);

    //Assert
    assertThat(status, notNullValue());
  }

  @Test
  void sendUpdateProfileRequest__error_4xx() {
    // Arrange
    var profileRequest = mockProfileRequest();
    HotelReservationException ex = mock(HotelReservationException.class);
    when(ex.getMessage()).thenReturn("Error while trying to update profile with Type: Guest");
    Mono<Status> rsp = Mono.error(ex);

    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.bodyToMono(Status.class)).thenReturn(rsp);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.sendUpdateProfileRequest("HOTEL_ID", profileRequest));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to update profile with Type: Guest", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetReservationsByReservationRelatedFields__shouldReturnOK() {
    //Arrange
    Map<String, List<String>> searchInputParams = new HashMap<>();
    searchInputParams.put(OhipConstants.EXTERNAL_REFERENCE_IDS_PARAM,
        Collections.singletonList("AWM9509632"));
    searchInputParams.put(OhipConstants.ARRIVAL_DATE_PARAM,
        Collections.singletonList("2022-12-05"));

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ReservationsDetails.class)).thenReturn(
        mockGetReservationsByReservationRelatedFields());

    //Act
    ReservationsDetails reservations = ohipReservationClient.sendGetReservationsByReservationRelatedFields(
        searchInputParams);

    //Assert
    assertThat(reservations, notNullValue());

  }

  @Test
  void sendGetDepositsByReservationId__shouldReturnOK() {
    //Arrange
    String hotelId = "DHAMME";
    String reservationId = "234567";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RetrievedDepositFolio.class)).thenReturn(
        Mono.just(new RetrievedDepositFolio()));

    //Act
    var deposits = ohipReservationClient.getDepositsByReservationId(hotelId, reservationId);

    //Assert
    assertThat(deposits, notNullValue());

  }

  @Test
  void sendGetCancellationPolicesHotelId__shouldReturnOK() {
    //Arrange
    String hotelId = "HOTELTEST";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancellationPolicyDetails.class)).thenReturn(
        Mono.just(new CancellationPolicyDetails()));

    //Act
    var cancellationPolicy = ohipReservationClient.sendGetCancellationPoliciesRequest(hotelId);

    //Assert
    assertThat(cancellationPolicy, notNullValue());

  }

  @Test
  void sendGetPolicySchedulesRequest_shouldReturnOK() {
    //Arrange
    String hotelId = "HOTELTEST";
    String ratePlan = "FLEXRATE";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(PolicySchedulesDetails.class)).thenReturn(
        Mono.just(new PolicySchedulesDetails()));

    //Act
    var cancellationPolicy = ohipReservationClient.sendGetPolicySchedulesRequest(hotelId, ratePlan);

    //Assert
    assertThat(cancellationPolicy, notNullValue());

  }

  @Test
  void testGetReservationWithRoutingInstructions_shouldReturnOK() {
    //Arrange
    String hotelId = "HOTELTEST";
    String ratePlan = "FLEXRATE";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(
        ArgumentMatchers.<Class<Void>>notNull())).thenReturn(Mono.empty().then());

    //Act
    ohipReservationClient.getReservationWithRoutingInstructions(hotelId, ratePlan);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetReservationWithRoutingInstructions_error_4xx() {
    // Arrange
    String hotelId = "HOTELTEST";
    String ratePlan = "FLEXRATE";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.getReservationWithRoutingInstructions(hotelId, ratePlan));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get reservation with routing instructions for "
        + "hotelId=HOTELTEST and reservationId=FLEXRATE", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendDeleteReservation_shouldReturnOK() {
    //Arrange
    String reservationId = "1124025";
    String hotelId = "HOTELTEST";

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    ohipReservationClient.deleteReservationRequest(hotelId, reservationId);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteRoutingInstructions_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipReservationClient.deleteRoutingInstruction("FRAMTI", "12345",
        mockDeleteRoutingInstructions());

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteRoutingInstructions__4xx_exception() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(HotelReservationException.class, () ->
        ohipReservationClient.deleteRoutingInstruction("FRAMTI", "12345",
            mockDeleteRoutingInstructions()).block());

    //Assert
    String expectedMessage = "Error while trying to delete routing instruction";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetGuestProfilesByProfileIds__success() {
    // Arrange
    var profileIds = mockProfileIds();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(
        ArgumentMatchers.<Class<Void>>notNull())).thenReturn(Mono.empty().then());

    // Act
    ohipReservationClient.sendGetGuestProfilesByProfileIds(profileIds);

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void sendGetGuestProfilesByProfileIds__4xx_exception() {
    // Arrange
    var profileIds = mockProfileIds();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    Exception exception = assertThrows(HotelReservationException.class, () ->
        ohipReservationClient.sendGetGuestProfilesByProfileIds(profileIds));

    //Assert
    String expectedMessage = "Error while trying to get profile";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetReservationWithPreferences_shouldReturnOK() {
    //Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(
        ArgumentMatchers.<Class<Void>>notNull())).thenReturn(Mono.empty().then());

    //Act
    ohipReservationClient.getReservationWithPreferences(hotelId, reservationId);

    //Assert
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void getReservationWithPreferences__error_4xx() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.getReservationWithPreferences("HOTEL_ID", "1234"));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals(
        "Error while trying to get reservation with preference for hotelId=HOTEL_ID and reservationId=1234",
        debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetActivityLogShouldReturnOk() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ActivityLog.class)).thenReturn(mockActivityLog());

    //Act
    var response = ohipReservationClient.getActivityLog(hotelId, reservationId, 5, 0);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(Integer.valueOf(5), response.getActivityLog().getTotalResults());
    assertFalse(response.getActivityLog().getHasMore());
  }

  @Test
  void testGetActivityLogShouldReturnOk_whenLimitOffsetZero() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);

    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ActivityLog.class)).thenReturn(mockActivityLog());

    //Act
    var response = ohipReservationClient.getActivityLog(hotelId, reservationId, 0, 0);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(Integer.valueOf(5), response.getActivityLog().getTotalResults());
    assertFalse(response.getActivityLog().getHasMore());
  }


  @Test
  void testGetActivityLog_ShouldThrowExceptionIf4xxError() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(ChangeLogException.class,
        () -> ohipReservationClient.getActivityLog(hotelId, reservationId, 10, 5));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get activity log from Opera", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetActivityLog_ShouldThrowExceptionIf5xxError() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(ChangeLogException.class,
        () -> ohipReservationClient.getActivityLog(hotelId, reservationId, 10, 5));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Unable to retrieve activity log", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void testGetActivityLog_ShouldThrowExceptionIf204() {

    String hotelId = "HOTELTEST";
    String reservationId = "1234";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NO_CONTENT);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(ChangeLogException.class,
        () -> ohipReservationClient.getActivityLog(hotelId, reservationId, 10, 5));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("No activity log found", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private List<ResGuestTypeProfileInfo> mockProfileResponse() {

    var idType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType();
    idType.setType("profile");
    idType.setId("123");

    uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    profileType.setProfileType(
        uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeType.GUEST);

    ResGuestTypeProfileInfo profile = new ResGuestTypeProfileInfo();
    profile.setProfileIdList(List.of(idType));
    profile.setProfile(profileType);
    return List.of(profile);
  }

  private Mono<ActivityLog> mockActivityLog() {

    ActivityLog activityLog = new ActivityLog();
    ActivityLogListType activityLogListType = new ActivityLogListType();
    List<ActivityLogType> activityLogType = new ArrayList<>();

    ActivityLogType activity = new ActivityLogType();
    activity.setActionType("New Reservation");
    activity.setActionDescription("test");

    activityLogType.add(activity);
    activityLogListType.setActivityLog(activityLogType);
    activityLogListType.setTotalResults(5);
    activityLogListType.setHasMore(false);

    activityLog.setActivityLog(activityLogListType);
    return Mono.just(activityLog);
  }

  private Set<String> mockProfileIds() {
    return Set.of("123456");
  }

  private Mono<Profile> mockProfile() {
    return Mono.just(new Profile());
  }

  private Mono<Reservation> mockGetReservationResponse() {
    return Mono.just(new Reservation());
  }

  private Mono<ReservationsDetails> mockGetReservationsDetailsResponse() {
    return Mono.just(new ReservationsDetails());
  }

  private Mono<Reservation> mockGetReservationsDetailsIdResponse() {
    return Mono.just(new Reservation());
  }

  private Mono<PriceBreakdownDto> mockPriceBreakdownDto() {
    return Mono.just(new PriceBreakdownDto());
  }

  private Mono<ReservationStatusOhipDto> mockReservationResponse() {
    return Mono.just(new ReservationStatusOhipDto());
  }

  private Mono<ChangeReservationDetails> mockGetChangeReservation() {
    return Mono.just(new ChangeReservationDetails());
  }

  private Mono<PostedDepositFolio> mockPostedDepositFolio() {
    return Mono.just(new PostedDepositFolio());
  }

  Mono<Reservation> mockGetCancelInformationResponse() {
    return Mono.just(new Reservation());
  }

  private Mono<CancelReservationDetails> mockPostCancelReservation() {
    return Mono.just(new CancelReservationDetails());
  }

  private Mono<ChangeReservationDetails> mockReservationGuestResponse() {
    return Mono.just(new ChangeReservationDetails());
  }

  private Mono<Status> mockBookerProfileResponse() {
    return Mono.just(new Status());
  }

  private Mono<ReservationsDetails> mockGetReservationsByReservationRelatedFields() {
    return Mono.just(new ReservationsDetails());
  }

  private Mono<ReservationsDetails> mockGetReservationsByGuestRelatedFields() {
    return Mono.just(new ReservationsDetails());
  }

  private Mono<ReservationsDetails> mockGetReservationsByHotelId() {
    return Mono.just(new ReservationsDetails());
  }

  private Profile mockProfileRequest() {
    var idType = new UniqueIDType();
    idType.setType("profile");
    idType.setId("123");
    var profileDetails = new ProfileType();
    profileDetails.setProfileType(ProfileTypeType.GUEST);
    var profile = new Profile();
    profile.setProfileIdList(List.of(idType));
    profile.setProfileDetails(profileDetails);
    return profile;
  }

  private MultiValueMap<String, String> mockDeleteRoutingInstructions() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put("payeeId", singletonList("186771"));
    params.put("folioWindowNo", singletonList("2"));
    params.put("routingLinkId", singletonList("28034"));
    params.put("creditLimit", singletonList("10"));
    params.put("daily", singletonList("false"));
    params.put("sunday", singletonList("true"));
    params.put("monday", singletonList("true"));
    params.put("tuesday", singletonList("true"));
    params.put("wednesday", singletonList("true"));
    params.put("thursday", singletonList("true"));
    params.put("friday", singletonList("true"));
    params.put("saturday", singletonList("true"));
    params.put("startDate", singletonList("2022-07-04"));
    params.put("endDate", singletonList("2022-07-05"));
    params.put("retrievePostingsForRoomRouting", singletonList("false"));
    return params;
  }

  @Test
  void getFoliosAciAmount__shouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(FoliosResponseDto.class)).thenReturn(mockFoliosResponseDto());

    //Act
    var folioWindows = ohipReservationClient.getFoliosAciAmount("TKINPT", "123");

    //Assert
    assertThat(folioWindows.getReservationFolioInformation(), notNullValue());
  }

  @Test
  void getFoliosAciAmount__ShouldthrowException() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenThrow(
        new HotelReservationException(OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION,
            "Unable to retrieve GetFolio ACI amount"));

    // Act
    var thrownException = assertThrowsExactly(HotelReservationException.class,
        () -> ohipReservationClient.getFoliosAciAmount("TKINPT", "123"));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Unable to retrieve GetFolio ACI amount", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private Mono<FoliosResponseDto> mockFoliosResponseDto() {
    return Mono.just(
        new FoliosResponseDto(new ReservationFolioInformationDto(mockFolioWindowsList())));
  }

  private List<FolioWindowsDto> mockFolioWindowsList() {
    return List.of(new FolioWindowsDto());
  }

  @Test
  void addAttachmentToReservation__ShouldReturnOK() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("Success"));

    // Act
    String resultMono = ohipReservationClient.addAttachmentToReservation(
        OhipTestUtils.mockFileAttachmentRequest());

    // Assert
    assertThat(resultMono, notNullValue());
    assertEquals("Success", resultMono);
  }

  @Test
  void addAttachmentToReservation__WithNullFileName() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new HotelReservationException(
        OHIP_CHANGE_RESERVATION_EXCEPTION, "Error while trying to add attachment")));
    FileToUpload request = OhipTestUtils.mockFileAttachmentRequest();
    request.setFileName(null);

    // Act
    var thrownException = assertThrows(HotelReservationException.class,
        () -> ohipReservationClient.addAttachmentToReservation(request));

    // Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to add attachment", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void addAttachmentToReservation_TimeoutExceeded() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(String.class))
        .thenReturn(Mono.just("Success").delayElement(Duration.ofSeconds(20)));

    FileToUpload request = OhipTestUtils.mockFileAttachmentRequest();

    // Act
    var thrownException = assertThrows(HotelReservationException.class,
        () -> ohipReservationClient.addAttachmentToReservation(request));

    // Assert
    assertEquals("Operation timed out while adding attachment to reservation",
        thrownException.getMessage());
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void savePreCheckInStatus_ShouldReturnStatus() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Status.class)).thenReturn(Mono.just(new Status()));

    PreCheckInReservation request = new PreCheckInReservation();
    String hotelId = "STUAIR";
    String reservationId = "123456";

    // Act
    Status resultStatus = ohipReservationClient.savePreCheckInStatus(request, hotelId,
        reservationId);

    // Assert
    assertNotNull(resultStatus);
  }

  @Test
  void savePreCheckInStatus_ShouldHandleError() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Status.class)).thenReturn(Mono.error(new HotelReservationException(
        ErrorCode.OHIP_POST_PROFILE_EXCEPTION, "Unable to save Pre-CheckIn status")));

    PreCheckInReservation request = new PreCheckInReservation();
    String hotelId = "STUAIR";
    String reservationId = "123456";

    // Act and Assert
    HotelReservationException thrownException = assertThrows(HotelReservationException.class,
        () -> ohipReservationClient.savePreCheckInStatus(request, hotelId, reservationId));

    assertEquals("Unable to save Pre-CheckIn status", thrownException.getMessage());
  }

  @Test
  void savePreCheckInStatus_ShouldHandleNullParameter() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);

    // Act and Assert
    assertThrows(NullPointerException.class, () -> {
      ohipReservationClient.savePreCheckInStatus(null, "STUAIR", "123456");
    });
  }

  @Test
  void savePreCheckInStatus_WithNullHotelId() {
    // Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(Mono.class), any(Class.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Status.class)).thenReturn(Mono.error(new HotelReservationException(
        ErrorCode.OHIP_POST_PROFILE_EXCEPTION, "Error while trying to save pre-check-in status")));

    PreCheckInReservation request = new PreCheckInReservation();

    // Act
    var thrownException = assertThrows(HotelReservationException.class,
        () -> ohipReservationClient.savePreCheckInStatus(request, null, "123456"));

    // Assert
    String debugMessage = thrownException.getMessage();
    assertEquals("Error while trying to save pre-check-in status", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteReservationPreCheckIn_success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipReservationClient.deleteReservationPreCheckIn("FRAMTI", "12345");

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteReservationPreCheckIn_4xx_exception() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(HotelReservationException.class, () ->
        ohipReservationClient.deleteReservationPreCheckIn("FRAMTI", "12345"));

    //Assert
    String expectedMessage = "Error while trying to delete reservation pre-checkIn for"
        + " hotelId=FRAMTI and reservationId=12345";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteReservationAttachment_success() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    // Act
    ohipReservationClient.deleteReservationAttachment("FRAMTI", "12345", "12345");

    // Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void deleteReservationAttachment_4xx_exception() {
    // Arrange
    when(webClient.delete()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(HotelReservationException.class, () ->
        ohipReservationClient.deleteReservationAttachment("FRAMTI", "12345", "12345"));

    //Assert
    String expectedMessage = "Error while trying to delete reservation attachment for"
        + " hotelId=FRAMTI, reservationId=12345 and attachmentId=12345";
    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getReservationPaymentMethods_4xx_exception() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    //Act
    Exception exception = assertThrows(HotelReservationException.class, () ->
        ohipReservationClient.getReservationPaymentMethods("TestHotelId", "reservationId"));
    //Assert
    String expectedMessage = "Error while trying to get reservation payment method by ids for hotelId=TestHotelId and reservationId";

    String actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
    verifyNoMoreInteractions(webClient);
  }

}
