package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public interface ReservationAccompanyingGuestProfileRequestOhipMapper {

  String PASSPORT = "PASSPORT";
  String DOCUMENT_ID = "DocumentId";
  String MASKED_PASSPORT_MARKER = "XXX";

  default Profile toDto(StayingGuest stayingGuest, Profile profileDetails) {
    if (stayingGuest == null || stayingGuest.getIsAccompanyingGuest() == null) {
      return null;
    }

    StayingGuestDetails details = stayingGuest.getStayingGuestDetails();
    if (details == null) {
      return null;
    }
    var profileObj = determineProfileAction(details, profileDetails);
    return ObjectUtils.isNotEmpty(profileObj) ? profileObj : null;
  }

  private Profile determineProfileAction(StayingGuestDetails details,
      Profile profileDetails) {

    var isExistingProfile = ObjectUtils.isNotEmpty(profileDetails)
        && ObjectUtils.isNotEmpty(profileDetails.getProfileDetails());

    return isExistingProfile
        ? updateProfile(details, profileDetails)
        : createNewProfile(details);
  }

  private Profile updateProfile(StayingGuestDetails details, Profile profileDetails) {
    if (ObjectUtils.isNotEmpty(profileDetails)) {
      profileDetails.setProfileDetails(createOrUpdateProfileType(details, profileDetails));
      return profileDetails;
    }
    return null;
  }

  private Profile createNewProfile(StayingGuestDetails details) {
    Profile profile = new Profile();
    profile.setProfileDetails(createOrUpdateProfileType(details, null));
    return profile;
  }

  private ProfileType createOrUpdateProfileType(StayingGuestDetails details,
      Profile profileDetails) {
    ProfileType profileType = new ProfileType();
    profileType.setProfileType(ProfileTypeType.GUEST);
    profileType.setCustomer(createOrUpdateCustomer(details, profileDetails));

    if (details.getAddress() != null) {
      profileType.setAddresses(createOrUpdateAddress(details, profileDetails));
    }

    return profileType;
  }

  private CustomerType createOrUpdateCustomer(StayingGuestDetails details, Profile profileDetails) {
    CustomerType customer = new CustomerType();
    customer.setPersonName(setPersonName(details));
    Optional.ofNullable(details.getAdditionalDetails()).ifPresent(additionalDetails -> {
      Optional.ofNullable(additionalDetails.getDob()).ifPresent(customer::setBirthDate);
      Optional.ofNullable(additionalDetails.getNationality()).ifPresent(customer::setNationality);
      Optional.ofNullable(additionalDetails.getPassportNumber())
          .filter(passport -> !passport.contains(MASKED_PASSPORT_MARKER))
          .ifPresent(passportNumber -> customer.setIdentifications(
              setIdentification(passportNumber, profileDetails)));
    });

    return customer;
  }

  private List<PersonNameType> setPersonName(StayingGuestDetails details) {
    PersonNameType nameType = new PersonNameType();
    Optional.ofNullable(details.getTitle()).ifPresent(nameType::setNameTitle);
    nameType.setGivenName(details.getFirstName());
    nameType.setSurname(details.getLastName());
    nameType.setNameType(PersonNameTypeType.PRIMARY);

    return List.of(nameType);
  }

  private ProfileTypeAddresses createOrUpdateAddress(StayingGuestDetails details, Profile profileDetails) {
    if (details == null || details.getAddress() == null) {
      throw new IllegalArgumentException("Details and Address cannot be null");
    }

    StayingGuestAddress address = details.getAddress();
    AddressInfoType addressInfoType = new AddressInfoType();
    AddressType addressType = new AddressType();

    String existingAddressId = address.getAddressId();
    if (StringUtils.isNotEmpty(existingAddressId) && Objects.nonNull(profileDetails)
        && Objects.nonNull(profileDetails.getProfileDetails())) {
      var profileType = profileDetails.getProfileDetails();
      var homeAddressOpt = Optional.ofNullable(profileType)
              .map(ProfileType::getAddresses)
              .map(ProfileTypeAddresses::getAddressInfo)
              .orElseGet(Collections::emptyList)
              .stream()
              .filter(addressInfo -> existingAddressId.equals(addressInfo.getId()))
              .findFirst();

      if (homeAddressOpt.isEmpty()) {
        throw new IllegalArgumentException("No existing address found for ID: " + existingAddressId);
      }
      addressType = homeAddressOpt.get().getAddress();
      updateAddressFields(addressType, address);
      addressInfoType.setId(existingAddressId);
      addressInfoType.setType(address.getAddressType());
    } else {
      updateAddressFields(addressType, address);
      addressType.setPrimaryInd(true);
    }
    addressType.setAddressLine(createAddressLines(details));
    addressInfoType.setAddress(addressType);

    ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));

    return profileTypeAddresses;
  }

  private void updateAddressFields(AddressType addressType, StayingGuestAddress address) {
    addressType.setPostalCode(address.getPostalCode());
    addressType.setCityName(address.getCityName());
    Optional.ofNullable(address.getCountryCode()).ifPresent(code -> {
      CountryNameType country = new CountryNameType();
      country.setValue(code);
      addressType.setCountry(country);
    });
    Optional.ofNullable(address.getAddressType()).ifPresent(addressType::setType);
  }

  private List<String> createAddressLines(StayingGuestDetails details) {
    return Stream.of(details.getAddress().getAddressLine1(), details.getAddress().getAddressLine2(),
            details.getAddress().getAddressLine3(), details.getAddress().getAddressLine4())
        .map(line -> Optional.ofNullable(line).orElse(StringUtils.EMPTY))
        .toList();
  }

  private CustomerTypeIdentifications setIdentification(String passportNumber,
      Profile profileDetails) {
    IdentificationType identification = new IdentificationType();
    identification.setIdType(PASSPORT);
    identification.setIdNumber(passportNumber);
    identification.setPrimaryInd(Boolean.TRUE);

    IdentificationInfoType identificationInfo = new IdentificationInfoType();
    identificationInfo.setIdentification(identification);

    if (ObjectUtils.isNotEmpty(profileDetails)) {
      identificationInfo.setId(getPassportId(profileDetails));
      identificationInfo.setType(DOCUMENT_ID);
    }

    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    identifications.setIdentificationInfo(List.of(identificationInfo));

    return identifications;
  }

  private String getPassportId(Profile profileDetails) {
    return Optional.ofNullable(profileDetails)
        .map(Profile::getProfileDetails)
        .map(ProfileType::getCustomer)
        .map(CustomerType::getIdentifications)
        .map(CustomerTypeIdentifications::getIdentificationInfo)
        .flatMap(identifications -> identifications.stream()
            .filter(info -> PASSPORT.equals(info.getIdentification().getIdType())
                && Boolean.TRUE.equals(info.getIdentification().getPrimaryInd()))
            .map(IdentificationInfoType::getId).findFirst())
        .orElse(null);
  }
}
