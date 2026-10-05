package uk.co.whitbread.ohip.infrastructure.rest.client.profile;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static uk.co.whitbread.ohip.ErrorCode.*;

import java.util.Collections;
import java.util.List;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.profile.ProfileTestUtils;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfileWrapper;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyWrapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.exception.ProfileException;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipCreateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipProfileMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipUpdateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.OhipProfileClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@ExtendWith(MockitoExtension.class)
class ProfileOutPortImplTest {

  @InjectMocks
  private ProfileOutPortImpl profileOutPort;

  @Mock
  private OhipProfileClient ohipProfileClient;
  @Mock
  private OhipReservationClient ohipReservationClient;

  @Mock
  private OhipProfileMapper ohipProfileMapper;
  @Mock
  private OhipCreateProfileRequestMapper ohipCreateProfileRequestMapper;
  @Mock
  private OhipUpdateProfileRequestMapper ohipUpdateProfileRequestMapper;

  @Test
  void createProfileRequest__ShouldReturnOK() {
    //Arrange
    var request = ProfileTestUtils.mockGuestDetailsRequest();

    //Act
    var response = profileOutPort.createProfileRequest(request, "HOTEL_ID");

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void createProfile__ShouldReturnOK() {
    //Arrange
    var request = ProfileTestUtils.mockStayingGuestDetails();
    when(ohipProfileClient.createProfile(any(), anyString())).thenReturn(
        ProfileTestUtils.mockCreateProfileResponse());
    when(ohipProfileClient.getProfileIdByReservation(anyString(), anyString())).thenReturn(
        ProfileTestUtils.mockgetProfileIdByReservation());
    when(ohipCreateProfileRequestMapper.toProfileModel(any(), anyString())).thenReturn(
        new Profile());

    //Act
    var response = profileOutPort.createProfile(request, "HOTEL_ID", "123456");

    //Assert
    assertThat(response, notNullValue());
    assertThat(response, hasSize(2));
    verify(ohipProfileClient, times(1))
        .createProfile(any(), anyString());
  }

  @Test
  void getCheckInResponse__shouldThrowException() {
    // Arrange
    String errorCreateProfile = "Error while trying to get ProfileId";
    var request = ProfileTestUtils.mockStayingGuestDetails();
    when(ohipProfileClient.getProfileIdByReservation(anyString(), anyString())).thenThrow(
        new ProfileException(OHIP_GET_PROFILEID_EXCEPTION, "Error while trying to get ProfileId"));
    // Act
    ProfileException exception = Assertions
        .assertThrows(ProfileException.class, () ->
            profileOutPort.createProfile(request, "HOTEL_ID", "123456")
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), errorCreateProfile);
  }

  @Test
  void addProfile__ShouldReturnOK() {
    //Arrange
    var request = ProfileTestUtils.mockAddProfileRequest();

    //Act
    profileOutPort.addProfile(request, "HOTEL_ID");

    //Assert
    verify(ohipProfileClient, times(1)).addProfile(any(), anyString());

  }

  @Test
  void updateProfile__ShouldReturnOK() {
    //Arrange
    var reservation = mockReservation();
    var request = mockKioskUpdateProfileRequest();
    when(ohipReservationClient.getReservation(any(), anyString())).thenReturn(
        Mono.just(reservation));
    when(ohipUpdateProfileRequestMapper.toProfileModel(any(), any(), anyString())).thenReturn(
        new Profile());
    doNothing().when(ohipProfileClient).updateProfile(any(), anyString());

    //Act
    profileOutPort.updateProfile("HOTELID", "HOTEL_ID", request);

    //Assert
    verify(ohipProfileClient, times(1)).updateProfile(any(), anyString());

  }

  @Test
  void updateProfile_ShouldUseSourceCode_WhenRoomRatesPresent() {

    // Arrange
    var reservation = mockReservation();

    var roomRate = new RoomRateType();
    roomRate.setSourceCode("46");

    var roomStay = new RoomStayType();
    roomStay.setRoomRates(List.of(roomRate));

    reservation.getReservations()
            .getReservation()
            .get(0)
            .setRoomStay(roomStay);

    var request = mockKioskUpdateProfileRequest();

    when(ohipReservationClient.getReservation(any(), anyString()))
            .thenReturn(Mono.just(reservation));

    when(ohipUpdateProfileRequestMapper.toProfileModel(
            any(), any(), anyString()))
            .thenReturn(new Profile());

    doNothing().when(ohipProfileClient)
            .updateProfile(any(), anyString());

    // Act
    profileOutPort.updateProfile("HOTELID", "HOTEL_ID", request);

    // Assert
    verify(ohipUpdateProfileRequestMapper, times(1))
            .toProfileModel(any(), any(), anyString());

    verify(ohipProfileClient, times(1))
            .updateProfile(any(), anyString());
  }

  @Test
  void updateProfile_ShouldHandleNullRoomStay() {

    var reservation = mockReservation();

    reservation.getReservations()
            .getReservation()
            .get(0)
            .setRoomStay(null);

    var request = mockKioskUpdateProfileRequest();

    when(ohipReservationClient.getReservation(any(), anyString()))
            .thenReturn(Mono.just(reservation));

    when(ohipUpdateProfileRequestMapper.toProfileModel(
            any(), any(), anyString()))
            .thenReturn(new Profile());

    doNothing().when(ohipProfileClient)
            .updateProfile(any(), anyString());

    profileOutPort.updateProfile("HOTELID", "HOTEL_ID", request);

    verify(ohipUpdateProfileRequestMapper)
            .toProfileModel(any(), any(), anyString());

    verify(ohipProfileClient)
            .updateProfile(any(), anyString());
  }

  @Test
  void updateProfile_ShouldThrowException_WhenReservationGuestIsNull() {

    // Arrange
    var reservation = mockReservation();

    reservation.getReservations()
            .getReservation()
            .get(0)
            .setReservationGuests(null);

    var request = mockKioskUpdateProfileRequest();

    when(ohipReservationClient.getReservation(any(), anyString()))
            .thenReturn(Mono.just(reservation));

    // Act + Assert
    HotelReservationException exception =
            assertThrows(
                    HotelReservationException.class,
                    () -> profileOutPort.updateProfile(
                            "HOTELID",
                            "HOTEL_ID",
                            request));

    assertEquals(
            ErrorCode.DIGITAL_NO_RESERV_ID_EXCEPTION.getCode(),
            exception.getErrorCode());

    verify(ohipProfileClient, never())
            .updateProfile(any(), anyString());
  }
  @Test
  void updateProfile__shouldThrowException() {
    // Arrange
    String error = "Error while trying to get reservations " +
            "by ids for hotelId=HOTEL_ID and 1234 ids";
    doThrow(new ProfileException(OHIP_GET_RESERVATIONS_EXCEPTION, error)).when(ohipReservationClient)
        .getReservation(anyString(), anyString());

    //Act
    ProfileException exception = Assertions
        .assertThrows(ProfileException.class, () ->
            profileOutPort.updateProfile("HOTEL_ID", "1234", new UpdateProfileRequest()));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);

  }

  @Test
  void addProfile__shouldThrowException() {
    // Arrange
    String error = "Error while trying to add profile to reservation";
    var request = ProfileTestUtils.mockAddProfileRequest();
    doThrow(new ProfileException(OHIP_ADD_PROFILE_RESERVATION_EXCEPTION, error)).when(ohipProfileClient)
        .addProfile(any(), anyString());

    //Act
    ProfileException exception = Assertions
        .assertThrows(ProfileException.class, () ->
            profileOutPort.addProfile(request, "HOTEL_ID"));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);

  }

  @Test
  void getCompanyProfileByCorporateId__ShouldReturnOK() {
    //Arrange
    var request = "corporateId";
    var company = new Company();
    var companyWrapper = CompanyWrapper.builder().company(company).build();
    var companyProfile = CompanyProfile.builder().companyId("companyId").build();

    when(ohipProfileClient.getCompanyByCorporateId(request)).thenReturn(company);
    when(ohipProfileMapper.toCompanyProfileModel(companyWrapper))
        .thenReturn(CompanyProfileWrapper.builder().companyProfile(companyProfile).build());
    //Act
    var companyProfileResult = profileOutPort.getCompanyProfileByCorporateId(request);

    //Assert
    AssertionsForClassTypes.assertThat(companyProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfile);
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(ohipProfileMapper);
  }

  @Test
  void getCompanyProfileByCompanyId__ShouldReturnOKByCorporateId() {
    //Arrange
    var request = "companyId";
    var corporateId = "corporateId";
    var company = new Company();
    var profile = new Profile();

    var uniqueIDType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId(corporateId);
    uniqueIDType.setType("CorporateId");

    profile.setProfileIdList(List.of(uniqueIDType));
    var companyWrapper = CompanyWrapper.builder().company(company).build();
    var companyProfile = CompanyProfile.builder().companyId("companyId").build();

    when(ohipProfileClient.getCompanyByCorporateId(corporateId)).thenReturn(company);
    when(ohipProfileClient.getCompanyProfile(request)).thenReturn(profile);
    when(ohipProfileMapper.toCompanyProfileModel(companyWrapper))
        .thenReturn(CompanyProfileWrapper.builder().companyProfile(companyProfile).build());
    //Act
    var companyProfileResult = profileOutPort.getCompanyProfileByCompanyId(request);

    //Assert
    AssertionsForClassTypes.assertThat(companyProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfile);
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(ohipProfileMapper);
  }

  @Test
  void getCompanyProfileByCompanyId__ShouldBeEmptyByCorporateId() {
    //Arrange
    var request = "companyId";
    var company = new Company();
    var profile = new Profile();

    profile.setProfileIdList(List.of());
    var companyWrapper = CompanyWrapper.builder().company(company).build();
    var emptyCompanyProfile = CompanyProfile.builder().build();

    when(ohipProfileClient.getCompanyProfile(request)).thenReturn(profile);
    when(ohipProfileMapper.toCompanyProfileModel(companyWrapper))
        .thenReturn(CompanyProfileWrapper.builder().companyProfile(emptyCompanyProfile).build());
    //Act
    var companyProfileResult = profileOutPort.getCompanyProfileByCompanyId(request);

    //Assert
    AssertionsForClassTypes.assertThat(companyProfileResult).usingRecursiveComparison()
        .isEqualTo(emptyCompanyProfile);
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(ohipProfileMapper);
  }

  @Test
  void getCompanyProfile__ByCorporateId__shouldThrowException() {
    // Arrange
    String errorMsg = "Error while trying to get company profile";
    var request = "corporateId";
    doThrow(new ProfileException(OHIP_GET_COMPANY_PROFILE_EXCEPTION, errorMsg))
        .when(ohipProfileClient)
        .getCompanyByCorporateId(request);

    //Act
    var exception = Assertions
        .assertThrows(ProfileException.class, () ->
            profileOutPort.getCompanyProfileByCorporateId(request));

    // Assert
    Assertions.assertEquals(errorMsg, exception.getMessage());
    verifyNoInteractions(ohipProfileMapper);
  }

  @Test
  void getCompaniesProfile__ShouldReturnOK() {
    //Arrange
    var request = CompaniesProfileRequest.builder().limit(2).hotelId("TEST").build();
    var profileSummary = new ProfileSummaries();
    var companiesProfile = CompaniesProfile.builder().totalResults(0).hasMore(false).build();

    when(ohipProfileClient.getCompaniesProfile(request.getHotelId(), request.getArNumber(),
        request.getCompanyName(),
        request.getLimit()))
        .thenReturn(profileSummary);
    when(ohipProfileMapper.toCompaniesProfileModel(profileSummary))
        .thenReturn(companiesProfile);
    //Act
    var companiesProfileResult = profileOutPort.getCompaniesProfile(request);

    //Assert
    AssertionsForClassTypes.assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companiesProfile);
    verifyNoMoreInteractions(ohipProfileClient);
    verifyNoMoreInteractions(ohipProfileMapper);
  }

  @Test
  void getCompaniesProfile__shouldThrowException() {
    // Arrange
    String errorMsg = "An error was returned by OHIP!";
    var request = CompaniesProfileRequest.builder().limit(2).hotelId("MANOLD").companyName("TEST").build();
    doThrow(new ProfileException(OHIP_GET_COMPANIES_PROFILE_EXCEPTION, errorMsg))
        .when(ohipProfileClient)
        .getCompaniesProfile("MANOLD", null, "TEST", 2);

    //Act
    var exception = Assertions
        .assertThrows(ProfileException.class, () ->
            profileOutPort.getCompaniesProfile(request));

    // Assert
    Assertions.assertEquals(errorMsg, exception.getMessage());
    verifyNoInteractions(ohipProfileMapper);
  }

  private Reservation mockReservation() {

    var uniqueIDType = new UniqueIDType();
    uniqueIDType.setId("12345");
    uniqueIDType.setType("Profile");
    var personNameType = new PersonNameType();
    personNameType.setGivenName("Test");
    personNameType.setSurname("Name");
    var customerType = new CustomerType();
    customerType.setPersonName(List.of(personNameType));
    var profileType = new ProfileType();
    profileType.setCustomer(customerType);

    var resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIDType));
    resGuestTypeProfileInfo.setProfile(profileType);

    var resGuestType = new ResGuestType();
    resGuestType.setProfileInfo(resGuestTypeProfileInfo);

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setReservationGuests(List.of(resGuestType));

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);
    return reservation;
  }

  private UpdateProfileRequest mockKioskUpdateProfileRequest() {
    return UpdateProfileRequest.builder().guestDetails(Collections.singletonList(
            GuestDetails.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }
}
