package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static java.util.Objects.nonNull;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTR_AMADEUS;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTR_TRAVELPORT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EMPTY_STR;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_BOOKING_COM_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_CHECK24_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_DIGITAL_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_EXPEDIA_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_MIGRATION_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_14;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_15;
import static uk.co.whitbread.ohip.infrastructure.rest.client.reservation.HotelReservationOutPortImpl.CITYTAX_ID;

import io.netty.util.internal.StringUtil;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ExternalReferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuaranteeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResInventoryItemsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeTelephones;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PrivacyInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.TelephoneType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoTypeGeneralInformation;
import uk.co.whitbread.ohip.domain.model.checkin.out.CharacterUDFs;
import uk.co.whitbread.ohip.domain.model.checkin.out.UserDefinedFields;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails.StayingGuestAdditionalDetailsBuilder;
import uk.co.whitbread.ohip.domain.model.reservation.out.AdditionalGuestInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.AddressResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.BillingResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;
import uk.co.whitbread.ohip.domain.model.reservation.out.Guarantee;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.LightweightReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ResCashieringType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationBookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse.ReservationByBasketRefResponseBuilder;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCompany;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCreationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationEmailNotifications;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationOverrideReasons;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationTaxTypeInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.out.UniqueIdType;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public interface ReservationResponseOhipMapper {

  String CANCELLED = "Cancelled";
  String PROFILE = "Profile";
  String PASSPORT = "PASSPORT";
  Pattern OVERRIDE_REASON_PATTERN = Pattern.compile("(.+,){2}.+");
  String HOME = "HOME";
  String MOBILE = "MOBILE";

  static AddressResponse toAddressResponseModel(AddressType addressType, String companyName) {
    return AddressResponse.builder()
        .line1(addressType.getAddressLine() != null ? addressType.getAddressLine().get(0) : "")
        .line2(addressType.getAddressLine() != null && !addressType.getAddressLine().isEmpty()
            ? addressType.getAddressLine().get(1) : "")
        .line3(addressType.getAddressLine() != null && addressType.getAddressLine().size() > 2
            ? addressType.getAddressLine().get(2) : "")
        .line4(addressType.getAddressLine() != null && addressType.getAddressLine().size() > 3
            ? addressType.getAddressLine().get(3) : "")
        .postalCode(addressType.getPostalCode() != null ? addressType.getPostalCode() : "")
        .countryCode(addressType.getCountry() != null ? addressType.getCountry().getCode() : "")
        .companyName(companyName != null ? companyName : "")
        .cityName(addressType.getCityName())
        .build();
  }

  @Mapping(source = "reservationResponseOhip", target = "reservationId", qualifiedByName = "reservationIdCustomMapper")
  @Mapping(source = "reservationResponseOhip", target = "createDateTime",
      qualifiedByName = "createDateTimeCustomMapper")
  @Mapping(source = "reservationResponseOhip", target = "roomStay", qualifiedByName = "toRoomStayForModel")
  @Mapping(source = "reservationResponseOhip", target = "depositPolicies",
      qualifiedByName = "toDepositPoliciesForModel")
  ReservationCreationResponse toReservationCreationResponseModel(
      Reservation reservationResponseOhip);

  @Named("reservationIdCustomMapper")
  default String toReservationIdForModel(Reservation reservation) {
    final var reservationIdList = reservation
        .getReservations()
        .getReservation()
        .get(0)
        .getReservationIdList();
    return reservationIdList.stream()
        .filter(uniqueIDType -> uniqueIDType.getType()
            .equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .map(UniqueIDType::getId)
        .toList()
        .get(0);
  }

  @Named("createDateTimeCustomMapper")
  default String toCreateDateTimeForModel(Reservation reservation) {
    return reservation
        .getReservations()
        .getReservation()
        .get(0)
        .getCreateDateTime()
        .toInstant()
        .truncatedTo(ChronoUnit.SECONDS)
        .toString();
  }

  ReservationsDetailsResponse toReservationsDetailsResponseModel(
      ReservationsDetails reservationsDetailsOhip);


  ReservationIdResponse toReservationIdDetailsResponseModel(Reservation reservationIdOhip);

  @Mapping(target = "relationshipsSummary", ignore = true)
  ProfileType toProfileTypeModel(uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType);

  @Mapping(expression = "java(toReservationGuestForModel(reservationById, stayerProfileDetails))",
      target = "reservationGuestList")
  @Mapping(expression = "java(toReservationBookerForModel(bookerProfile, companyName))", target = "reservationBooker")
  @Mapping(expression = "java(toReservationCompanyForModel(reservationById, companyName))",
      target = "reservationCompany")
  @Mapping(expression = "java(toRoomStayForModel(reservationById))", target = "roomStay")
  @Mapping(expression = "java(toResCashieringTypeForModel(reservationById))", target = "cashiering")
  @Mapping(expression = "java(toDepositPoliciesForModel(reservationById))", target = "depositPolicies")
  @Mapping(expression = "java(toBillingForModel(bookerProfile, companyName))", target = "billing")
  @Mapping(expression = "java(toReservationPackagesForModel(reservationById))", target = "reservationPackageList")
  @Mapping(expression = "java(toAdditionalGuestInfoForModel(reservationById, bookerProfile))",
      target = "additionalGuestInfo")
  @Mapping(expression = "java(toPaymentCardForModel(paymentInformation, reservationById))", target = "paymentCard")
  @Mapping(expression = "java(toReservationOverrideReasonsForModel(reservationById))",
      target = "reservationOverrideReasons")
  @Mapping(expression = "java(toGuaranteeForModel(reservationById))", target = "guarantee")
  @Mapping(expression = "java(toReservationStatusForModel(reservationById))", target = "reservationStatus")
  @Mapping(expression = "java(toReservationPreCheckInForModel(reservationById))", target = "preCheckInStatus")
  @Mapping(expression = "java(toReservationEmailNotificationsForModel(reservationById))",
      target = "reservationEmailNotifications")
  @Mapping(expression = "java(toBalanceAmountForModel(reservationById))", target = "balanceAmount")
  @Mapping(expression = "java(toGdsReferenceNumberForModel(reservationById))", target = "gdsReferenceNumber")
  @Mapping(expression = "java(toHotelIdForModel(reservationById))", target = "hotelId")
  @Mapping(expression = "java(toReservationIdForModel(reservationById))", target = "reservationId")
  @Mapping(expression = "java(toUserDefinedFieldsForModel(reservationById))", target = "userDefinedFields")
  @Mapping(expression = "java(toOperaLinkedReservationModel(reservationById))", target = "operaLinkedReservation")
  @Mapping(source = "rateInfo", target = "rateInfo")
  ReservationById toReservationByIdModel(Reservation reservationById, Profile bookerProfile,
      String companyName,
      ReservationPaymentMethodType paymentInformation,
      PriceBreakdownDto rateInfo, List<Profile> stayerProfileDetails);


  @Mapping(expression = "java(toReservationIdForModel(reservationById))", target = "reservationId")
  @Mapping(expression = "java(toReservationPackagesForModel(reservationById))", target = "reservationPackageList")
  LightweightReservationById toLightweightReservationByIdModel(Reservation reservationById);

  @Named("toAdditionalGuestInfoForModel")
  default AdditionalGuestInfo toAdditionalGuestInfoForModel(Reservation reservationById,
      Profile bookerProfile) {
    var resGuestAdditionalInfoType = reservationById.getReservations().getReservation().get(0)
        .getAdditionalGuestInfo();

    Boolean acceptFutureMailing = Optional.ofNullable(bookerProfile)
        .map(Profile::getProfileDetails)
        .map(ProfileType::getPrivacyInfo)
        .map(PrivacyInfoType::getOptInEmail)
        .orElse(null);

    return AdditionalGuestInfo.builder()
        .purposeOfStay(
            Optional.ofNullable(resGuestAdditionalInfoType)
                .map(ResGuestAdditionalInfoType::getPurposeOfStay)
                .orElse(""))
        .acceptFutureMailing(acceptFutureMailing)
        .build();
  }

  @Named("toHotelIdForModel")
  default String toHotelIdForModel(Reservation reservationById) {
    return reservationById.getReservations().getReservation().get(0)
        .getHotelId();
  }

  @Named("toUserDefinedFieldsForModel")
  default UserDefinedFields toUserDefinedFieldsForModel(Reservation reservationById) {
    List<CharacterUDFs> characterUDFs = new ArrayList<>();
    UserDefinedFieldsType reservationUserDefinedFields = reservationById.getReservations().getReservation()
        .get(0).getUserDefinedFields();
    if (reservationUserDefinedFields != null) {
      reservationUserDefinedFields.getCharacterUDFs().forEach(characterUDFType -> {
        CharacterUDFs characterUDF = new CharacterUDFs();
        characterUDF.setName(characterUDFType.getName());
        characterUDF.setValue(characterUDFType.getValue());
        characterUDFs.add(characterUDF);
      });
    }
    UserDefinedFields userDefinedFields = new UserDefinedFields();
    userDefinedFields.setCharacterUDFs(characterUDFs);
    return userDefinedFields;
  }

  @Named("toOperaLinkedReservationModel")
  default boolean toOperaLinkedReservationModel(Reservation reservationById) {
    return Objects.nonNull(reservationById.getReservations().getReservation().get(0).getLinkedReservation())
            && Objects.nonNull(reservationById.getReservations().getReservation().get(0).getLinkedReservation()
            .getReservationInfo());
  }

  default List<ReservationGuest> toReservationGuestForModel(Reservation reservationById,
      List<Profile> stayerProfileDetails) {
    return reservationById.getReservations().getReservation().get(0).getReservationGuests().stream()
        .map(resGuestType ->
            ReservationGuest.builder()
            .givenName(
                resGuestType.getProfileInfo().getProfile().getCustomer().getPersonName().get(0)
                    .getGivenName())
            .surname(resGuestType.getProfileInfo().getProfile().getCustomer().getPersonName().get(0)
                .getSurname())
            .nameTitle(
                resGuestType.getProfileInfo().getProfile().getCustomer().getPersonName().get(0)
                    .getNameTitle())
            .type(resGuestType.getProfileInfo().getProfile().getCustomer().getPersonName().get(0)
                .getNameType() != null
                ? resGuestType.getProfileInfo().getProfile().getCustomer().getPersonName().get(0)
                .getNameType().getValue()
                : null)
            .email(resGuestType.getProfileInfo().getProfile().getEmails() != null
                ? resGuestType.getProfileInfo().getProfile().getEmails().getEmailInfo().get(0)
                .getEmail().getEmailAddress() : null)
            .address(getGuestAddress(resGuestType))
            .language(resGuestType.getProfileInfo().getProfile().getCustomer() != null
                ? resGuestType.getProfileInfo().getProfile().getCustomer().getLanguage() : null)
            .additionalDetails(setAdditionalDetailsForGuest(resGuestType, stayerProfileDetails))
            .isAccompanyingGuest(
                resGuestType.getPrimary() != null && Boolean.TRUE.equals(resGuestType.getPrimary())
                    ? Boolean.FALSE : Boolean.TRUE)
            .profileId(
                Optional.of(resGuestType).map(ResGuestType::getProfileInfo)
                    .map(ResGuestTypeProfileInfo::getProfileIdList)
                    .map(this::getProfileId).orElse(null))
                .homeAddress(getGuestHomeAddress(resGuestType, stayerProfileDetails))
                .build()).toList();
  }

  private StayingGuestAdditionalDetails setAdditionalDetailsForGuest(ResGuestType resGuestType,
      List<Profile> stayerProfileDetails) {
    return Optional.ofNullable(findMatchingProfile(resGuestType, stayerProfileDetails))
        .map(Profile::getProfileDetails)
        .map(ProfileType::getCustomer)
        .map(this::setAdditionalDetails)
        .orElse(null);
  }

  private Profile findMatchingProfile(ResGuestType resGuestType,
      List<Profile> stayerProfileDetails) {
    String reservationProfileId = Optional.ofNullable(resGuestType)
        .map(ResGuestType::getProfileInfo)
        .map(ResGuestTypeProfileInfo::getProfileIdList)
        .map(this::getProfileId)
        .orElse(null);

    return stayerProfileDetails.stream()
        .filter(profile -> profile != null && profile.getProfileIdList() != null)
        .filter(profile -> profile.getProfileIdList().stream()
            .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType::getId)
            .anyMatch(id -> id.equals(reservationProfileId)))
        .findFirst()
        .orElse(null);
  }

  private String getProfileId(List<UniqueIDType> idTypes) {
    return Optional.ofNullable(idTypes)
        .flatMap(ids -> ids.stream()
            .filter(uniqueIdType -> PROFILE.equals(uniqueIdType.getType()))
            .map(UniqueIDType::getId)
            .findFirst())
        .orElse(null);
  }

  private String getPassportNumber(CustomerType customerType) {
    return Optional.ofNullable(customerType)
        .map(CustomerType::getIdentifications)
        .map(CustomerTypeIdentifications::getIdentificationInfo)
        .orElse(Collections.emptyList()).stream()
        .filter(identificationInfo -> ObjectUtils.isNotEmpty(identificationInfo.getIdentification())
            && ObjectUtils.isNotEmpty(identificationInfo.getIdentification().getIdType())
            && identificationInfo.getIdentification().getIdType().equals(PASSPORT)
            && StringUtils.isNotEmpty(identificationInfo.getIdentification().getIdNumber()))
        .map(identificationInfo -> identificationInfo.getIdentification().getIdNumber())
        .findFirst()
        .orElse(null);
  }

  private StayingGuestAdditionalDetails setAdditionalDetails(CustomerType customer) {
    StayingGuestAdditionalDetailsBuilder additionalDetailsBuilder = StayingGuestAdditionalDetails.builder();
    Optional.ofNullable(customer.getBirthDate()).ifPresent(additionalDetailsBuilder::dob);
    Optional.ofNullable(customer.getNationality()).ifPresent(additionalDetailsBuilder::nationality);
    additionalDetailsBuilder.passportNumber(getPassportNumber(customer));
    return additionalDetailsBuilder.build();
  }

  private GuestAddress getGuestAddress(ResGuestType resGuestType) {
    var guestAddress = GuestAddress.builder();

    Optional.ofNullable(resGuestType.getProfileInfo())
        .map(ResGuestTypeProfileInfo::getProfile)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType::getAddresses)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeAddresses::getAddressInfo)
        .map(addressInfoTypes -> addressInfoTypes.stream().findFirst())
        .ifPresent(addressInfoType -> {
          var ohipGuestAddresses = addressInfoType.get().getAddress();
          Optional.ofNullable(ohipGuestAddresses)
              .ifPresent(ohipGuestAddress -> {
                var addressLines = ohipGuestAddress.getAddressLine();
                if (Objects.nonNull(addressLines)) {
                  guestAddress.addressLine1(Optional.ofNullable(addressLines.get(0)).orElse(null));
                  guestAddress.addressLine2(Optional.ofNullable(addressLines.get(1)).orElse(null));
                  guestAddress.addressLine3(Optional.ofNullable(addressLines.get(2)).orElse(null));
                  guestAddress.addressLine4(Optional.ofNullable(addressLines.get(3)).orElse(null));

                  guestAddress.cityName(ohipGuestAddress.getCityName());
                  guestAddress.countryCode(Objects.nonNull(ohipGuestAddress.getCountry())
                      ? ohipGuestAddress.getCountry().getCode() : null);
                }
                if (Objects.nonNull(ohipGuestAddress.getPostalCode())) {
                  guestAddress.postalCode(ohipGuestAddress.getPostalCode());
                  guestAddress.addressType(ohipGuestAddress.getType());
                }
              });
        });
    return guestAddress.build();
  }

  private GuestAddress getGuestHomeAddress(ResGuestType resGuestType,
      List<Profile> profileDetails) {
    var guestAddress = GuestAddress.builder();
    String guestProfileId = Optional.of(resGuestType).map(ResGuestType::getProfileInfo)
        .map(ResGuestTypeProfileInfo::getProfileIdList)
        .map(this::getProfileId).orElse(null);
    profileDetails.stream()
        .filter(profile -> profile != null && profile.getProfileIdList() != null)
        .filter(profile -> profile.getProfileIdList().stream()
            .anyMatch(profileId -> profileId.getId().equals(guestProfileId)))
        .map(Profile::getProfileDetails)
        .filter(addressObj -> Objects.nonNull(addressObj.getAddresses()))
        .map(ProfileType::getAddresses)
        .filter(profileTypeAddresses -> Objects.nonNull(profileTypeAddresses.getAddressInfo()))
        .map(ProfileTypeAddresses::getAddressInfo)
        .flatMap(Collection::stream)
        .filter(addressInfoType -> Objects.nonNull(addressInfoType) && Objects.nonNull(
            addressInfoType.getAddress())
            && ObjectUtils.isNotEmpty(addressInfoType.getType()) && HOME.equals(
            addressInfoType.getType()))
        .findFirst()
        .ifPresent(addressInfoType -> {
          guestAddress.addressId(addressInfoType.getId());
          Optional.ofNullable(addressInfoType.getAddress())
              .ifPresent(ohipGuestAddress -> {
                var addressLines = Optional.ofNullable(ohipGuestAddress.getAddressLine())
                    .orElse(List.of());
                if (Objects.nonNull(addressLines)) {
                  guestAddress.addressLine1(Optional.ofNullable(addressLines.get(0)).orElse(null));
                  guestAddress.addressLine2(Optional.ofNullable(addressLines.get(1)).orElse(null));
                  guestAddress.addressLine3(Optional.ofNullable(addressLines.get(2)).orElse(null));
                  guestAddress.addressLine4(Optional.ofNullable(addressLines.get(3)).orElse(null));

                  guestAddress.cityName(StringUtils.isNotEmpty(ohipGuestAddress.getCityName())
                      ? ohipGuestAddress.getCityName() : null);
                  guestAddress.countryCode(Optional.ofNullable(ohipGuestAddress.getCountry())
                      .map(CountryNameType::getCode)
                      .orElse(null));
                }
                guestAddress.postalCode(StringUtils.isNotEmpty(ohipGuestAddress.getPostalCode())
                    ? ohipGuestAddress.getPostalCode() : null);
                guestAddress.addressType(ohipGuestAddress.getType());
              });
        });

    return guestAddress.build();
  }

  default ReservationBooker toReservationBookerForModel(Profile bookerProfile, String companyName) {

    return Optional.ofNullable(bookerProfile)
        .map(Profile::getProfileDetails)
        .map(profile -> ReservationResponseOhipMapper.getReservationBooker(profile, companyName))
        .map(reservationBooker -> {
          reservationBooker.setProfileId(Optional.ofNullable(bookerProfile.getProfileIdList())
              .orElseGet(Collections::emptyList)
              .stream()
              .findFirst()
              .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType::getId)
              .orElse(null));
          return reservationBooker;
        })
        .orElse(new ReservationBooker());
  }

  default ReservationCompany toReservationCompanyForModel(Reservation reservationById,
      String companyName) {
    if (Objects.isNull(companyName)) {
      return null;
    }

    var address = Optional.ofNullable(reservationById.getReservations().getReservation().get(0)
            .getReservationProfiles())
        .flatMap(hotelReservationTypeReservationProfiles -> getCompanyReservationProfile(reservationById)
                .map(ReservationProfileType::getProfile)
                .map(this::toProfileTypeModel)
                .map(profile -> ReservationResponseOhipMapper.getReservationBookerAddress(profile, companyName)))
        .orElse(null);

    return ReservationCompany.builder()
        .address(address)
        .build();
  }

  private static Optional<ReservationProfileType> getCompanyReservationProfile(
      Reservation reservationById) {
    return reservationById
        .getReservations()
        .getReservation()
        .get(0)
        .getReservationProfiles().getReservationProfile().stream()
        .filter(reservationProfileType -> ResProfileTypeType.COMPANY.equals(
            reservationProfileType.getReservationProfileType()))
        .findFirst();
  }

  private static ReservationBooker getReservationBooker(ProfileType bookerProfileType, String companyName) {

    var telephoneInfo = Optional.ofNullable(bookerProfileType)
        .map(ProfileType::getTelephones)
        .map(CompanyProfileTypeTelephones::getTelephoneInfo);
    var mobile = telephoneInfo
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(telephoneInfoType -> "MOBILE".equalsIgnoreCase(telephoneInfoType.getType()))
        .findFirst()
        .map(telephoneInfoType -> telephoneInfoType.getTelephone().getPhoneNumber())
        .orElse(null);
    var landline = telephoneInfo
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(telephoneInfoType -> "HOME".equalsIgnoreCase(telephoneInfoType.getType()))
        .findFirst()
        .map(telephoneInfoType -> telephoneInfoType.getTelephone().getPhoneNumber())
        .orElse(null);

    var email = Optional.ofNullable(bookerProfileType)
        .map(ProfileType::getEmails)
        .map(CompanyProfileTypeEmails::getEmailInfo)
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst()
        .map(emailInfoType -> emailInfoType.getEmail().getEmailAddress())
        .orElse(null);

    var address = getReservationBookerAddress(bookerProfileType, companyName);

    var personName = Optional.ofNullable(bookerProfileType)
        .map(ProfileType::getCustomer)
        .map(CustomerType::getPersonName)
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst();

    return ReservationBooker.builder()
        .email(email)
        .title(personName.map(PersonNameType::getNameTitle).orElse(null))
        .firstName(personName.map(PersonNameType::getGivenName).orElse(null))
        .lastName(personName.map(PersonNameType::getSurname).orElse(null))
        .mobile(mobile)
        .landline(landline)
        .address(address)
        .build();
  }

  private static ReservationBookerAddress getReservationBookerAddress(ProfileType bookerProfileType,
      String companyName) {
    var company = Optional.ofNullable(bookerProfileType)
        .map(ProfileType::getCompany)
        .map(CompanyType::getCompanyName)
        .orElse(companyName);

    var addressInfo = Optional.ofNullable(bookerProfileType)
        .map(ProfileType::getAddresses)
        .map(ProfileTypeAddresses::getAddressInfo)
        .orElseGet(Collections::emptyList)
        .stream()
        .findFirst();
    var address = addressInfo
        .map(AddressInfoType::getAddress);
    var addressLines = address
        .map(AddressType::getAddressLine)
        .orElse(Collections.emptyList());

    return ReservationBookerAddress.builder()
        .addressType(addressInfo.map(AddressInfoType::getType).orElse(null))
        .companyName(company)
        .countryCode(address.map(AddressType::getCountry).map(CountryNameType::getCode).orElse(null))
        .cityName(address.map(AddressType::getCityName).orElse(null))
        .postalCode(address.map(AddressType::getPostalCode).orElse(null))
        .addressLine1(!CollectionUtils.isEmpty(addressLines) ? addressLines.get(0) : null)
        .addressLine2(!CollectionUtils.isEmpty(addressLines) && addressLines.size() > 1 ? addressLines.get(1) : null)
        .addressLine3(!CollectionUtils.isEmpty(addressLines) && addressLines.size() > 2 ? addressLines.get(2) : null)
        .addressLine4(!CollectionUtils.isEmpty(addressLines) && addressLines.size() > 3 ? addressLines.get(3) : null)
        .build();
  }

  @Named("toReservationPackagesForModel")
  default List<ReservationPackagesDetailsResponse> toReservationPackagesForModel(
      Reservation reservationById) {

    return Optional.ofNullable(
            reservationById.getReservations().getReservation().get(0).getReservationPackages())
        .orElseGet(Collections::emptyList)
        .stream()
        .filter(Objects::nonNull)
        .map(
            packageCodeType -> {
              var postingAttributes = Optional.ofNullable(packageCodeType.getPackageHeaderType())
                      .map(PackageCodeHeaderType::getPostingAttributes);
              var addToRate = postingAttributes.map(ConfigPostingAttributesType::getAddToRate)
                      .orElse(Boolean.FALSE);
              var printSeparateLine = postingAttributes.map(ConfigPostingAttributesType::getPrintSeparateLine)
                      .orElse(Boolean.FALSE);

              var shouldIncludePrice = addToRate.equals(Boolean.FALSE)
                      && printSeparateLine.equals(Boolean.TRUE);

              var code = packageCodeType.getPackageCode();
              var description = packageCodeType.getPackageHeaderType().getPrimaryDetails()
                  .getDescription();
              var unitPrice =
                  shouldIncludePrice ? packageCodeType.getScheduleList().get(0).getUnitPrice()
                      : BigDecimal.ZERO;
              var computedPrice = shouldIncludePrice ? packageCodeType.getScheduleList().get(0)
                  .getComputedResvPrice() : BigDecimal.ZERO;
              var totalQuantity = packageCodeType.getConsumptionDetails().getTotalQuantity();
              var startDate = packageCodeType.getStartDate();
              var endDate = packageCodeType.getEndDate();

              return ReservationPackagesDetailsResponse.builder()
                  .packageCode(code)
                  .description(description)
                  .unitPrice(unitPrice)
                  .totalQuantity(totalQuantity)
                  .computedPrice(computedPrice)
                  .startDate(String.valueOf(startDate))
                  .endDate(String.valueOf(endDate))
                  .packageGroup(packageCodeType.getPackageGroup())
                  .build();
            })
        .toList();
  }

  @Named("toRoomStayForModel")
  default RoomStay toRoomStayForModel(Reservation reservationById) {
    return RoomStay.builder()
        .adultCount(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getGuestCounts()
                .getAdults())
        .childCount(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getGuestCounts()
                .getChildren())
        .arrivalDate(reservationById.getReservations().getReservation().get(0).getRoomStay()
            .getArrivalDate())
        .departureDate(reservationById.getReservations().getReservation().get(0).getRoomStay()
            .getDepartureDate())
        .promotionCode(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getPromotion()
                != null
                ? reservationById.getReservations().getReservation().get(0).getRoomStay()
                .getPromotion().getPromotionCode()
                : null
        )
        .ratePlanCode(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates()
                .get(0).getRatePlanCode())
        .roomType(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates()
                .get(0).getRoomType())
        .marketCode(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates()
                .get(0).getMarketCode())
        .sourceCode(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates()
                .get(0).getSourceCode())
        .cot(toCotModel(
            reservationById.getReservations().getReservation().get(0).getInventoryItems()))
        .roomPrice(
            reservationById.getReservations().getReservation().get(0).getRoomStay().getTotal()
                .getAmountBeforeTax())
        .ratesPerNight(toRatePerNightModel(reservationById))
        .cellCode(toCellCodeForModel(reservationById))
        .roomNumber(toRoomNumberForModel(reservationById))
        .bookingChannel(reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates().get(0)
            .getSourceCodeDescription())
        .build();
  }

  @Named("toResCashieringTypeForModel")
  default ResCashieringType toResCashieringTypeForModel(Reservation reservationById) {
    return ResCashieringType.builder()
        .taxType(ReservationTaxTypeInfo.builder()
            .code(reservationById.getReservations().getReservation().get(0)
                    .getCashiering().getTaxType() != null ? reservationById.getReservations().getReservation().get(0)
                .getCashiering().getTaxType().getCode() : null)
            .build())
        .build();
  }

  private String toRoomNumberForModel(Reservation reservationById) {
    var currentRoomInfo = reservationById.getReservations().getReservation().get(0).getRoomStay()
        .getCurrentRoomInfo();
    return (currentRoomInfo != null) ? currentRoomInfo.getRoomId() : null;
  }

  private String toCellCodeForModel(Reservation reservationById) {
    var userDefinedFields = reservationById.getReservations().getReservation().get(0)
        .getUserDefinedFields();

    return (userDefinedFields != null && userDefinedFields.getCharacterUDFs() != null)
        ? userDefinedFields.getCharacterUDFs().stream().filter(entry -> entry.getName().equals(UDFC_15))
        .map(CharacterUDFType::getValue).findFirst().orElse(null) : null;
  }

  @Named("toBillingForModel")
  default BillingResponse toBillingForModel(Profile bookerProfile, String companyName) {
    if (bookerProfile == null) {
      return null;
    }
    AddressResponse address = new AddressResponse();
    final var profileType = bookerProfile.getProfileDetails();
    if (profileType.getAddresses() != null && profileType.getAddresses().getAddressInfo() != null
        && !profileType.getAddresses().getAddressInfo().isEmpty()) {
      final var addressType = profileType.getAddresses().getAddressInfo().get(0).getAddress();
      if (addressType != null) {
        address = toAddressResponseModel(addressType, companyName);
      }
    }

    return buildBillingResponse(profileType, address);
  }

  private BillingResponse buildBillingResponse(ProfileType profileType, AddressResponse address) {
    final var personName = profileType.getCustomer().getPersonName().get(0);

    return BillingResponse.builder()
            .address(address)
            .title(Optional.ofNullable(personName.getNameTitle()).orElse(EMPTY_STR))
            .firstName(personName.getGivenName())
            .lastName(personName.getSurname())
            .email(getLatestEmailAddress(profileType))
            .telephone(getLatestTelephoneNumber(profileType, MOBILE))
            .landline(getTelephoneNumber(profileType, HOME))
            .build();
  }

  private static long safeIdAsLong(String id) {
    if (id == null || id.isBlank()) {
      return Long.MIN_VALUE; // null/blank ids sort last when descending
    }
    try {
      return Long.parseLong(id.trim());
    } catch (NumberFormatException ex) {
      return Long.MIN_VALUE; // non-numeric ids sort last when descending
    }
  }

  private String getLatestEmailAddress(ProfileType profileType) {
    if (profileType.getEmails() == null || CollectionUtils.isEmpty(profileType.getEmails().getEmailInfo())) {
      return EMPTY_STR;
    }

    return profileType.getEmails().getEmailInfo().stream()
        .filter(Objects::nonNull)
        .filter(info -> info.getEmail() != null)
        .filter(info -> StringUtils.isNotBlank(info.getEmail().getEmailAddress()))
        .max(Comparator.comparingLong(info -> safeIdAsLong(info.getId())))
        .map(info -> info.getEmail().getEmailAddress())
        .orElse(EMPTY_STR);
  }

  private String getTelephoneNumber(ProfileType profileType, String type) {
    if (profileType.getTelephones() == null
            || CollectionUtils.isEmpty(profileType.getTelephones().getTelephoneInfo())) {
      return EMPTY_STR;
    }

    return profileType.getTelephones().getTelephoneInfo().stream()
            .filter(info -> Objects.equals(type, info.getType()) && info.getTelephone() != null)
            .findFirst()
            .map(info -> Optional.ofNullable(info.getTelephone())
                    .map(TelephoneType::getPhoneNumber)
                    .orElse(EMPTY_STR))
            .orElse(EMPTY_STR);
  }

  private String getLatestTelephoneNumber(ProfileType profileType, String type) {
    if (profileType.getTelephones() == null
        || CollectionUtils.isEmpty(profileType.getTelephones().getTelephoneInfo())) {
      return EMPTY_STR;
    }

    return profileType.getTelephones().getTelephoneInfo().stream()
        .filter(Objects::nonNull)
        .filter(info -> Objects.equals(type, info.getType()) && info.getTelephone() != null)
        .filter(info -> StringUtils.isNotBlank(info.getTelephone().getPhoneNumber()))
        .max(Comparator.comparingLong(info -> safeIdAsLong(info.getId())))
        .map(info -> info.getTelephone().getPhoneNumber())
        .orElse(EMPTY_STR);
  }

  @Named("toDepositPoliciesForModel")
  default List<DepositPolicies> toDepositPoliciesForModel(Reservation reservationById) {
    return reservationById.getReservations().getReservation().get(0).getReservationPolicies()
        .getDepositPolicies().stream()
        .map(depositPolicyType -> DepositPolicies.builder()
            .amountPaid(CurrencyAmountType.builder()
                .amount(depositPolicyType.getAmountPaid().getAmount())
                .currencyCode(depositPolicyType.getAmountPaid().getCurrencyCode())
                .build())
            .amountDue(CurrencyAmountType.builder()
                .amount(depositPolicyType.getAmountDue().getAmount())
                .currencyCode(depositPolicyType.getAmountDue().getCurrencyCode())
                .build())
            .policyCode(depositPolicyType.getPolicy().getPolicyCode())
            .build()).toList();
  }

  @Named("toReservationLightweightResponseModel")
  default ReservationLightweightResponse toReservationLightweightResponseModel(
      List<Reservation> reservations, HotelDetails hotelConfigs, String hotelId) {
    List<LightweightReservationById> reservationByIds = reservations.stream()
        .map(reservationById -> {
          LightweightReservationById mappedReservation = toLightweightReservationByIdModel(reservationById);
          mappedReservation.setHotelId(hotelId);
          setStayTimeInfo(mappedReservation, hotelConfigs);
          setEmailAddress(mappedReservation, reservationById);
          setPurposeOfStay(mappedReservation, reservationById);
          return mappedReservation;
        })
        .toList();

    return new ReservationLightweightResponse(reservationByIds);
  }

  private void setPurposeOfStay(LightweightReservationById mappedReservation,
                                uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationById) {
    String purposeOfStay = Optional.of(reservationById)
        .map(Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0))
        .map(HotelReservationType::getAdditionalGuestInfo)
        .map(ResGuestAdditionalInfoType::getPurposeOfStay)
        .orElse(null);
    mappedReservation.setPurposeOfStay(purposeOfStay);
  }

  private void setEmailAddress(LightweightReservationById reservation,
                               uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationById) {
    var reservationGuests = reservationById.getReservations().getReservation().get(0).getReservationGuests();
    if (reservationGuests != null && !reservationGuests.isEmpty()) {
      var leadGuest = reservationGuests.get(0);
      if (leadGuest.getProfileInfo().getProfile().getEmails() != null) {
        reservation.setEmail(leadGuest.getProfileInfo().getProfile().getEmails()
            .getEmailInfo().stream()
            .findFirst()
            .map(emailInfo -> emailInfo.getEmail().getEmailAddress())
            .orElse(null));
      }
    }
  }

  @SuppressWarnings("java:S107")
  @Named("toReservationByBasketRefResponseModel")
  default ReservationByBasketRefResponse toReservationByBasketRefResponseModel(
      List<Reservation> reservations, Map<String, RateInfo> rateInfos, Map<String, Profile> profiles,
      HotelDetails hotelConfigs, ReservationAmounts totalReservationAmounts,
      ReservationPaymentMethodType paymentInformation, Map<String, PriceBreakdownDto> rateInfoMap,
      String purchaseOrderNumber, String customReferenceNumber, String channel) {
    List<ReservationById> reservationByIds = reservations.stream()
        .map(reservationById ->
            toReservationByIdModel(reservationById, getBookerProfile(reservationById, profiles),
                getCompanyName(reservationById), paymentInformation,
                rateInfoMap.get(reservationById.getReservations().getReservation().get(0)
                    .getReservationIdList().get(0).getId()),
                getStayerProfile(reservationById, profiles)))
        .toList();
    if (!rateInfos.isEmpty()) {
      reservationByIds.forEach(reservationById -> setRoomRateBreakdown(reservationById, rateInfos));
      reservationByIds.forEach(reservationById -> setPackagesBreakDown(reservationById,
          rateInfos.entrySet().stream().findFirst().get().getValue()));
    }
    reservationByIds.forEach(this::setReservationOverriddenInfo);
    reservationByIds.forEach(reservationById -> setStayTimeInfo(reservationById, hotelConfigs));
    IntStream.range(0, reservations.size()).forEach(idx ->
        setPackageStartAndEndDate(reservations.get(idx), reservationByIds.get(idx)));
    var res = reservations.get(0).getReservations().getReservation().get(0);
    String bookingReference = " ";
    if (!(CollectionUtils.isEmpty(res.getExternalReferences()))) {
      bookingReference = reservations.get(0)
          .getReservations().getReservation().get(0)
          .getExternalReferences().stream() //TODO: maybe this filter is not necessary at all, needs to be checked
          .filter(extRef -> (extRef.getIdContext().equalsIgnoreCase(EXTERNAL_REF_DIGITAL_ID_CONTEXT)
              || extRef.getIdContext().equalsIgnoreCase(EXTERNAL_REF_MIGRATION_ID_CONTEXT)
              || extRef.getIdContext().equalsIgnoreCase(EXTERNAL_REF_BOOKING_COM_ID_CONTEXT)
              || extRef.getIdContext().equalsIgnoreCase(EXTERNAL_REF_EXPEDIA_ID_CONTEXT)
              || extRef.getIdContext().equalsIgnoreCase(EXTERNAL_REF_CHECK24_ID_CONTEXT)))
          .findFirst().map(ExternalReferenceType::getId).orElse(StringUtil.EMPTY_STRING);
    }
    String currencyCode = reservations.get(0).getReservations().getReservation().get(0)
        .getRoomStay().getRoomRates()
        .get(0).getRates().getRate().get(0).getBase().getCurrencyCode();
    reservationByIds.forEach(this::setReservationOverriddenInfo);
    reservationByIds.forEach(r -> setStayTimeInfo(r, hotelConfigs));
    String hotelId = hotelConfigs.getHotelConfigInfo().getHotelId();

    //QUICK fix for DISTR to check billing intructions - this is a temporary fix until DNRQ-58405 is implemented
    Boolean isCnp = reservations.get(0) != null
        && !CollectionUtils.isEmpty(
        reservations.get(0).getReservations().getReservation().get(0)
            .getRoutingInstructions())
        && !CollectionUtils.isEmpty(
        reservations.get(0).getReservations().getReservation().get(0)
            .getRoutingInstructions().get(0).getFolio().getInstructions())
        && reservations.get(0).getReservations().getReservation().get(0)
        .getRoutingInstructions().stream()
        .flatMap(f -> f.getFolio().getInstructions().stream())
        .filter(routingInstructionType -> routingInstructionType.getBillingInstructions() != null)
        .flatMap(routingInstructionType -> routingInstructionType.getBillingInstructions().stream())
        .anyMatch(billingInstructionType -> "ROOM".equals(billingInstructionType.getBillingCode()));

    return buildReservationResponse(totalReservationAmounts, reservationByIds, currencyCode,
        hotelId, isCnp, getCompanyId(reservations.get(0), channel), bookingReference,
        purchaseOrderNumber, customReferenceNumber);
  }

  private void setPackageStartAndEndDate(Reservation reservation, ReservationById reservationById) {

    var reservationPackageTypes = reservation.getReservations()
        .getReservation().get(0).getReservationPackages();
    var reservationPackagesDetailsResponses = reservationById.getReservationPackageList();

    if (Objects.nonNull(reservationPackageTypes) && Objects.nonNull(
        reservationPackagesDetailsResponses) && !reservationPackagesDetailsResponses.isEmpty()) {

      /* null objects from this list are filtered out in the #toReservationPackagesForModel method,
      when the package list is copied from the Reservation object to the ReservationById object.
      So we must do the same here, otherwise the two lists may have different sizes,
      with the first one containing null entries.
       */
      var nonNullReservationPackageTypes = reservationPackageTypes.stream().filter(Objects::nonNull).toList();
      IntStream.range(0, nonNullReservationPackageTypes.size()).forEach(idx -> {
        reservationPackagesDetailsResponses.get(idx)
            .setStartDate(String.valueOf(reservationPackageTypes.get(idx).getStartDate()));
        reservationPackagesDetailsResponses.get(idx)
            .setEndDate(String.valueOf(reservationPackageTypes.get(idx).getEndDate()));
      });
    }
  }

  @SuppressWarnings("java:S107")
  private ReservationByBasketRefResponse buildReservationResponse(
      ReservationAmounts totalReservationAmounts,
      List<ReservationById> reservationByIds, String currencyCode, String hotelId,
      Boolean isCnp, String companyId, String bookingReference,
      String purchaseOrderNumber, String customReferenceNumber) {
    var containsCancelledRsv = reservationByIds.stream()
        .anyMatch(reservationById -> reservationById.getReservationStatus().equals(CANCELLED));
    ReservationByBasketRefResponseBuilder builder = ReservationByBasketRefResponse.builder()
        .reservationByIdList(reservationByIds)
        .currencyCode(currencyCode)
        .companyId(companyId)
        .bookingReference(bookingReference)
        .purchaseOrderNumber(purchaseOrderNumber)
        .customReferenceNumber(customReferenceNumber);
    if (nonNull(totalReservationAmounts)) {
      var total = totalReservationAmounts.getTotalCostOfStay();
      builder.amountPaid(totalReservationAmounts.getDeposit())
          .balanceOutstanding(totalReservationAmounts.getOutStandingCostOfStay())
          //newTotal and previousTotal may need changes according to future requirements
          .newTotal(total)
          .previousTotal(total.subtract(totalReservationAmounts.getOutStandingCostOfStay()))
          .totalCostWoDiscount(total.add(totalReservationAmounts.getDiscount()))
          .totalCost(total)
          .discount(totalReservationAmounts.getDiscount())
          .hotelId(hotelId);
    }
    // when we have cancelled reservations, newTotal, previousTotal and totalCost will be calculated
    // differently
    if (nonNull(totalReservationAmounts) && containsCancelledRsv) {
      var total = totalReservationAmounts.getTotalCostOfStay();
      var cancelledRsv = reservationByIds.stream()
              .filter(reservationById -> reservationById.getReservationStatus().equals(CANCELLED))
                      .toList();

      BigDecimal totalCancelledReservations = cancelledRsv.stream()
              .map(reservation -> reservation.getRateInfo().getSummary().getOutStandingCostOfStay())
              .filter(Objects::nonNull)
              .reduce(BigDecimal.ZERO, BigDecimal::add);

      builder.balanceOutstanding(totalReservationAmounts.getOutStandingCostOfStay().negate())
              .newTotal(total.subtract(totalCancelledReservations))
              .previousTotal(total)
              .totalCost(total.subtract(totalCancelledReservations));
    }

    builder.isCnp(isCnp);

    return builder.build();
  }

  private void setStayTimeInfo(ReservationById reservation, HotelDetails hotelConfigs) {
    HotelInfoTypeGeneralInformation hotelGeneralInfo = hotelConfigs.getHotelConfigInfo()
        .getGeneralInformation();
    if (nonNull(hotelGeneralInfo)) {
      reservation.getRoomStay().setCheckInTime(
          getTimeFromDate(hotelGeneralInfo.getCheckInTime()));
      reservation.getRoomStay().setCheckOutTime(
          getTimeFromDate(hotelGeneralInfo.getCheckOutTime()));
    }
  }

  private void setStayTimeInfo(LightweightReservationById reservation, HotelDetails hotelConfigs) {
    HotelInfoTypeGeneralInformation hotelGeneralInfo = hotelConfigs.getHotelConfigInfo()
        .getGeneralInformation();
    if (nonNull(hotelGeneralInfo)) {
      reservation.setCheckInTime(
          getTimeFromDate(hotelGeneralInfo.getCheckInTime()));
      reservation.setCheckOutTime(
          getTimeFromDate(hotelGeneralInfo.getCheckOutTime()));
    }
  }

  private void setReservationOverriddenInfo(ReservationById reservation) {
    if (nonNull(reservation.getReservationOverrideReasons())) {
      reservation.setReservationOverridden(true);
    }
  }

  private void setRoomRateBreakdown(ReservationById reservation, Map<String, RateInfo> rateInfos) {
    if (!CollectionUtils.isEmpty(reservation.getRoomStay().getRatesPerNight())) {
      reservation.getRoomStay().getRatesPerNight().forEach(ratePerNight -> {
        var rateInfo = rateInfos.get(ratePerNight.getStartDate());
        ratePerNight.setGrossPricePerNight(rateInfo.getDetail().getRevenue().getAmountBeforeTax());
        ratePerNight.setVatRate(
            rateInfo.getDetail().getRevenue().getTaxes().getTax().get(0).getAmount());

        // set city tax breakdown
        var cityTaxPackage = reservation.getReservationPackageList()
            .stream()
            .filter(pckg -> CITYTAX_ID.equals(pckg.getPackageCode())).findAny();

        if (cityTaxPackage.isPresent()
            /* Check needed for Edinburgh hotels where the city tax is calculated for the first 5 nights.
            Opera will return the cityTax package only for the first 5 nights of a reservation. */
            && rateInfo.getDetail().getPackages() != null) {
          rateInfo.getDetail().getPackages()
              .stream()
              .filter(ratePckg -> CITYTAX_ID.equals(ratePckg.getCode()))
              .findFirst()
              .ifPresent(cityTax -> {
                ratePerNight.setCityTaxAmountBeforeTax(cityTax.getAmountBeforeTax());
                ratePerNight.setCityTaxVat(cityTax.getTaxes().getTax().get(0).getAmount());
              });
        }
      });
    }
  }

  private void setPackagesBreakDown(ReservationById reservation, RateInfo rateInfo) {
    reservation.getReservationPackageList().forEach(reservationPackage -> {
      if (Objects.nonNull(rateInfo.getDetail().getPackages())) {
        var packageRateInfo = rateInfo.getDetail().getPackages().stream()
            .filter(totalType -> totalType.getCode().equals(reservationPackage.getPackageCode()))
            .findFirst();
        if (packageRateInfo.isPresent()) {
          reservationPackage.setGrossPrice(packageRateInfo.get().getAmountBeforeTax());
          reservationPackage.setVatTax(
              packageRateInfo.get().getTaxes().getTax().get(0).getAmount());
        }
      }
    });
  }

  private String getTimeFromDate(Date date) {
    if (Objects.isNull(date)) {
      return null;
    }
    var timeFormatter = new SimpleDateFormat("HH:mm");
    return timeFormatter.format(date);
  }

  @Named("toReservationCreationResponseModel")
  default ReservationResponse toReservationResponseModel(List<Reservation> reservations) {
    List<ReservationCreationResponse> reservationByIds = reservations.stream()
        .map(this::toReservationCreationResponseModel).toList();
    String currencyCode = reservations.get(0).getReservations().getReservation().get(0)
        .getRoomStay().getRoomRates()
        .get(0).getRates().getRate().get(0).getBase().getCurrencyCode();
    String hotelId = reservations.get(0)
        .getReservations()
        .getReservation()
        .get(0)
        .getHotelId();
    return ReservationResponse.builder().reservations(reservationByIds).currencyCode(currencyCode)
        .hotelId(hotelId).build();
  }

  @Named("toReservationCreationResponseLightModel")
  default ReservationResponse toReservationResponseLightModel(Set<String> reservationIds, String hotelId) {
    List<ReservationCreationResponse> reservationByIds = reservationIds.stream()
        .map(resId -> ReservationCreationResponse.builder().reservationId(resId).createDateTime(
            Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()).build()).toList();
    return ReservationResponse.builder().reservations(reservationByIds).currencyCode(null)
        .hotelId(hotelId).build();
  }

  default ReservationPackagesResponse toReservationPackagesResponseModel(
      List<Reservation> reservationsList, boolean mealInclusiveRate) {
    if (CollectionUtils.isEmpty(reservationsList)) {
      return ReservationPackagesResponse.builder().roomsSelections(null).build();
    }
    List<RoomsSelections> roomsSelections = reservationsList.stream()
        .map(reservations -> toRoomsSelectionsModel(
            reservations.getReservations().getReservation().get(0).getReservationPackages(), mealInclusiveRate))
        .toList();

    var ratePlanCode = reservationsList.get(0).getReservations().getReservation().get(0).getRoomStay().getRoomRates()
        .get(0).getRatePlanCode();

    return ReservationPackagesResponse.builder()
        .roomsSelections(roomsSelections)
        .ratePlanCode(ratePlanCode)
        .build();
  }

  default RoomsSelections toRoomsSelectionsModel(List<ReservationPackageType> packageTypes,
      boolean mealInclusiveRate) {
    if (CollectionUtils.isEmpty(packageTypes)) {
      return RoomsSelections.builder().packagesSelection(null).build();
    }
    var packageTypesList =
        !mealInclusiveRate
            ? packageTypes.stream()
                .filter(packageType -> packageType.getScheduleList() != null
                    && packageType.getScheduleList().stream()
                    .anyMatch(schedule -> schedule.getTotalQuantity() != null
                        && schedule.getTotalQuantity() > 0))
                .map(this::toPackagesSelectionsModel)
                .toList()
            : packageTypes.stream().map(this::toPackagesSelectionsModel)
                .toList();

    return RoomsSelections.builder()
        .packagesSelection(packageTypesList)
        .build();
  }

  default PackagesSelection toPackagesSelectionsModel(
      ReservationPackageType reservationPackageType) {
    List<String> scheduledList = null;
    if (nonNull(reservationPackageType.getScheduleList())) {
      scheduledList = reservationPackageType.getScheduleList().stream()
          .filter(dayScheduled -> dayScheduled.getConsumptionDate() != null)
          .map(dayScheduled -> dayScheduled.getConsumptionDate().toString())
          .toList();
    }
    return PackagesSelection.builder()
        .id(reservationPackageType.getPackageCode())
        .noSelections(reservationPackageType.getConsumptionDetails().getTotalQuantity())
        .scheduledList(scheduledList)
        .packageGroup(reservationPackageType.getPackageGroup())
        .ratePlanCode(reservationPackageType.getRatePlanCode())
        .build();
  }

  private List<RatePerNight> toRatePerNightModel(Reservation reservationById) {
    var cityTaxData =
        reservationById.getReservations().getReservation().get(0).getReservationPackages() != null
            ? reservationById.getReservations().getReservation().get(0).getReservationPackages()
            .stream()
            .filter(pckg -> "CITYTAX".equals(pckg.getPackageCode())).toList() :
            null;

    return reservationById.getReservations().getReservation().get(0).getRoomStay().getRoomRates()
        .stream()
        .map(roomRateType -> {
          var startDate = roomRateType.getStart().toString();
          BigDecimal pricePerNight = roomRateType.getRates().getRate().get(0).getBase()
              .getAmountBeforeTax();
          var cityTaxPerNight = Objects.isNull(cityTaxData) ? BigDecimal.ZERO :
              cityTaxData.stream().flatMap(pckg -> pckg.getScheduleList().stream()).filter(
                      pckgSchedule -> pckgSchedule.getConsumptionDate().equals(roomRateType.getStart()))
                  .map(ReservationPackageScheduleType::getComputedResvPrice).findFirst()
                  .orElse(BigDecimal.ZERO);
          return RatePerNight.builder()
              .startDate(startDate)
              .pricePerNight(pricePerNight)
              .cityTaxPerNight(cityTaxPerNight)
              .build();
        }).toList();
  }

  private Boolean toCotModel(ResInventoryItemsType inventoryItems) {
    return inventoryItems != null && inventoryItems.getItem() != null
        && inventoryItems.getItem().stream()
        .anyMatch(resItem -> "Cot".equals(resItem.getItem().getName()));
  }

  private Profile getBookerProfile(Reservation reservationById, Map<String, Profile> profiles) {
    if (reservationById.getReservations().getReservation().get(0).getReservationProfiles() == null) {
      return null;
    }
    String bookerProfileId = reservationById.getReservations().getReservation().get(0)
        .getReservationProfiles().getReservationProfile().stream().filter(
            reservationProfileType -> ResProfileTypeType.RESERVATIONCONTACT.equals(
                reservationProfileType.getReservationProfileType())).findFirst()
        .map(reservationProfileType -> reservationProfileType.getProfileIdList().get(0).getId()).orElse(null);

    if (bookerProfileId != null) {
      return profiles.get(bookerProfileId);
    }

    return null;
  }

  private List<Profile> getStayerProfile(Reservation reservationById,
      Map<String, Profile> profiles) {
    return Optional.ofNullable(reservationById)
        .map(Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .orElse(Collections.emptyList()).stream()
        .flatMap(
            hotelReservationType -> Optional.ofNullable(hotelReservationType.getReservationGuests())
                .orElse(Collections.emptyList()).stream())
        .flatMap(resGuestType -> Optional.ofNullable(resGuestType.getProfileInfo())
            .map(ResGuestTypeProfileInfo::getProfileIdList)
            .orElse(Collections.emptyList()).stream())
        .filter(profileIds -> StringUtils.isNotEmpty(profileIds.getType()) && PROFILE.equals(
            profileIds.getType()))
        .map(UniqueIDType::getId)
        .map(profiles::get)
        .toList();
  }

  private String getCompanyName(Reservation reservationById) {
    if (reservationById.getReservations().getReservation().get(0).getReservationProfiles() == null) {
      return null;
    }

    return getCompanyReservationProfile(reservationById)
        .map(reservationProfileType -> reservationProfileType.getProfile().getCompany().getCompanyName())
        .orElse(null);
  }

  private String getCompanyId(Reservation reservationById, String channel) {
    if ((nonNull(channel) && DISTRIBUTION_CHANNEL.equals(channel))
        || reservationById.getReservations().getReservation().get(0).getReservationProfiles()
        == null) {
      return null;
    }

    var reservationProfileCompany = getCompanyReservationProfile(reservationById);
    return reservationProfileCompany
        .flatMap(reservationProfileType -> reservationProfileType.getProfileIdList()
            .stream()
            .filter(profile -> profile.getType().equals(PROFILE))
            .map(UniqueIDType::getId)
            .findFirst())
        .orElse(null);
  }

  default ReservationsPaymentCardType toPaymentCardReservationModel(List<UniqueIDType> ids,
      ReservationPaymentMethodType paymentInformation,
      Reservation reservationById) {
    return ReservationsPaymentCardType.builder()
        .paymentCardType(toPaymentCardForModel(paymentInformation, reservationById))
        .ids(ids)
        .build();
  }

  @Named("toPaymentCardForModel")
  default ReservationPaymentCardType toPaymentCardForModel(ReservationPaymentMethodType paymentInformation,
      Reservation reservationById) {
    var paymentCard = (paymentInformation != null && paymentInformation.getPaymentCard() != null)
        ? paymentInformation.getPaymentCard() : null;

    String paymentMethod = null;
    if (paymentInformation != null && paymentInformation.getPaymentMethod() != null) {
      paymentMethod = paymentInformation.getPaymentMethod();
    } else if (reservationById.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0).getPaymentMethod() != null) {
      paymentMethod = reservationById.getReservations().getReservation().get(0).getReservationPaymentMethods()
          .get(0)
          .getPaymentMethod();
    }

    Integer folioView = null;
    if (paymentInformation != null) {
      folioView = paymentInformation.getFolioView();
    } else if (reservationById.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0).getFolioView() != null) {
      folioView = reservationById.getReservations().getReservation().get(0)
          .getReservationPaymentMethods()
          .get(0).getFolioView();
    }
    var reservationPaymentCardType = ReservationPaymentCardType.builder()
        .cardId((paymentCard != null) ? toCardIdForModel(paymentCard.getCardId()) : null)
        .currentAuthorizedAmount((paymentCard != null) ? toCurrencyAmountTypeForModel(
            paymentCard.getCurrentAuthorizedAmount()) : null)
        .approvalAmountNeeded((paymentCard != null) ? toCurrencyAmountTypeForModel(
            paymentCard.getApprovalAmountNeeded()) : null)
        .approvalCode((paymentCard != null) ? paymentCard.getApprovalCode() : null)
        .userDefinedCardType((paymentCard != null) ? paymentCard.getUserDefinedCardType() : null)
        .cardNumberLast4Digits(
            (paymentCard != null) ? paymentCard.getCardNumberLast4Digits() : null)
        .expirationDateMasked((paymentCard != null) ? paymentCard.getExpirationDateMasked() : null)
        .expirationDateExpired(
            (paymentCard != null) ? paymentCard.getExpirationDateExpired() : null)
        .cardHolderName((paymentCard != null) ? paymentCard.getCardHolderName() : null)
        .attachCreditCardToProfile(
            (paymentCard != null) ? paymentCard.getAttachCreditCardToProfile() : null)
        .swiped((paymentCard != null) ? paymentCard.getSwiped() : null)
        .cardPresent((paymentCard != null) ? paymentCard.getCardPresent() : null)
        .cardNumberMasked(paymentCard != null ? paymentCard.getCardNumberMasked() : "")
        .token(paymentCard != null ? paymentCard.getCardNumber() : "")
        .expirationDate(paymentCard != null ? paymentCard.getExpirationDate() : null)
        .citId((paymentCard != null) ? paymentCard.getCitId() : null)
        .processing((paymentCard != null) ? paymentCard.getProcessing().getValue() : null)
        .cardOrToken((paymentCard != null) ? paymentCard.getCardOrToken().getValue() : null)
        .paymentMethod(paymentMethod)
        .folioView(folioView)
        .build();

    var userDefinedCardType = reservationPaymentCardType.getUserDefinedCardType();
    if (Objects.nonNull(userDefinedCardType) && !userDefinedCardType.isEmpty()) {
      reservationPaymentCardType.setCardType(reservationPaymentCardType.getUserDefinedCardType());
    } else {
      reservationPaymentCardType.setCardType((paymentCard != null && paymentCard.getCardType() != null)
          ? paymentCard.getCardType().toString() : "");
    }

    return reservationPaymentCardType;
  }

  default UniqueIdType toCardIdForModel(UniqueIDType uniqueIDType) {
    if (uniqueIDType == null) {
      return null;
    }
    return UniqueIdType.builder()
        .id(uniqueIDType.getId())
        .type(uniqueIDType.getType())
        .build();
  }

  default CurrencyAmountType toCurrencyAmountTypeForModel(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrencyAmountType currencyAmountType) {
    if (currencyAmountType == null) {
      return null;
    }
    return CurrencyAmountType
        .builder()
        .amount(currencyAmountType.getAmount())
        .currencyCode(currencyAmountType.getCurrencyCode())
        .build();
  }

  @Named("toReservationOverrideReasonsForModel")
  default ReservationOverrideReasons toReservationOverrideReasonsForModel(
      Reservation reservationById) {
    var userDefinedFields = reservationById.getReservations().getReservation().get(0)
        .getUserDefinedFields();

    if (Objects.isNull(userDefinedFields)) {
      return null;
    }
    return userDefinedFields.getCharacterUDFs()
        .stream()
        .filter(characterUDFType -> characterUDFType.getName().equals(UDFC_08)
            && OVERRIDE_REASON_PATTERN.matcher(characterUDFType.getValue()).matches())
        .findFirst()
        .map(this::mapToReservationOverrideReason)
        .orElse(null);
  }

  private ReservationOverrideReasons mapToReservationOverrideReason(
      CharacterUDFType characterUDFType) {
    // from Opera, we get one string which we need to split to populate the object attributes
    var udfValueElements = Stream.of(characterUDFType.getValue().split(","))
        .map(String::trim)
        .toList();
    return ReservationOverrideReasons.builder()
        .reasonCode(udfValueElements.get(0))
        .reasonName(udfValueElements.get(1))
        .callerName(udfValueElements.get(2))
        .managerName(udfValueElements.size() > 3 ? udfValueElements.get(3) : EMPTY_STR)
        .build();
  }

  @Named("toReservationEmailNotificationsForModel")
  default ReservationEmailNotifications toReservationEmailNotificationsForModel(
      Reservation reservationById) {

    var userDefinedFieldsOptional = Optional.ofNullable(reservationById.getReservations()
        .getReservation().get(0).getUserDefinedFields());

    return userDefinedFieldsOptional.flatMap(
        userDefinedFieldsType -> userDefinedFieldsType.getCharacterUDFs()
            .stream()
            .filter(characterUDFType -> characterUDFType.getName().equals(UDFC_14))
            .findFirst()
            .map(this::mapToReservationEmailNotifications)).orElse(
        ReservationEmailNotifications.builder().sendEmailInvoice(Boolean.TRUE)
            .sendEmailConfirmation(Boolean.TRUE).build());
  }

  private ReservationEmailNotifications mapToReservationEmailNotifications(
      CharacterUDFType characterUDFType) {
    // from Opera we get one string which we need to split to populate the object attributes
    var udfValueElements = Stream.of(characterUDFType.getValue().split(""))
        .map(String::trim)
        .toList();
    return ReservationEmailNotifications.builder()
        .sendEmailConfirmation("Y".equalsIgnoreCase(udfValueElements.get(0)))
        .sendEmailInvoice("Y".equalsIgnoreCase(udfValueElements.get(1)))
        .build();
  }

  @Named("toGuaranteeForModel")
  default Guarantee toGuaranteeForModel(Reservation reservationById) {
    ResGuaranteeType resGuaranteeType =
        reservationById.getReservations().getReservation().get(0).getRoomStay().getGuarantee();
    return Guarantee.builder()
        .onHold(resGuaranteeType != null ? resGuaranteeType.getOnHold() : null)
        .guaranteeCode(resGuaranteeType != null ? resGuaranteeType.getGuaranteeCode() : null)
        .shortDescription(resGuaranteeType != null ? resGuaranteeType.getShortDescription() : null)
        .build();
  }

  @Named("toReservationStatusForModel")
  default String toReservationStatusForModel(Reservation reservationById) {
    return reservationById.getReservations().getReservation().get(0).getReservationStatus()
        .getValue();
  }

  @Named("toReservationPreCheckInForModel")
  default Boolean toReservationPreCheckInForModel(Reservation reservationById) {
    return reservationById.getReservations().getReservation().get(0).getPreRegistered();
  }

  @Named("toBalanceAmountForModel")
  default BigDecimal toBalanceAmountForModel(Reservation reservationById) {
    var balance = reservationById.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0).getBalance();
    return (balance != null) ? balance.getAmount() : null;
  }

  @Named("toGdsReferenceNumberForModel")
  default String toGdsReferenceNumberForModel(Reservation reservationById) {
    return Optional.ofNullable(
            reservationById.getReservations().getReservation().get(0).getExternalReferences())
        .orElseGet(Collections::emptyList).stream()
        .filter(extRef -> DISTR_AMADEUS.equalsIgnoreCase(extRef.getIdContext()) || DISTR_TRAVELPORT
            .equalsIgnoreCase(extRef.getIdContext())).findFirst().map(
            ExternalReferenceType::getId).orElse(null);
  }

}
