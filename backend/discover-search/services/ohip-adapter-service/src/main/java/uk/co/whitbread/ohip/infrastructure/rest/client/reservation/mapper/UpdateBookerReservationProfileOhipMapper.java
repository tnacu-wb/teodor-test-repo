package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class UpdateBookerReservationProfileOhipMapper {

  @Mapping(target = "reservations", expression = "java(injectReservationCustomerDetails(hotelId, "
      + "reservationId, guestProfileId, emailAddress))")
  public abstract ChangeReservation toDto(
      String hotelId,
      String reservationId,
      String guestProfileId,
      String emailAddress);

  protected List<HotelReservationInstructionType> injectReservationCustomerDetails(
      String hotelId, String reservationId, String guestProfileId, String emailAddress) {

    EmailType emailType = new EmailType();
    emailType.setEmailAddress(emailAddress);

    EmailInfoType emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);

    ProfileTypeEmails profileEmails = new ProfileTypeEmails();
    profileEmails.setEmailInfo(List.of(emailInfoType));

    ProfileType bookerReservationProfile = new ProfileType();
    bookerReservationProfile.setEmails(profileEmails);

    UniqueIDType profileId = new UniqueIDType();
    profileId.setId(guestProfileId);
    profileId.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());

    ReservationProfileType reservationProfile = new ReservationProfileType();

    reservationProfile.setProfileIdList(List.of(profileId));
    reservationProfile.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
    reservationProfile.setProfile(bookerReservationProfile);

    HotelReservationTypeReservationProfiles hotelReservationProfiles = new HotelReservationTypeReservationProfiles();
    hotelReservationProfiles.setReservationProfile(List.of(reservationProfile));

    UniqueIDType reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    final var hotelReservation = new HotelReservationInstructionType();
    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setHotelId(hotelId);
    hotelReservation.setReservationProfiles(hotelReservationProfiles);
    return List.of(hotelReservation);
  }
}
