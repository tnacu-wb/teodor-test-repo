package uk.co.whitbread.ohip.domain.logic.utils;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EMPTY_STR;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOME_CONSTANT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_ENGLISH;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_GERMAN;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import uk.co.whitbread.ohip.domain.model.profile.in.Country;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AddressType;
import uk.co.whitbread.ohip.domain.model.reservation.in.CustomerType;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.EmailType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameType;
import uk.co.whitbread.ohip.domain.model.reservation.in.PersonNameTypeType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileInfo;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeAddresses;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeEmails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileTypeTelephones;
import uk.co.whitbread.ohip.domain.model.reservation.in.TelephoneInfoType;
import uk.co.whitbread.ohip.domain.model.reservation.in.TelephoneType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;

public final class GuestInfoUtils {

  private GuestInfoUtils() {
  }

  public static ProfileInfo toProfileType(ReservationGuest reservationGuest,
      ReservationBooker reservationBooker, String language) {
    return ProfileInfo.builder()
        .profile(getProfileInfo(reservationGuest, reservationBooker, language))
        .build();
  }

  private static ProfileType getProfileInfo(ReservationGuest reservationGuest,
      ReservationBooker reservationBooker, String language) {

    var profileType = new ProfileType();
    profileType.setCustomer(createCustomerType(reservationGuest));

    if (reservationGuest.getEmail() != null) {
      profileType.setEmails(createEmailInfo(reservationGuest));
    }
    if (reservationGuest.getPhoneNumber() != null) {
      profileType.setTelephones(createPhoneNumberInfo(reservationGuest));
    }

    if (reservationBooker != null && reservationBooker.getProfileId() != null) {
      profileType.setAddresses(createAddress(reservationBooker.getAddress()));
      profileType.getCustomer().getPersonName().stream().findFirst()
          .ifPresent(personNameType -> personNameType.setLanguage(getLanguageCode(language)));
    }

    Optional.ofNullable(reservationGuest.getAddress())
        .ifPresent(guestAddress -> profileType.setAddresses(createGuestAddress(reservationGuest)));

    return profileType;
  }

  private static String getLanguageCode(String language) {
    if (language == null) {
      return LANGUAGE_ENGLISH;
    }
    switch (language.toUpperCase()) {
      case "EN":
        return LANGUAGE_ENGLISH;
      case "E":
        return LANGUAGE_ENGLISH;
      case "DE":
        return LANGUAGE_GERMAN;
      case "D":
        return LANGUAGE_GERMAN;
      default:
        return LANGUAGE_ENGLISH;
    }
  }

  private static ProfileTypeAddresses createGuestAddress(ReservationGuest reservationGuest) {
    final var guestAddress = reservationGuest.getAddress();
    final var addressType = AddressType.builder()
        .addressLine(List.of(
            Optional.ofNullable(guestAddress.getAddressLine1()).orElse(EMPTY_STR),
            Optional.ofNullable(guestAddress.getAddressLine2()).orElse(EMPTY_STR),
            Optional.ofNullable(guestAddress.getAddressLine3()).orElse(EMPTY_STR),
            Optional.ofNullable(guestAddress.getAddressLine4()).orElse(EMPTY_STR))
        )
        .cityName(guestAddress.getCityName())
        .postalCode(guestAddress.getPostalCode())
        .country(new Country(guestAddress.getCountryCode()))
        .language(reservationGuest.getLanguage())
        .type(Optional.ofNullable(guestAddress.getAddressType()).orElse(HOME_CONSTANT))
        .build();
    final var addressInfoType = AddressInfoType.builder()
        .address(addressType)
        .build();

    return ProfileTypeAddresses.builder()
        .addressInfo(List.of(addressInfoType))
        .build();
  }

  private static CustomerType createCustomerType(ReservationGuest reservationGuest) {
    return CustomerType.builder()
        .personName(Arrays.asList(createPersonInformation(reservationGuest)))
        .birthDate(reservationGuest.getBirthDate())
        .build();
  }

  private static PersonNameType createPersonInformation(ReservationGuest reservationGuest) {
    return PersonNameType.builder()
        .givenName(reservationGuest.getGivenName())
        .surname(reservationGuest.getSurname())
        .nameTitle(reservationGuest.getNameTitle())
        .language(reservationGuest.getLanguage())
        .nameType(PersonNameTypeType.fromValue(reservationGuest.getType()))
        .email(reservationGuest.getEmail())
        .build();
  }

  private static ProfileTypeTelephones createPhoneNumberInfo(ReservationGuest reservationGuest) {
    var telephoneInfoType = TelephoneInfoType.builder()
        .telephone(TelephoneType.builder()
            .primaryInd(true)
            .phoneNumber(reservationGuest.getPhoneNumber())
            .build())
        .type("HOME")
        .build();
    return ProfileTypeTelephones.builder()
            .telephoneInfo(Arrays.asList(telephoneInfoType))
        .build();
  }

  private static ProfileTypeAddresses createAddress(ReservationBookerAddress reservationBookerAddress) {
    var addressInfoType = AddressInfoType.builder()
            .address(AddressType.builder()
                    .primaryInd(true)
                    .addressLine(Arrays.asList(reservationBookerAddress.getAddressLine1(),
                            reservationBookerAddress.getAddressLine2(),
                            reservationBookerAddress.getAddressLine3()))
                    .cityName(reservationBookerAddress.getCityName())
                    .postalCode(reservationBookerAddress.getPostalCode())
                    .type(reservationBookerAddress.getAddressType())
                    .build())
                       .build();
    return ProfileTypeAddresses.builder()
            .addressInfo(Arrays.asList(addressInfoType))
            .build();
  }

  private static ProfileTypeEmails createEmailInfo(ReservationGuest reservationGuest) {
    var emailInfoType = EmailInfoType.builder()
            .email(EmailType.builder()
                .type("EMAIL")
                .emailAddress(reservationGuest.getEmail())
                .build())
        .build();
    return ProfileTypeEmails.builder()
        .emailInfo(Arrays.asList(emailInfoType))
        .build();
  }

}
