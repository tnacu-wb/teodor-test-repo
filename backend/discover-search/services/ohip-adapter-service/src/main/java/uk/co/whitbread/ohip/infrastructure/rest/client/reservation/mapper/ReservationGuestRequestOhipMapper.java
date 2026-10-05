package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_09;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_10;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_14;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_35;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_36;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.ohip.domain.model.reservation.in.AccompanyingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class ReservationGuestRequestOhipMapper {

  @Mapping(expression = "java(injectReservationGuestDetails(reservationGuestRequest, stayingGuest, bookerProfileId, "
      + "companyProfileId))",
      target = "reservations")
  public abstract ChangeReservation toDto(ReservationGuestRequest reservationGuestRequest,
      StayingGuest stayingGuest, String bookerProfileId, String companyProfileId);


  protected List<HotelReservationInstructionType> injectReservationGuestDetails(
      ReservationGuestRequest reservationGuestRequest, StayingGuest stayingGuest, String bookerProfileId,
      String companyProfileId) {

    final var resGuestAdditionalInfoType = new ResGuestAdditionalInfoType();
    resGuestAdditionalInfoType.setPurposeOfStay(reservationGuestRequest.getReasonForStay());
    final var resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();

    if (Boolean.TRUE.equals(stayingGuest.getSameAsBooker())) {
      // if the guest is the same as the booker, reuse the booker's profile ID
      UniqueIDType bookerUniqueIdType = new UniqueIDType();
      bookerUniqueIdType.setId(bookerProfileId);
      bookerUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
      resGuestTypeProfileInfo.setProfileIdList(Collections.singletonList(bookerUniqueIdType));
    } else {
      UniqueIDType guestUniqueIdType = new UniqueIDType();
      guestUniqueIdType.setId(stayingGuest.getStayingGuestDetails().getProfileId());
      guestUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
      resGuestTypeProfileInfo.setProfileIdList(List.of(guestUniqueIdType));
    }

    final var resGuestType = new ResGuestType();
    resGuestType.setProfileInfo(resGuestTypeProfileInfo);

    var resGuestTypeList = new ArrayList<>(List.of(resGuestType));

    if (shouldAddAccompanyingGuestProfile(stayingGuest.getAccompanyingGuestDetails())) {
      final var accompanyingGuestType = new ResGuestType();
      final var accompanyingResGuestTypeProfileInfo = new ResGuestTypeProfileInfo();

      UniqueIDType accompanyingGuestUniqueIdType = new UniqueIDType();
      accompanyingGuestUniqueIdType.setId(stayingGuest.getAccompanyingGuestDetails().getProfileId());
      accompanyingGuestUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
      accompanyingResGuestTypeProfileInfo.setProfileIdList(Collections.singletonList(accompanyingGuestUniqueIdType));

      accompanyingGuestType.primary(false);
      accompanyingGuestType.setProfileInfo(accompanyingResGuestTypeProfileInfo);

      resGuestTypeList.add(accompanyingGuestType);
    }


    // reservation id
    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(stayingGuest.getReservationId());

    // booker profile link
    final var attachedProfiles = new ArrayList<ReservationProfileType>();
    final var bookerProfileUniqueIdType = new UniqueIDType();
    final var reservationBookerProfile = new ReservationProfileType();
    bookerProfileUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
    bookerProfileUniqueIdType.setId(bookerProfileId);
    reservationBookerProfile.setProfileIdList(List.of(bookerProfileUniqueIdType));
    reservationBookerProfile.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
    attachedProfiles.add(reservationBookerProfile);

    if (StringUtils.isNotEmpty(companyProfileId)) {
      final var companyProfileUniqueIdType = new UniqueIDType();
      final var reservationCompanyProfile = new ReservationProfileType();
      companyProfileUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
      companyProfileUniqueIdType.setId(companyProfileId);
      reservationCompanyProfile.setProfileIdList(List.of(companyProfileUniqueIdType));
      reservationCompanyProfile.setReservationProfileType(ResProfileTypeType.COMPANY);
      attachedProfiles.add(reservationCompanyProfile);
    }

    final var reservationProfiles = new HotelReservationTypeReservationProfiles();
    reservationProfiles.setReservationProfile(attachedProfiles);

    final var hotelReservation = new HotelReservationInstructionType();
    final var userDefinedFields = new UserDefinedFieldsType();

    if (reservationGuestRequest.getSendEmailConfirmation() != null
        || reservationGuestRequest.getSendEmailInvoice() != null) {
      StringBuilder value = new StringBuilder();

      if (reservationGuestRequest.getSendEmailConfirmation() != null) {
        mapValueToUdfc14(reservationGuestRequest.getSendEmailConfirmation(), 0, value);
      }

      if (reservationGuestRequest.getSendEmailInvoice() != null) {
        mapValueToUdfc14(reservationGuestRequest.getSendEmailInvoice(), 1, value);
      }

      final var characterUDF14 = createCharacterUdf(UDFC_14, value.toString());
      userDefinedFields.addCharacterUDFsItem(characterUDF14);
    }

    if (StringUtils.isNotBlank(stayingGuest.getStayingGuestDetails().getEmployeeAccountId())) {
      final var characterUDF36 =
          createCharacterUdf(UDFC_36, stayingGuest.getStayingGuestDetails().getEmployeeAccountId());
      userDefinedFields.addCharacterUDFsItem(characterUDF36);
    }

    if (StringUtils.isNotEmpty(reservationGuestRequest.getCompanyAccountId())) {
      userDefinedFields.addCharacterUDFsItem(
          createCharacterUdf(UDFC_10, reservationGuestRequest.getCompanyAccountId()));
    }

    if (StringUtils.isNotEmpty(reservationGuestRequest.getUserAccountId())) {
      userDefinedFields.addCharacterUDFsItem(
          createCharacterUdf(UDFC_35, reservationGuestRequest.getUserAccountId()));
    }

    if (StringUtils.isNotEmpty(reservationGuestRequest.getBookingType())) {
      userDefinedFields.addCharacterUDFsItem(
          createCharacterUdf(UDFC_09, reservationGuestRequest.getBookingType()));
    }

    if (Objects.nonNull(userDefinedFields.getCharacterUDFs())) {
      hotelReservation.setUserDefinedFields(userDefinedFields);
    }

    hotelReservation.setReservationGuests(resGuestTypeList);
    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setHotelId(reservationGuestRequest.getHotelId());
    hotelReservation.setAdditionalGuestInfo(resGuestAdditionalInfoType);
    hotelReservation.setReservationProfiles(reservationProfiles);

    return List.of(hotelReservation);
  }

  private boolean shouldAddAccompanyingGuestProfile(AccompanyingGuestDetails accompanyingGuestDetails) {
    return accompanyingGuestDetails != null && StringUtils.isNotBlank(accompanyingGuestDetails.getProfileId());
  }

  private static void mapValueToUdfc14(Boolean udfValue, int position, StringBuilder value) {
    //return (Boolean.TRUE.equals(udfValue)) ? value.insert(position, "Y") : value.insert(position, "N");
    if (Boolean.TRUE.equals(udfValue)) {
      value.insert(position, "Y");
    } else {
      value.insert(position, "N");
    }
  }

  private CharacterUDFType createCharacterUdf(String name, String value) {
    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(name);
    characterUDF.setValue(value);
    return characterUDF;
  }
}