package uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummariesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservations;
import uk.co.whitbread.ohip.domain.model.profile.out.CreateProfileResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.KioskProfileInfoOut;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileIdList;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationDetailsResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationGuestsOut;
import uk.co.whitbread.ohip.domain.model.profile.out.Reservation;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.exception.ProfileException;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipProfileClientTest {

  @InjectMocks
  private OhipProfileClient ohipProfileClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void getCompany__ShouldReturnOK() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Company.class)).thenReturn(
        mockCompany());

    //Act
    Company company =
        ohipProfileClient.getCompanyByCorporateId("123");

    //Assert
    assertThat(company, notNullValue());
    assertEquals("555",
        Objects.requireNonNull(company).getCompanyIdList().get(0).getId());
  }

  @Test
  void getCompanyByCompId__ShouldReturnOK() {
    // Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(Profile.class)).thenReturn(
        mockProfile());

    //Act
    Profile company =
        ohipProfileClient.getCompanyProfile("123");

    //Assert
    assertThat(company, notNullValue());
    assertEquals("555",
        Objects.requireNonNull(company).getProfileIdList().get(0).getId());
  }

  @Test
  void getProfileIdByReservation__ShouldReturnOK() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ProfileReservationDetailsResponse.class)).thenReturn(
        mockReservationResponse());

    //Act
    ProfileReservationDetailsResponse reservationDetailsResponse =
        ohipProfileClient.getProfileIdByReservation("123456", "MANOLD");

    //Assert
    assertThat(reservationDetailsResponse, notNullValue());
    assertEquals("24567",
        Objects.requireNonNull(reservationDetailsResponse).getReservations().getReservation().get(0)
            .getReservationGuests().get(0).getProfileInfo().getProfileIdList().get(0).getId());
  }

  @Test
  void getProfileIdByReservation__ShouldThrowException() {
    // Arrage
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(ProfileException.class,
        () -> ohipProfileClient.getProfileIdByReservation("12345", "MANOLD"));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get ProfileId", debugMessage);
    verifyNoMoreInteractions(webClient);
  }


  @Test
  void createProfile__ShouldReturnOK() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CreateProfileResponse.class)).thenReturn(
        mockCreateProfileResponse());

    //Act
    CreateProfileResponse createProfileResponse =
        ohipProfileClient.createProfile(new Profile(), "LONEUS");

    //Assert
    assertThat(createProfileResponse, notNullValue());
  }

  @Test
  void addProfile__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    ohipProfileClient.addProfile(mockAddProfileRequest(), "LONEUS");

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void updateProfile__ShouldReturnOK() {
    //Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.put()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

    //Act
    ohipProfileClient.updateProfile(new Profile(), "LONEUS");

    //Assert
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getCompaniesProfile_validArNumber_ShouldReturnOK() {
    getCompaniesProfile_differentQueryParams("AR-7492");
  }

  @Test
  void getCompaniesProfile_emptyArNumber_ShouldReturnOK() {
    getCompaniesProfile_differentQueryParams(null);
  }

  void getCompaniesProfile_differentQueryParams(String arNumber) {
    //Arrange
    var profileSummaries = new ProfileSummaries();
    var summariesType = new ProfileSummariesType();
    summariesType.setTotalResults(0);
    summariesType.setHasMore(false);
    profileSummaries.setProfileSummaries(summariesType);

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ProfileSummaries.class)).thenReturn(Mono.just(profileSummaries));

    //Act
    var profileSummariesResult =
        ohipProfileClient.getCompaniesProfile("MANOLD", arNumber, null, 2);

    //Assert
    assertThat(profileSummariesResult, notNullValue());
    AssertionsForClassTypes.assertThat(profileSummariesResult).usingRecursiveComparison()
        .isEqualTo(profileSummaries);
    verifyNoMoreInteractions(webClient);
  }

  @Test
  void getCompaniesProfile__ShouldThrowException() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(ProfileException.class,
        () -> ohipProfileClient.getCompaniesProfile("MANOLD", "", null, 2));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get Companies Profile", debugMessage);
    verifyNoMoreInteractions(webClient);
  }

  private AddProfileRequest mockAddProfileRequest() {
    return AddProfileRequest.builder()
        .reservations(List.of(ProfileReservations.builder()
            .reservationIdList(List.of(ProfileReservationIdList.builder()
                .id("1")
                .build()))
            .build()))
        .build();
  }

  private Mono<CreateProfileResponse> mockCreateProfileResponse() {
    return Mono.just(
        CreateProfileResponse.builder().build());
  }

  private Mono<Company> mockCompany() {
    var company = new Company();
    var id = new UniqueIDType();
    id.setId("555");
    company.setCompanyIdList(List.of(id));

    return Mono.just(company);
  }

  private Mono<Profile> mockProfile() {
    var profile = new Profile();
    var id = new UniqueIDType();
    id.setId("555");
    profile.setProfileIdList(List.of(id));

    return Mono.just(profile);
  }

  private Mono<ProfileReservationDetailsResponse> mockReservationResponse() {
    var reservationDetailsResponse = ProfileReservationDetailsResponse.builder().reservations(
        uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationsOut.builder().reservation(
            Collections.singletonList(
                Reservation.builder().reservationGuests(Collections.singletonList(
                    ProfileReservationGuestsOut.builder().profileInfo(
                            KioskProfileInfoOut.builder().profileIdList(Collections.singletonList(
                                ProfileIdList.builder().id("24567").type("Guest").build())).build())
                        .build())).build())).build()).build();
    return Mono.just(reservationDetailsResponse);
  }

}
