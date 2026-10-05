package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

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
import org.modelmapper.ModelMapper;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CommonAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.AddressTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.EmailTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.NameTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.out.TelephoneTypeResponse;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.PhoneTypeEnumDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class BookerProfileOhipMapper {

  private final ModelMapper mapper = new ModelMapper();

  private static ProfileTypeAddresses getProfileTypeAddresses(ProfileType profileType,
      CommonAddress address) {
    if (address == null) {
      return null;
    }

    var addressType = new AddressType();

    String profileAddType = null;
    String cityName = null;
    String id = null;

    if (null != profileType.getAddress()) {
      profileAddType = profileType.getAddress().getType();
      cityName = profileType.getAddress().getCityName();
      id = profileType.getAddress().getId();
    } else {
      profileAddType = address.getAddressType();
      cityName = address.getCityName();
    }
    addressType.setType(profileAddType);
    addressType.setCityName(cityName);

    if (address.getPostalCode() != null) {
      addressType.setPostalCode(address.getPostalCode());
      addressType.setType(profileAddType);
      addressType.setCityName(cityName);
    }

    if (addressType.getCityName() != null && addressType.getCityName().equals(address.getAddressLine4())) {
      addressType.setAddressLine(
              Stream.of(
                              address.getAddressLine1(),
                              address.getAddressLine2(),
                              address.getAddressLine3())
                      .filter(StringUtils::isNotBlank)
                      .toList()
      );
    } else {
      addressType.setAddressLine(
              Stream.of(
                              address.getAddressLine1(),
                              address.getAddressLine2(),
                              address.getAddressLine3(),
                              address.getAddressLine4())
                      .filter(StringUtils::isNotBlank)
                      .toList()
      );
    }
    AddressInfoType addressInfoType = new AddressInfoType();
    addressInfoType.setAddress(addressType);
    addressInfoType.setId(id);
    addressInfoType.setType(profileAddType);

    ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    return profileTypeAddresses;
  }

  @Mapping(target = "profileId", expression = "java(injectProfileId(profile))")
  @Mapping(target = "address", expression = "java(injectAddress(profile))")
  @Mapping(target = "email", expression = "java(injectEmail(profile))")
  @Mapping(target = "telephones", expression = "java(injectProfileTelephones(profile))")
  @Mapping(target = "name", expression = "java(injectName(profile))")
  public abstract ProfileType toModel(Profile profile);

  @Mapping(target = "profileIdList", expression = "java(injectProfileIdList(profileType))")
  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(profileType, bookerDetailsCnpRequest))")
  public abstract Profile toDto(ProfileType profileType,
      BookerDetailsCnpRequest bookerDetailsCnpRequest);

  @Mapping(target = "profileIdList", expression = "java(injectProfileIdList(profileType))")
  @Mapping(target = "profileDetails",
      expression = "java(injectProfileDetails(profileType, billingAddressRequest))")
  public abstract Profile toBillingDto(ProfileType profileType,
      BillingAddressRequest billingAddressRequest);

  protected uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType injectProfileDetails(
      ProfileType profileType,
      BookerDetailsCnpRequest bookerDetailsCnpRequest) {

    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileDetails.setAddresses(injectAddresses(profileType, bookerDetailsCnpRequest));
    profileDetails.setEmails(injectEmails(profileType, bookerDetailsCnpRequest));
    profileDetails.setCustomer(injectCustomer(profileType, bookerDetailsCnpRequest));
    profileDetails.setTelephones(injectTelephones(profileType, bookerDetailsCnpRequest));

    return profileDetails;
  }

  protected uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType injectProfileDetails(
      ProfileType profileType,
      BillingAddressRequest billingAddressRequest) {

    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileDetails.setAddresses(injectAddresses(profileType, billingAddressRequest));
    return profileDetails;
  }

  private CompanyProfileTypeTelephones injectTelephones(ProfileType profileType,
      BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    final var booker = bookerDetailsCnpRequest.getBooker();
    final var profileTypeTelephones = new CompanyProfileTypeTelephones();
    final var telephones = new ArrayList<TelephoneInfoType>();

    // landline
    if (StringUtils.isNotBlank(booker.getLandline())) {
      final var landlineTelephoneInfoType = new TelephoneInfoType();
      final var landlineTelephoneType = new TelephoneType();
      if (profileType.getTelephones() != null) {
        var existingPhone = profileType.getTelephones().stream()
            .filter(t -> PhoneTypeEnumDto.HOME.name().equals(t.getType())).findFirst();
        landlineTelephoneInfoType.setId(
            existingPhone.map(TelephoneTypeResponse::getId).orElse(null));
        landlineTelephoneInfoType.setType(
            existingPhone.map(TelephoneTypeResponse::getType).orElse(null));
      }
      landlineTelephoneType.setPhoneNumber(booker.getLandline());
      landlineTelephoneType.setPhoneUseType(PhoneTypeEnumDto.HOME.name());

      landlineTelephoneInfoType.setTelephone(landlineTelephoneType);
      telephones.add(landlineTelephoneInfoType);
    }

    // mobile phone
    if (StringUtils.isNotBlank(booker.getMobile())) {
      final var mobileTelephoneInfoType = new TelephoneInfoType();
      final var mobileTelephoneType = new TelephoneType();
      if (profileType.getTelephones() != null) {
        var existingPhone = profileType.getTelephones().stream()
            .filter(t -> PhoneTypeEnumDto.MOBILE.name().equals(t.getType())).findFirst();
        mobileTelephoneInfoType.setId(existingPhone.map(TelephoneTypeResponse::getId).orElse(null));
        mobileTelephoneInfoType.setType(
            existingPhone.map(TelephoneTypeResponse::getType).orElse(null));
      }
      mobileTelephoneType.setPhoneNumber(booker.getMobile());
      mobileTelephoneType.setPhoneUseType(PhoneTypeEnumDto.MOBILE.name());

      mobileTelephoneInfoType.setTelephone(mobileTelephoneType);
      telephones.add(mobileTelephoneInfoType);
    }

    profileTypeTelephones.setTelephoneInfo(telephones);

    return profileTypeTelephones;
  }

  private CustomerType injectCustomer(ProfileType profileType, BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    var booker = bookerDetailsCnpRequest.getBooker();
    final var bookerNameType = new PersonNameType();

    if (profileType.getName() != null) {
      bookerNameType.setNameTitle(profileType.getName().getTitle());
      bookerNameType.setGivenName(profileType.getName().getFirstName());
      bookerNameType.setSurname(profileType.getName().getLastName());
    }

    // name
    if (StringUtils.isNotBlank(booker.getTitle())) {
      bookerNameType.setNameTitle(booker.getTitle());
    }

    if (StringUtils.isNotBlank(booker.getFirstName())) {
      bookerNameType.setGivenName(booker.getFirstName());
    }

    if (StringUtils.isNotBlank(booker.getLastName())) {
      bookerNameType.setSurname(booker.getLastName());
    }

    bookerNameType.setNameType(PersonNameTypeType.PRIMARY);

    final var customerType = new CustomerType();
    customerType.setPersonName(List.of(bookerNameType));

    return customerType;
  }

  protected ProfileTypeAddresses injectAddresses(ProfileType profileType,
      BookerDetailsCnpRequest bookerDetailsCnpRequest) {

    var address = mapper
        .map(bookerDetailsCnpRequest.getBooker().getAddress(), CommonAddress.class);

    return getProfileTypeAddresses(profileType, address);
  }

  protected ProfileTypeAddresses injectAddresses(ProfileType profileType,
      BillingAddressRequest billingAddressRequest) {
    var address = mapper
        .map(billingAddressRequest.getBooker().getAddress(), CommonAddress.class);

    return getProfileTypeAddresses(profileType, address);
  }

  protected CompanyProfileTypeEmails injectEmails(ProfileType profileType,
      BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    var email = bookerDetailsCnpRequest.getBooker().getEmailAddress();
    if (email == null) {
      return null;
    }
    var emailType = new EmailType();
    emailType.setEmailAddress(email);

    EmailInfoType emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);
    if (Objects.nonNull(profileType.getEmail())) {
      emailInfoType.setId(profileType.getEmail().getId());
      emailInfoType.setType(profileType.getEmail().getType());
    }
    CompanyProfileTypeEmails companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
    return companyProfileTypeEmails;
  }

  protected List<UniqueIDType> injectProfileIdList(ProfileType profileType) {
    if (profileType.getProfileId() == null) {
      return new ArrayList<>();
    }
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId(profileType.getProfileId().getId());
    uniqueIdType.setType(profileType.getProfileId().getType());
    return List.of(uniqueIdType);
  }

  protected ProfileIdResponse injectProfileId(Profile profile) {
    return profile.getProfileIdList()
        .stream()
        .findFirst()
        .map(uniqueIDType -> ProfileIdResponse
            .builder()
            .id(uniqueIDType.getId())
            .type(uniqueIDType.getType())
            .build()
        ).orElse(null);
  }

  protected AddressTypeResponse injectAddress(Profile profile) {
    return profile.getProfileDetails()
        .getAddresses()
        .getAddressInfo() ==  null ? null : profile.getProfileDetails()
          .getAddresses()
          .getAddressInfo()
          .stream().findFirst()
          .map(addressInfoType -> AddressTypeResponse
              .builder()
              .id(addressInfoType.getId())
              .type(addressInfoType.getType() == null ? addressInfoType.getAddress().getType()
                  : addressInfoType.getType())
              .cityName(addressInfoType.getAddress().getCityName())
              .build())
          .orElse(null);
  }

  protected EmailTypeResponse injectEmail(Profile profile) {
    return profile.getProfileDetails()
        .getEmails()
        .getEmailInfo() ==  null ? null : profile.getProfileDetails()
          .getEmails()
          .getEmailInfo()
          .stream()
          .findFirst()
          .map(emailInfoType -> EmailTypeResponse
              .builder()
              .id(emailInfoType.getId())
              .type(emailInfoType.getType() == null ? emailInfoType.getEmail().getType()
                  : emailInfoType.getType())
            .build()
          ).orElse(null);
  }

  protected List<TelephoneTypeResponse> injectProfileTelephones(Profile profile) {
    if (profile.getProfileDetails().getTelephones() == null) {
      return null;
    }

    return profile.getProfileDetails()
        .getTelephones()
        .getTelephoneInfo() ==  null ? null : profile.getProfileDetails()
        .getTelephones()
        .getTelephoneInfo()
        .stream()
        .map(telephoneInfoType -> TelephoneTypeResponse
            .builder()
            .id(telephoneInfoType.getId())
            .type(telephoneInfoType.getType() == null ? telephoneInfoType.getTelephone().getPhoneUseType()
                : telephoneInfoType.getType())
            .build()
        ).toList();
  }

  protected NameTypeResponse injectName(Profile profile) {
    var nameList = profile.getProfileDetails().getCustomer() == null
        ? null : profile.getProfileDetails().getCustomer().getPersonName();

    if (nameList == null || nameList.isEmpty()) {
      return null;
    }

    return NameTypeResponse.builder()
        .firstName(nameList.get(0).getGivenName())
        .lastName(nameList.get(0).getSurname())
        .title(nameList.get(0).getNameTitle())
        .build();
  }
}
