package uk.co.whitbread.ohip.infrastructure.rest.controller.profile;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;
import uk.co.whitbread.ohip.domain.model.profile.in.CreateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.Customer;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.KioskProfileInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.MailingActions;
import uk.co.whitbread.ohip.domain.model.profile.in.PrivacyInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileAddress;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileAddressInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileAddresses;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileEmail;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileEmailInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileEmails;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfilePersonName;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationGuests;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservationIdList;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileReservations;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.Telephone;
import uk.co.whitbread.ohip.domain.model.profile.in.TelephoneInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.Telephones;
import uk.co.whitbread.ohip.domain.model.profile.out.Address;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CreateProfileResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileLinks;
import uk.co.whitbread.ohip.domain.ports.primary.ProfileInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.AddProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CompaniesProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CompanyProfileResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.CreateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.mapper.UpdateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.CompaniesProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.GuestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.UpdateProfileRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.ProfileStayingGuestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.in.RawRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.AddressDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompaniesProfileDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.profile.model.out.CompanyProfileDto;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

  @InjectMocks
  ProfileController profileController;

  @Mock
  private CreateProfileRequestMapper createProfileRequestMapper;

  @Mock
  private CompaniesProfileRequestMapper companiesProfileRequestMapper;

  @Mock
  private CompanyProfileResponseMapper companyProfileResponseMapper;

  @Mock
  private AddProfileRequestMapper addProfileRequestMapper;
  @Mock
  private UpdateProfileRequestMapper updateProfileRequestMapper;

  @Mock
  private ProfileInPort profileInPort;

  @Test
  void createProfileKiosk__ShouldReturnOk() {
    //Arrange
    ProfileStayingGuestDetailsDto stayingGuestDetailsDto = mockStayingGuestDetailsDto();

    when(createProfileRequestMapper.toProfileRequestModel(stayingGuestDetailsDto)).thenReturn(
        mockStayingGuestDetails());
    when(profileInPort.createProfile(mockStayingGuestDetails(), "HOTEL_ID", "1234"))
        .thenReturn(Arrays.asList("1234", "2345"));
    when(addProfileRequestMapper.toAddProfileRequestModel(createRawRequestDto())).thenReturn(
        mockAddProfileRequest());

    //act
    profileController.createProfileKiosk("HOTEL_ID", "1234", stayingGuestDetailsDto);

    //Assert
    verify(createProfileRequestMapper, times(1)).toProfileRequestModel(any());
    verify(profileInPort, times(1)).createProfile(any(), anyString(), eq("1234"));
    verify(addProfileRequestMapper, times(1)).toAddProfileRequestModel(any());
  }

  @Test
  void getCompaniesProfile__ShouldReturnOk() {
    //Arrange
    var companiesProfileRequestDto = CompaniesProfileRequestDto.builder()
        .companyName("")
        .hotelId("TEST")
        .limit(1)
        .build();
    var companiesProfileRequest = CompaniesProfileRequest.builder()
        .companyName("")
        .hotelId("TEST")
        .limit(1)
        .build();
    var companiesProfile = CompaniesProfile.builder().totalResults(0).build();
    var companiesProfileDto = CompaniesProfileDto.builder().totalResults(0).build();

    when(companiesProfileRequestMapper.toCompaniesProfileDomainModel(
        companiesProfileRequestDto)).thenReturn(
        companiesProfileRequest);
    when(profileInPort.getCompaniesProfile(companiesProfileRequest)).thenReturn(
        companiesProfile);
    when(companiesProfileRequestMapper.toCompaniesProfileDto(companiesProfile)).thenReturn(
        companiesProfileDto);

    //act
    var companiesProfileResult = profileController.getCompaniesProfile(companiesProfileRequestDto);

    //Assert
    assertThat(companiesProfileResult).usingRecursiveComparison()
        .isEqualTo(companiesProfileDto);
    verifyNoMoreInteractions(companiesProfileRequestMapper);
    verifyNoMoreInteractions(profileInPort);
  }

  @Test
  void getCompanyProfileByCorporateId__ShouldReturnOk() {
    //Arrange
    String corporateId = "corporateId";
    var companyProfile = CompanyProfile
        .builder()
        .companyId("companyId")
        .corpId("corpId")
        .address(Address
            .builder()
            .addressLine1("address line one")
            .build()
        )
        .build();
    var companyProfileDto = CompanyProfileDto
        .builder()
        .companyId("companyId")
        .corpId("corpId")
        .address(AddressDto
            .builder()
            .addressLine1("address line one")
            .build()
        )
        .build();
    when(profileInPort.getCompanyProfileByCorporateId(corporateId)).thenReturn(companyProfile);
    when(companyProfileResponseMapper.toCompanyProfileDto(companyProfile)).thenReturn(
        companyProfileDto);

    //act
    var companyProfileResult = profileController.getCompanyProfileByCorporateId(corporateId);

    //Assert
    assertThat(companyProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfileDto);
    verifyNoMoreInteractions(companyProfileResponseMapper);
    verifyNoMoreInteractions(profileInPort);
  }

  @Test
  void getCompanyProfileByCompanyId__ShouldReturnOk() {
    //Arrange
    String companyId = "companyId";
    var companyProfile = CompanyProfile
        .builder()
        .companyId("companyId")
        .corpId("corpId")
        .address(Address
            .builder()
            .addressLine1("address line one")
            .build()
        )
        .build();
    var companyProfileDto = CompanyProfileDto
        .builder()
        .companyId("companyId")
        .corpId("corpId")
        .address(AddressDto
            .builder()
            .addressLine1("address line one")
            .build()
        )
        .build();
    when(profileInPort.getCompanyProfileByCompanyId(companyId)).thenReturn(companyProfile);
    when(companyProfileResponseMapper.toCompanyProfileDto(companyProfile)).thenReturn(
        companyProfileDto);

    //act
    var companyProfileResult = profileController.getCompanyProfileByCompanyId(companyId);

    //Assert
    assertThat(companyProfileResult).usingRecursiveComparison()
        .isEqualTo(companyProfileDto);
    verifyNoMoreInteractions(companyProfileResponseMapper);
    verifyNoMoreInteractions(profileInPort);
  }

  @Test
  void updateProfileKiosk__ShouldReturnOk() {
    //Arrange
    UpdateProfileRequestDto kioskUpdateProfileRequestDto = mockKioskUpdateProfileRequestDto();

    when(updateProfileRequestMapper.toUpdateProfileRequestModel(
        kioskUpdateProfileRequestDto)).thenReturn(mockKioskUpdateProfileRequest());
    doNothing().when(profileInPort).updateProfile(anyString(), anyString(), any());

    //act
    profileController.updateProfileKiosk("HOTEL_ID", "1234", kioskUpdateProfileRequestDto);

    //Assert
    verify(updateProfileRequestMapper, times(1)).toUpdateProfileRequestModel(any());
    verify(profileInPort, times(1)).updateProfile(any(), anyString(), any());
  }

  private ProfileStayingGuestDetailsDto mockStayingGuestDetailsDto() {
    return ProfileStayingGuestDetailsDto.builder().guestDetails(Collections.singletonList(
            GuestDetailsDto.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }

  private UpdateProfileRequestDto mockKioskUpdateProfileRequestDto() {
    return UpdateProfileRequestDto.builder().guestDetails(Collections.singletonList(
            GuestDetailsDto.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }

  private ProfileStayingGuestDetails mockStayingGuestDetails() {
    return ProfileStayingGuestDetails.builder().guestDetails(Collections.singletonList(
            GuestDetails.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }

  private UpdateProfileRequest mockKioskUpdateProfileRequest() {
    return UpdateProfileRequest.builder().guestDetails(Collections.singletonList(
            GuestDetails.builder().givenName("Test").surname("Name").nameTitle("Mr").build()))
        .build();
  }


  private CreateProfileRequest mockCreateProfileRequest() {
    var customer = Customer.builder().personName(List.of(
        ProfilePersonName.builder().givenName("Testerson").surname("Tester").nameType("nameType")
            .build())).build();
    var addresses = ProfileAddresses.builder()
        .addressInfo(List.of(ProfileAddressInfo.builder().address(
            ProfileAddress.builder().isValidated(true).addressLine(
                    List.of("4 Brockley Avenue", "London District 2", "Greater London - sub district 2",
                        "Greater London - street 22")).cityName("London").postalCode("EC1A 1BB")
                .state("state").country(new Country("GB")).language("en").type("HOME")
                .primaryInd(true)
                .build()).build())).build();
    var telephones = Telephones.builder().telephoneInfo(
        List.of(TelephoneInfo.builder().telephone(Telephone.builder().phoneNumber("1234567")
            .build()).build())).build();
    var emails = ProfileEmails.builder().emailInfo(List.of(ProfileEmailInfo.builder().email(
        ProfileEmail.builder().emailAddress("tester@testerson.com").type("type")
            .typeDescription("description").build()).build())).build();
    var actions = MailingActions.builder().active(true).build();
    var privacyInfo = PrivacyInfo.builder().optInEmail(true).build();
    return CreateProfileRequest.builder().profileDetails(
        ProfileDetails.builder().customer(customer).addresses(addresses).telephones(telephones)
            .emails(emails).mailingActions(actions).privacyInfo(privacyInfo)
            .markAsRecentlyAccessed(true).profileType("type").build()).profileIdList(List.of(
        ProfileIdList.builder().id("112").build())).build();
  }

  private CreateProfileResponse mockCreateProfileResponse() {
    return CreateProfileResponse.builder().links(List.of(ProfileLinks.builder().href(
            "https://whitbce4ua.hospitality-api.eu-frankfurt-1.ocs.oc-test.com/crm/v1/profiles/112")
        .build())).build();
  }

  private RawRequestDto createRawRequestDto() {
    return RawRequestDto.builder().profileId(Arrays.asList("1234", "2345")).reservationId("1234")
        .build();
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
