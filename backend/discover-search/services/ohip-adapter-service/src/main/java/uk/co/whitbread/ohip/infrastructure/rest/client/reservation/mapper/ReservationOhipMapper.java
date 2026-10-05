package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EMAIL_CONSTANT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EMPTY_STR;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.HOME_CONSTANT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_ENGLISH;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_GERMAN;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SPECIALS_DESCRIPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.SPECIALS_TYPE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.TEMP_PROFILE_SURNAME;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_09;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_10;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_13;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_15;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_35;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFN_01;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ExternalReferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.FormattedTextTextType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.NumericUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PersonNameTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuaranteeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.LeadGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.Reservation;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {
    RoomRateOhipMapper.class, ScheduleListOhipMapper.class},
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class ReservationOhipMapper {

  private ReservationOhipProperties reservationOhipProperties;
  private UnleashWrapper<FeatureFlag> unleashWrapper;

  @Autowired
  public final void toReservationOhipPropertiesForModel(
      ReservationOhipProperties reservationOhipProperties,
      UnleashWrapper<FeatureFlag> unleashWrapper) {
    this.reservationOhipProperties = reservationOhipProperties;
    this.unleashWrapper = unleashWrapper;
  }

  @Mapping(source = "arrival", target = "roomStay.arrivalDate")
  @Mapping(source = "departure", target = "roomStay.departureDate")
  @Mapping(expression = "java(injectProfileDetails(reservation))", target = "reservationGuests")
  @Mapping(expression = "java(injectReservationProfiles(reservation))", target = "reservationProfiles")
  @Mapping(expression = "java(injectExternalReferenceIds(reservation))", target = "externalReferences")
  @Mapping(expression = "java(injectGuaranteeCode())", target = "roomStay.guarantee")
  @Mapping(expression = "java(injectPaymentMethod())", target = "reservationPaymentMethods")
  @Mapping(expression = "java(injectPreferenceCollection(reservation))", target = "preferenceCollection")
  @Mapping(source = "adults", target = "roomStay.guestCounts.adults")
  @Mapping(source = "children", target = "roomStay.guestCounts.children")
  @Mapping(expression = "java(injectUserDefinedFields(reservation))", target = "userDefinedFields")
  @Mapping(expression = "java(injectComments(reservation))", target = "comments")
  @Mapping(source = "roomRates.promotionCode", target = "roomStay.promotion.promotionCode")
  abstract HotelReservationType fromDto(Reservation reservation);


  protected List<PreferenceTypeType> injectPreferenceCollection(Reservation reservation) {
    List<String> specialRequests = getCompleteSpecialRequests(reservation);
    if (Objects.isNull(specialRequests) || specialRequests.isEmpty()) {
      return Collections.emptyList();
    }
    return createPreferenceCollection(specialRequests);
  }

  private List<String> getCompleteSpecialRequests(Reservation reservation) {
    List<String> specialRequests = reservation.getRoomRates().getSpecialRequests();
    if (Boolean.TRUE.equals(reservation.getCotRequired())) {
      if (Objects.isNull(specialRequests)) {
        specialRequests = new ArrayList<>(2);
      }
      specialRequests.add(reservationOhipProperties.getCotSpecialRequest());
    }
    return specialRequests;
  }

  private List<PreferenceTypeType> createPreferenceCollection(List<String> specialRequests) {
    final var preferenceType = new PreferenceTypeType();
    preferenceType.setPreference(createPreferences(specialRequests));
    preferenceType.setPreferenceType(SPECIALS_TYPE);
    preferenceType.setPreferenceTypeDescription(SPECIALS_DESCRIPTION);
    return List.of(preferenceType);
  }

  private List<PreferenceType> createPreferences(List<String> specialRequest) {
    return specialRequest.stream()
        .filter(Objects::nonNull)
        .map(this::createPreferenceType)
        .toList();
  }

  private PreferenceType createPreferenceType(String specialRequest) {
    final var preference = new PreferenceType();
    preference.setPreferenceValue(specialRequest);
    return preference;
  }

  protected List<ExternalReferenceType> injectExternalReferenceIds(Reservation reservation) {
    final var externalReferenceTypes = new ArrayList<ExternalReferenceType>();
    final var externalReferenceType = new ExternalReferenceType();

    externalReferenceType.setId(reservation.getExternalReferenceId());
    externalReferenceType.setIdContext(reservationOhipProperties.getContextId());

    externalReferenceTypes.add(externalReferenceType);

    if (StringUtils.isNotBlank(reservation.getGdsReferenceNumber())) {
      final var extReferenceType = new ExternalReferenceType();
      extReferenceType.setId(reservation.getGdsReferenceNumber());

      extReferenceType.setIdContext(
          switch (reservation.getSourceCode()) {
            case "38" -> reservationOhipProperties.getAmadeusDistributionContextId();
            case "43" -> reservationOhipProperties.getTravelPortDistributionContextId();
            default -> "Unknown contextId for source code " + reservation.getSourceCode();
          });

      externalReferenceTypes.add(extReferenceType);
    }

    return externalReferenceTypes;

  }

  protected List<ResGuestType> injectProfileDetails(Reservation reservation) {

    final PersonNameType personName = createPersonName(reservation.getLeadGuest());
    final ProfileType profile = createProfile(personName);

    if (reservation.getLeadGuest() != null
        && !StringUtils.isBlank(reservation.getLeadGuest().getEmailAddress())) {
      final var profileTypeEmails = createEmailInformation(reservation.getLeadGuest());
      profile.setEmails(profileTypeEmails);
      if (reservation.getLeadGuest().getAddress() != null) {
        final var profileTypeAddresses = createAddressInformation(reservation.getLeadGuest());
        profile.setAddresses(profileTypeAddresses);
      }
    }

    return List.of(createGuestType(profile));
  }

  protected HotelReservationTypeReservationProfiles injectReservationProfiles(Reservation reservation) {
    HotelReservationTypeReservationProfiles hotelReservationTypeReservationProfiles =
            new HotelReservationTypeReservationProfiles();

    if (!StringUtils.isBlank(reservation.getOperaCompanyId())) {
      List<ReservationProfileType> reservationProfiles = createReservationProfiles(reservation);
      hotelReservationTypeReservationProfiles.setReservationProfile(reservationProfiles);
      return hotelReservationTypeReservationProfiles;
    } else {
      return null;
    }
  }

  protected List<ReservationProfileType> createReservationProfiles(Reservation reservation) {
    ReservationProfileType reservationProfileType = new ReservationProfileType();
    UniqueIDType uniqueIDType = new UniqueIDType();

    uniqueIDType.setId(reservation.getOperaCompanyId());
    uniqueIDType.setType("Profile");
    reservationProfileType.setProfileIdList(List.of(uniqueIDType));
    reservationProfileType.setReservationProfileType(ResProfileTypeType.COMPANY);

    return List.of(reservationProfileType);
  }

  protected ProfileTypeEmails createEmailInformation(LeadGuest leadGuest) {

    final var  emailInfoType = new EmailInfoType();
    final var emailType = new EmailType();
    emailType.setEmailAddress(leadGuest.getEmailAddress());
    emailType.setType(EMAIL_CONSTANT);
    emailType.setPrimaryInd(Boolean.TRUE);
    emailInfoType.setEmail(emailType);
    final ProfileTypeEmails profileTypeEmails = new ProfileTypeEmails();
    profileTypeEmails.setEmailInfo(Arrays.asList(emailInfoType));
    return profileTypeEmails;
  }

  protected ProfileTypeAddresses createAddressInformation(LeadGuest leadGuest) {

    final var  addressInfoType = new AddressInfoType();
    final var addressType = new AddressType();
    addressType.setAddressLine(leadGuest.getAddress().getAddressLine());
    addressType.setCityName(leadGuest.getAddress().getCityName());
    addressType.setPostalCode(leadGuest.getAddress().getPostalCode());
    var countryNameType = new CountryNameType();
    countryNameType.setValue(leadGuest.getAddress().getCountry().getValue());
    addressType.setCountry(countryNameType);
    addressType.setType(HOME_CONSTANT);
    addressType.setPrimaryInd(Boolean.TRUE);
    addressInfoType.setAddress(addressType);
    final ProfileTypeAddresses profileTypeAddresses = new ProfileTypeAddresses();
    profileTypeAddresses.setAddressInfo(Arrays.asList(addressInfoType));
    return profileTypeAddresses;
  }

  private ResGuestType createGuestType(ProfileType profile) {
    final var resGuestType = new ResGuestType();
    final var profileInfo = new ResGuestTypeProfileInfo();
    profileInfo.setProfile(profile);
    resGuestType.setProfileInfo(profileInfo);
    resGuestType.setPrimary(Boolean.TRUE);
    return resGuestType;
  }

  private ProfileType createProfile(PersonNameType personName) {
    final var profile = new ProfileType();
    final var customer = new CustomerType();
    customer.setPersonName(List.of(personName));
    customer.setLanguage(personName.getLanguage());
    profile.setCustomer(customer);
    profile.setProfileType(ProfileTypeType.GUEST);
    return profile;
  }

  private PersonNameType createPersonName(LeadGuest leadGuest) {
    final var personName = new PersonNameType();
    personName.setNameTitle(leadGuest != null ? leadGuest.getTitle() : EMPTY_STR);
    personName.setGivenName(leadGuest != null ? leadGuest.getFirstName() : EMPTY_STR);
    personName.setMiddleName(EMPTY_STR);
    personName.setSurname(leadGuest != null ? leadGuest.getLastName() : TEMP_PROFILE_SURNAME);
    personName.setNameType(PersonNameTypeType.PRIMARY);
    personName.setLanguage(getGuestLanguage(leadGuest));

    return personName;
  }

  private String getGuestLanguage(LeadGuest leadGuest) {
    return Optional.ofNullable(leadGuest)
        .map(LeadGuest::getLanguage)
        .map(String::toUpperCase)
        .map(this::getOhipLanguageCode)
        .orElse(LANGUAGE_ENGLISH);
  }

  private String getOhipLanguageCode(String guestLanguage) {
    switch (guestLanguage) {
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

  protected ResGuaranteeType injectGuaranteeCode() {
    final var resGuaranteeType = new ResGuaranteeType();
    resGuaranteeType.setGuaranteeCode(reservationOhipProperties.getGuaranteeCode());
    resGuaranteeType.setOnHold(Boolean.TRUE);
    return resGuaranteeType;
  }

  protected List<ReservationPaymentMethodType> injectPaymentMethod() {
    final var reservationPaymentMethodType = new ReservationPaymentMethodType();
    var paymentMethod = unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getSetDefaultPaymentMethodDs())
        ? reservationOhipProperties.getDefaultPaymentMethodDs()
        : reservationOhipProperties.getDefaultPaymentMethod();
    reservationPaymentMethodType.setPaymentMethod(paymentMethod);
    return List.of(reservationPaymentMethodType);
  }

  protected UserDefinedFieldsType injectUserDefinedFields(Reservation reservation) {
    if (StringUtils.isEmpty(reservation.getRoomRates().getCellCode()) && StringUtils.isEmpty(
        reservation.getDistributionUsername()) && reservation.getDistributionIATANumber() == null
        && StringUtils.isEmpty(reservation.getCompanyAccountId())
        && StringUtils.isEmpty(reservation.getUserAccountId())
        && StringUtils.isEmpty(reservation.getBookingType())) {
      return null;
    }

    final var userDefinedFields = new UserDefinedFieldsType();

    if (StringUtils.isNotEmpty(reservation.getRoomRates().getCellCode())) {
      final var characterUDF = createCharacterUdf(UDFC_15, reservation.getRoomRates().getCellCode());
      userDefinedFields.addCharacterUDFsItem(characterUDF);
    }

    if (StringUtils.isNotEmpty(reservation.getDistributionUsername())) {
      final var characterUDF = createCharacterUdf(UDFC_13, reservation.getDistributionUsername());
      userDefinedFields.addCharacterUDFsItem(characterUDF);
    }

    if (StringUtils.isNotEmpty(reservation.getDistributionIATANumber())) {
      final var numericUDF = createNumericUdf(UDFN_01,
              BigDecimal.valueOf(Integer.parseInt(reservation.getDistributionIATANumber())));
      userDefinedFields.addNumericUDFsItem(numericUDF);
    }

    if (StringUtils.isNotEmpty(reservation.getCompanyAccountId())) {
      final var characterUDF = createCharacterUdf(UDFC_10, reservation.getCompanyAccountId());
      userDefinedFields.addCharacterUDFsItem(characterUDF);
    }

    if (StringUtils.isNotEmpty(reservation.getUserAccountId())) {
      final var characterUDF = createCharacterUdf(UDFC_35, reservation.getUserAccountId());
      userDefinedFields.addCharacterUDFsItem(characterUDF);
    }

    if (StringUtils.isNotEmpty(reservation.getBookingType())) {
      userDefinedFields.addCharacterUDFsItem(createCharacterUdf(UDFC_09, reservation.getBookingType()));
    }

    if (StringUtils.isNotEmpty(reservation.getCcuiUserEmailId())
        && BooleanUtils.toBoolean(reservationOhipProperties.getIsUdf08Enabled())) {
      userDefinedFields.addCharacterUDFsItem(createCharacterUdf(UDFC_08, reservation.getCcuiUserEmailId()));
    }

    return userDefinedFields;

  }

  private CharacterUDFType createCharacterUdf(String name, String value) {
    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(name);
    characterUDF.setValue(value);
    return characterUDF;
  }

  private NumericUDFType createNumericUdf(String name, BigDecimal value) {
    final var numericUDF = new NumericUDFType();
    numericUDF.setName(name);
    numericUDF.setValue(value);
    return numericUDF;
  }

  protected List<CommentInfoType> injectComments(Reservation reservation) {
    if (StringUtils.isNotBlank(reservation.getBookingNotes())) {
      final var commentInfoType = new CommentInfoType();

      final var commentType = new CommentType();
      commentType.setType(OhipConstants.GENERAL_COMMENT_TYPE);
      commentType.setNotificationLocation(OhipConstants.RESERVATION_NOTIFICATION_AREA);

      final var comment = new FormattedTextTextType();
      comment.setValue(reservation.getBookingNotes());

      commentType.setText(comment);
      commentInfoType.setComment(commentType);

      return List.of(commentInfoType);
    } else {
      return Collections.emptyList();
    }
  }
}
