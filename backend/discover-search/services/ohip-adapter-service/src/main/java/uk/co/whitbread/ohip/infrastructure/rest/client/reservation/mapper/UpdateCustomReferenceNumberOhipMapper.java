package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;

@Mapper(componentModel = "spring")
public abstract class UpdateCustomReferenceNumberOhipMapper {

  @Mapping(target = "reservations",
      expression = "java(injectReservations(hotelId, reservationId, customReference))")
  public abstract ChangeReservation toChangeReservationDto(String hotelId, String reservationId,
      String customReference);

  @Mapping(target = "reservationIdList", expression = "java(injectReservationIdList(reservationId))")
  @Mapping(target = "hotelId", source = "hotelId")
  @Mapping(target = "customReference", source = "customReference")
  public abstract HotelReservationInstructionType toHotelReservationInstructionTypeDto(String hotelId,
      String reservationId, String customReference);

  protected List<UniqueIDType> injectReservationIdList(String reservationId) {
    final var uniqueIdType = new UniqueIDType();

    uniqueIdType.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    uniqueIdType.setId(reservationId);

    return List.of(uniqueIdType);
  }

  protected List<HotelReservationInstructionType> injectReservations(
      String hotelId, String reservationId, String customReference) {

    return List.of(toHotelReservationInstructionTypeDto(hotelId, reservationId, customReference));
  }
}
