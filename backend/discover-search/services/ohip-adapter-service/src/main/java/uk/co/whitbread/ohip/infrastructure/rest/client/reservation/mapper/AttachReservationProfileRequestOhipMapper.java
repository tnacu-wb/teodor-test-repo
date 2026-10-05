package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public abstract class AttachReservationProfileRequestOhipMapper {

  @Mapping(target = "reservations", expression = "java(injectProfile(reservationId, profileId))")
  public abstract ChangeReservation toDto(String reservationId, String profileId);

  protected List<HotelReservationInstructionType> injectProfile(String reservationId, String profileId) {
    final var hotelReservation = new HotelReservationInstructionType();

    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    reservationUniqueIdType.setId(reservationId);

    final var attachedProfiles = new ArrayList<ReservationProfileType>();
    final var companyProfileUniqueIdType = new UniqueIDType();
    final var reservationCompanyProfile = new ReservationProfileType();
    companyProfileUniqueIdType.setType(UniqueIdTypeEnumDto.PROFILE_TYPE.value());
    companyProfileUniqueIdType.setId(profileId);
    reservationCompanyProfile.setProfileIdList(List.of(companyProfileUniqueIdType));
    reservationCompanyProfile.setReservationProfileType(ResProfileTypeType.COMPANY);
    attachedProfiles.add(reservationCompanyProfile);

    final var reservationProfiles = new HotelReservationTypeReservationProfiles();
    reservationProfiles.setReservationProfile(attachedProfiles);

    hotelReservation.setReservationIdList(List.of(reservationUniqueIdType));
    hotelReservation.setReservationProfiles(reservationProfiles);

    return List.of(hotelReservation);
  }
}
