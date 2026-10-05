package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {Arrays.class})
public abstract class UpdateReservationAttachedProfilesRequestOhipMapper {

  @Mapping(expression = "java(injectReservationBookerCnpDetails(hotelId, reservationId, bookerProfileId, "
      + "companyProfileId))", target = "reservations")
  public abstract ChangeReservation toDto(String hotelId, String reservationId, String bookerProfileId,
      String companyProfileId);

  protected List<HotelReservationInstructionType> injectReservationBookerCnpDetails(
      String hotelId, String reservationId, String bookerProfileId, String companyProfileId) {

    // reservation id
    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    // booker profile link
    final var attachedProfiles = new ArrayList<ReservationProfileType>();
    attachedProfiles.add(getReservationProfileType(bookerProfileId, ResProfileTypeType.RESERVATIONCONTACT));
    attachedProfiles.add(getReservationProfileType(companyProfileId, ResProfileTypeType.COMPANY));

    final var reservationProfiles = new HotelReservationTypeReservationProfiles();
    reservationProfiles.setReservationProfile(attachedProfiles);

    final var hotelReservation = new HotelReservationInstructionType();

    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setHotelId(hotelId);
    hotelReservation.setReservationProfiles(reservationProfiles);

    return List.of(hotelReservation);
  }

  private ReservationProfileType getReservationProfileType(String profileId, ResProfileTypeType type) {
    final var profileUniqueIdType = new UniqueIDType();
    final var reservationProfile = new ReservationProfileType();
    if (StringUtils.isNotEmpty(profileId)) {
      profileUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
      profileUniqueIdType.setId(profileId);
      reservationProfile.setProfileIdList(List.of(profileUniqueIdType));
    }
    reservationProfile.setReservationProfileType(type);

    return reservationProfile;
  }
}
