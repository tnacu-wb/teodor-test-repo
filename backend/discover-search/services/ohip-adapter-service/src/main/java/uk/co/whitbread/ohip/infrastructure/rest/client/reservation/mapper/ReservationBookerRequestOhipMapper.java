package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
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
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PrivacyInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.PhoneTypeEnumDto;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public interface ReservationBookerRequestOhipMapper {

  String ID_TYPE_PASSPORT = "PASSPORT";
  String MASKED_PASSPORT_MARKER = "XXX";

  default Profile toDto(BookerDetails booker) {
    return toDto(booker, Optional.empty());
  }

  default Profile toDto(BookerDetails booker, Optional<StayingGuest> guest) {
    final var bookerNameType = new PersonNameType();
    final var bookerProfileType = new ProfileType();

    // name
    if (StringUtils.isNotBlank(booker.getTitle())) {
      bookerNameType.setNameTitle(booker.getTitle());
    }

    bookerNameType.setGivenName(booker.getFirstName());
    bookerNameType.setSurname(booker.getLastName());
    bookerNameType.setNameType(PersonNameTypeType.PRIMARY);

    // email
    if (StringUtils.isNotBlank(booker.getEmailAddress())) {
      final var emailType = new EmailType();
      final var emailInfoType = new EmailInfoType();
      final var companyProfileTypeEmails = new CompanyProfileTypeEmails();
      emailType.setEmailAddress(booker.getEmailAddress());
      emailInfoType.setEmail(emailType);
      companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
      bookerProfileType.setEmails(companyProfileTypeEmails);
    }

    // address
    if (booker.getAddress() != null) {
      final var addressType = new AddressType();
      final var addressInfoType = new AddressInfoType();
      final var profileTypeAddresses = new ProfileTypeAddresses();

      final var addressLines = Stream.of(
              booker.getAddress().getAddressLine1(),
              booker.getAddress().getAddressLine2(),
              booker.getAddress().getAddressLine3(),
              booker.getAddress().getAddressLine4())
          .map(addressLine -> Objects.isNull(addressLine) ? StringUtils.EMPTY : addressLine)
          .toList();

      addressType.setAddressLine(addressLines);
      addressType.setPostalCode(booker.getAddress().getPostalCode());
      addressType.setCityName(booker.getAddress().getCityName());
      if (booker.getAddress().getCountryCode() != null) {
        final var countryNameType = new CountryNameType();
        countryNameType.setValue(booker.getAddress().getCountryCode());
        addressType.setCountry(countryNameType);
      }
      if (booker.getAddress().getAddressType() != null) {
        addressType.setType(booker.getAddress().getAddressType());
      }
      addressType.setPrimaryInd(true);
      addressInfoType.setAddress(addressType);
      profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
      bookerProfileType.setAddresses(profileTypeAddresses);
    }

    final var profileTypeTelephones = new CompanyProfileTypeTelephones();
    final var telephones = new ArrayList<TelephoneInfoType>();

    // landline
    if (StringUtils.isNotBlank(booker.getLandline())) {
      final var landlineTelephoneInfoType = new TelephoneInfoType();
      final var landlineTelephoneType = new TelephoneType();
      landlineTelephoneType.setPhoneNumber(booker.getLandline());
      landlineTelephoneType.setPhoneUseType(PhoneTypeEnumDto.HOME.name());
      landlineTelephoneInfoType.setTelephone(landlineTelephoneType);
      telephones.add(landlineTelephoneInfoType);
    }

    // mobile phone
    if (StringUtils.isNotBlank(booker.getMobile())) {
      final var mobileTelephoneInfoType = new TelephoneInfoType();
      final var mobileTelephoneType = new TelephoneType();
      mobileTelephoneType.setPhoneNumber(booker.getMobile());
      mobileTelephoneType.setPhoneUseType(PhoneTypeEnumDto.MOBILE.name());
      mobileTelephoneInfoType.setTelephone(mobileTelephoneType);
      telephones.add(mobileTelephoneInfoType);
    }

    profileTypeTelephones.setTelephoneInfo(telephones);
    bookerProfileType.setTelephones(profileTypeTelephones);

    final var customerType = new CustomerType();
    customerType.setPersonName(List.of(bookerNameType));
    customerType.setLanguage(booker.getLanguage());

    // additional Information
    guest.map(StayingGuest::getStayingGuestDetails).map(StayingGuestDetails::getAdditionalDetails)
        .ifPresent(additionalDetails -> {
          Optional.ofNullable(additionalDetails.getDob())
              .ifPresent(customerType::setBirthDate);
          Optional.ofNullable(additionalDetails.getNationality())
              .filter(StringUtils::isNotEmpty)
              .ifPresent(customerType::setNationality);
          Optional.ofNullable(additionalDetails.getPassportNumber())
              .filter(StringUtils::isNotEmpty)
              .filter(passport -> !passport.contains(MASKED_PASSPORT_MARKER))
              .ifPresent(passportNumber -> {
                final var identificationType = new IdentificationType();
                final var identificationInfoType = new IdentificationInfoType();
                final var customerTypeIdentifications = new CustomerTypeIdentifications();
                identificationType.setIdNumber(passportNumber);
                identificationType.setIdType(ID_TYPE_PASSPORT);
                identificationInfoType.setIdentification(identificationType);
                customerTypeIdentifications.setIdentificationInfo(List.of(identificationInfoType));
                customerType.setIdentifications(customerTypeIdentifications);
              });
        });

    bookerProfileType.setProfileType(ProfileTypeType.CONTACT);
    bookerProfileType.setCustomer(customerType);

    // email updates and offers
    if (booker.getAcceptFutureMailing() != null) {
      final var privacyInfoType = new PrivacyInfoType();
      privacyInfoType.setOptInEmail(booker.getAcceptFutureMailing());
      bookerProfileType.setPrivacyInfo(privacyInfoType);
    }

    var bookerProfile = new Profile();
    bookerProfile.setProfileDetails(bookerProfileType);

    return bookerProfile;
  }
}
