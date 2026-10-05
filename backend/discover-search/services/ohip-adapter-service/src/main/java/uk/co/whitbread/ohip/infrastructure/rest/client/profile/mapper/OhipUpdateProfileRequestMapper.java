package uk.co.whitbread.ohip.infrastructure.rest.client.profile.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.profile.in.GuestDetails;
import uk.co.whitbread.ohip.domain.model.profile.in.KioskAddress;
import uk.co.whitbread.ohip.domain.model.profile.in.UpdateProfileRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.exceptions.EckohException;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class OhipUpdateProfileRequestMapper {

  static ProfileTypeAddresses injectAddresses(
      ResGuestTypeProfileInfo resGuestTypeProfileInfo,
      UpdateProfileRequest updateProfileRequest, GuestDetails guestDetails) {
    KioskAddress address;
    if (null != guestDetails) {
      address = guestDetails.getKioskAddress();
    } else {
      address = updateProfileRequest.getGuestDetails().get(0).getKioskAddress();
    }
    if (address == null) {
      return null;
    }

    var addressType = new AddressType();

    if (address.getAddressLine1() != null || address.getAddressLine2() != null
        || address.getAddressLine3() != null || address.getAddressLine4() != null) {
      addressType.setAddressLine(
          Stream.of(
                  address.getAddressLine1(),
                  address.getAddressLine2(),
                  address.getAddressLine3(),
                  address.getAddressLine4())
              .map(addressLine -> Objects.isNull(addressLine) ? StringUtils.EMPTY : addressLine)
              .toList()
      );
      addressType.setType("HOME");
      addressType.setCityName(address.getCityName());
    }

    if (address.getCountry() != null
        && !address.getCountry().isEmpty()
        && address.getCountry().trim().length() >= 2) {
      var countryNameType = new CountryNameType();
      countryNameType.setValue(address.getCountry().trim().substring(0, 2));
      addressType.setCountry(countryNameType);
    }

    if (address.getPostalCode() != null) {
      addressType.setPostalCode(address.getPostalCode());
      addressType.setType("HOME");
      addressType.setCityName(address.getCityName());
    }

    AddressInfoType addressInfoType = new AddressInfoType();
    addressInfoType.setAddress(addressType);
    if (null != resGuestTypeProfileInfo) {
      var addresses = resGuestTypeProfileInfo.getProfile().getAddresses();
      if (addresses.getAddressInfo().get(0).getAddress() != null && !updateProfileRequest.isThirdPartySourceCode()) {
        addressInfoType.setId(addresses.getAddressInfo().get(0).getId());
        addressInfoType.setType(addresses.getAddressInfo().get(0).getType());
      }
    }

    ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));

    return profileTypeAddresses;
  }

  @Mapping(target = "profileIdList", expression = "java(injectProfileIdList(resGuestTypeProfileInfo))")
  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(resGuestTypeProfileInfo, updateProfileRequest, hotelId))")
  public abstract Profile toProfileModel(ResGuestTypeProfileInfo resGuestTypeProfileInfo,
      UpdateProfileRequest updateProfileRequest, String hotelId);

  protected List<UniqueIDType> injectProfileIdList(
      ResGuestTypeProfileInfo resGuestTypeProfileInfo) {
    if (resGuestTypeProfileInfo.getProfileIdList() == null) {
      return new ArrayList<>();
    }
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId(resGuestTypeProfileInfo.getProfileIdList().get(0).getId());
    uniqueIdType.setType(resGuestTypeProfileInfo.getProfileIdList().get(0).getType());
    return List.of(uniqueIdType);
  }

  protected ProfileType injectProfileDetails(ResGuestTypeProfileInfo resGuestTypeProfileInfo,
      UpdateProfileRequest updateProfileRequest, String hotelId) {

    var profileDetails = new ProfileType();
    profileDetails.setCustomer(injectCustomer(updateProfileRequest));
    profileDetails.setTelephones(
        injectTelephones(resGuestTypeProfileInfo, updateProfileRequest));
    profileDetails.setEmails(injectEmails(resGuestTypeProfileInfo, updateProfileRequest));
    profileDetails.setAddresses(
        injectAddresses(resGuestTypeProfileInfo, updateProfileRequest, null));
    profileDetails.setRegisteredProperty(hotelId);
    return profileDetails;
  }

  private CompanyProfileTypeEmails injectEmails(ResGuestTypeProfileInfo resGuestTypeProfileInfo,
      UpdateProfileRequest updateProfileRequest) {
    var email = updateProfileRequest.getGuestDetails().get(0).getEmailAddress();
    if (email == null) {
      return null;
    }

    var emailType = new EmailType();
    emailType.setEmailAddress(email);
    emailType.setType("EMAIL");
    EmailInfoType emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);

    var profileTypeEmails = resGuestTypeProfileInfo.getProfile().getEmails();
    if (profileTypeEmails != null && !updateProfileRequest.isThirdPartySourceCode()) {
      emailInfoType.setId(profileTypeEmails.getEmailInfo().get(0).getId());
      emailInfoType.setType(profileTypeEmails.getEmailInfo().get(0).getType());
    }
    CompanyProfileTypeEmails companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
    return companyProfileTypeEmails;
  }

  private CompanyProfileTypeTelephones injectTelephones(
      ResGuestTypeProfileInfo resGuestTypeProfileInfo,
      UpdateProfileRequest updateProfileRequest) {
    var profileTelephones = resGuestTypeProfileInfo.getProfile().getTelephones();
    final var telephones = new ArrayList<TelephoneInfoType>();
    final var telephoneType = new TelephoneType();
    var guestDetails = updateProfileRequest.getGuestDetails().get(0);

    if (StringUtils.isNotBlank(guestDetails.getPhoneNumber())) {
      if (profileTelephones != null) {
        var telephoneInfoTypeList = profileTelephones.getTelephoneInfo();
        uk.co.whitbread.hotel.ohip.adapter.generated.models.TelephoneInfoType telephoneInfoType2 =
            telephoneInfoTypeList.stream()
                .filter(telephoneInfoType1 -> telephoneInfoType1.getTelephone().getPrimaryInd())
                .findFirst()
                .orElseThrow(() -> new EckohException(ErrorCode.DIGITAL_GET_TELEPHONE_EXCEPTION,
                    "Unable to get telephone info."));

        var telephoneInfoType = new TelephoneInfoType();
        if (!updateProfileRequest.isThirdPartySourceCode()) {
          telephoneInfoType.setType(telephoneInfoType2.getType());
          telephoneInfoType.setId(telephoneInfoType2.getId());
        }
        telephoneType.setPhoneTechType("PHONE");
        telephoneType.setPhoneUseType("MOBILE");
        telephoneType.setPrimaryInd(!updateProfileRequest.isThirdPartySourceCode());
        telephoneType.setPhoneNumber(guestDetails.getPhoneNumber());

        telephoneInfoType.setTelephone(telephoneType);
        telephones.add(telephoneInfoType);
      }
    }
    final var profileTypeTelephones = new CompanyProfileTypeTelephones();
    profileTypeTelephones.setTelephoneInfo(telephones);

    return profileTypeTelephones;
  }

  private CustomerType injectCustomer(UpdateProfileRequest updateProfileRequest) {

    var guestDetails = updateProfileRequest.getGuestDetails().get(0);
    final var profileNameType = new PersonNameType();

    //personName
    if (StringUtils.isNotBlank(guestDetails.getGivenName())) {
      profileNameType.setGivenName(guestDetails.getGivenName());
    }
    if (StringUtils.isNotBlank(guestDetails.getSurname())) {
      profileNameType.setSurname(guestDetails.getSurname());
    }
    if (StringUtils.isNotBlank(guestDetails.getNameTitle())) {
      profileNameType.setNameTitle(guestDetails.getNameTitle());
    }
    if (StringUtils.isNotBlank(guestDetails.getNameType())) {
      profileNameType.setNameType(PersonNameTypeType.PRIMARY);
    }

    var passportDetails = guestDetails.getPassportDetails();
    var identificationType = new IdentificationType();
    //Identifications
    if (StringUtils.isNotBlank(passportDetails.getPassportNumber())) {
      identificationType.setIdType("PASSPORT");
      identificationType.setIdNumber(passportDetails.getPassportNumber());
    }
    if (StringUtils.isNotBlank(passportDetails.getPlaceOfIssue())) {
      identificationType.setIssuedPlace(passportDetails.getPlaceOfIssue());
    }

    var identificationInfoType = new IdentificationInfoType();
    identificationInfoType.setIdentification(identificationType);

    var customerTypeIdentifications = new CustomerTypeIdentifications();
    customerTypeIdentifications.setIdentificationInfo(List.of(identificationInfoType));

    var customerType = new CustomerType();
    customerType.setPersonName(List.of(profileNameType));
    customerType.setLanguage("E");
    customerType.setPrivateProfile(false);
    customerType.setIdentifications(customerTypeIdentifications);
    if (StringUtils.isNotBlank(guestDetails.getNationality())) {
      customerType.setNationality(guestDetails.getNationality());
    } else {
      customerType.setNationality("GB");
    }

    return customerType;
  }

}
