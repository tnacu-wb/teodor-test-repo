package uk.co.whitbread.ohip.infrastructure.rest.client.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Company;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.profile.in.AddProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CompaniesProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.CreateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.in.Customer;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.PrivacyInfo;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfilePersonName;
import uk.co.whitbread.ohip.domain.model.profile.in.ProfileStayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.domain.model.profile.out.CompaniesProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyProfile;
import uk.co.whitbread.ohip.domain.model.profile.out.CompanyWrapper;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationDetailsResponse;
import uk.co.whitbread.ohip.domain.model.profile.out.ProfileReservationGuestsOut;
import uk.co.whitbread.ohip.domain.ports.secondary.ProfileOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipCreateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipProfileMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper.OhipUpdateProfileRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.profile.ohip.OhipProfileClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@RequiredArgsConstructor
@Slf4j
public class ProfileOutPortImpl implements ProfileOutPort {

  private final OhipProfileClient ohipProfileClient;
  private final OhipReservationClient ohipReservationClient;
  private final OhipProfileMapper ohipProfileMapper;
  private final OhipUpdateProfileRequestMapper ohipUpdateProfileRequestMapper;
  private final OhipCreateProfileRequestMapper ohipCreateProfileRequestMapper;
  public static final Set<String> THIRD_PARTY_SOURCE_CODES_SET = Set.of("35", "36", "38", "43", "46", "49");

  @Override
  public List<String> createProfile(ProfileStayingGuestDetails stayingGuestDetails,
      String hotelId, String reservationId) {

    ProfileReservationDetailsResponse profileIdByReservation = ohipProfileClient.getProfileIdByReservation(
        reservationId, hotelId);
    List<ProfileReservationGuestsOut> reservationGuests = profileIdByReservation.getReservations()
        .getReservation().get(0).getReservationGuests();
    List<String> profileIds = new ArrayList<>(reservationGuests.stream()
        .map(guest -> guest.getProfileInfo().getProfileIdList().get(0).getId()).toList());

    Flux.fromIterable(stayingGuestDetails.getGuestDetails())
        .flatMap(guestDetails -> {
          final var profile = ohipProfileClient.createProfile(
              ohipCreateProfileRequestMapper.toProfileModel(guestDetails, hotelId), hotelId);
          String href = profile.getLinks().get(0).getHref();
          return Mono.just(profileIds.add(href.substring(href.lastIndexOf("/") + 1)));
        }).collectList().block();
    return profileIds;
  }

  @Override
  public void addProfile(AddProfileRequest addProfileRequest, String hotelId) {
    ohipProfileClient.addProfile(addProfileRequest, hotelId);
  }

  @Override
  public CreateProfileRequest createProfileRequest(GuestDetails guestDetails, String hotelId) {
    List<ProfilePersonName> personNameList = Collections.singletonList(ProfilePersonName.builder()
        .nameType("Primary")
        .givenName(guestDetails.getGivenName())
        .surname(guestDetails.getSurname())
        .nameTitle(guestDetails.getNameTitle())
        .build());

    return CreateProfileRequest.builder().profileDetails(
            ProfileDetails.builder()
                .customer(Customer.builder().personName(personNameList).build())
                .privacyInfo(PrivacyInfo.builder().build())
                .requestForHotel(hotelId)
                .markAsRecentlyAccessed(true)
                .profileType("Guest")
                .build())
        .build();
  }

  @Override
  public CompaniesProfile getCompaniesProfile(CompaniesProfileRequest companiesProfileRequest) {
    var companiesProfileSummary = ohipProfileClient.getCompaniesProfile(
        companiesProfileRequest.getHotelId(),
        companiesProfileRequest.getArNumber(),
        companiesProfileRequest.getCompanyName(),
        companiesProfileRequest.getLimit());
    return ohipProfileMapper.toCompaniesProfileModel(companiesProfileSummary);
  }

  @Override
  public CompanyProfile getCompanyProfileByCorporateId(final String corporateId) {
    var company = ohipProfileClient.getCompanyByCorporateId(corporateId);
    var companyProfileWrapper = ohipProfileMapper.toCompanyProfileModel(CompanyWrapper
        .builder()
        .company(company)
        .build()
    );
    return companyProfileWrapper.getCompanyProfile();
  }

  @Override
  public CompanyProfile getCompanyProfileByCompanyId(String companyId) {
    var profile = ohipProfileClient.getCompanyProfile(companyId);

    if (profile != null) {
      Optional<String> corporateId = profile.getProfileIdList().stream()
          .filter(p -> "CorporateId".equals(p.getType()))
          .map(UniqueIDType::getId)
          .findFirst();

      if (corporateId.isPresent()) {
        return getCompanyProfileByCorporateId(corporateId.get());
      }
    }

    return buildEmptyCompanyProfile();
  }

  @Override
  public void updateProfile(String hotelId, String reservationId,
      UpdateProfileRequest updateProfileRequestDto) {
    var getReservationResponse = ohipReservationClient.getReservation(hotelId, reservationId)
        .block();

    var reservationGuests = getReservationResponse.getReservations().getReservation().get(0)
        .getReservationGuests();

    if (reservationGuests != null && reservationGuests.get(0) != null) {
      var resGuestTypeProfileInfo = reservationGuests.get(0).getProfileInfo();

      // 1. Safely extract source code
      String sourceCode = null;
      var roomStay = getReservationResponse.getReservations().getReservation().get(0).getRoomStay();
      if (roomStay != null && roomStay.getRoomRates() != null && !roomStay.getRoomRates().isEmpty()) {
        sourceCode = roomStay.getRoomRates().get(0).getSourceCode();
      }
      updateProfileRequestDto.setThirdPartySourceCode(isThirdPartySourceCode(sourceCode));
      var profileModel = ohipUpdateProfileRequestMapper.toProfileModel(resGuestTypeProfileInfo,
          updateProfileRequestDto, hotelId);

      ohipProfileClient.updateProfile(profileModel, hotelId);
    } else {
      var ex = new HotelReservationException(ErrorCode.DIGITAL_NO_RESERV_ID_EXCEPTION,
          String.format("No reservation with given id:%s",
              reservationId));
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  /**
   * Checks if the provided source code is a valid third-party source code.
   *
   * @param sourceCode The code to validate
   * @return true if present, false otherwise
   */
  public static boolean isThirdPartySourceCode(String sourceCode) {
    if (sourceCode == null) {
      return false;
    }
    return THIRD_PARTY_SOURCE_CODES_SET.contains(sourceCode);
  }

  private CompanyProfile buildEmptyCompanyProfile() {
    var companyProfileWrapper = ohipProfileMapper.toCompanyProfileModel(
        CompanyWrapper
            .builder()
            .company(new Company())
            .build()
    );

    return companyProfileWrapper.getCompanyProfile();
  }
}

