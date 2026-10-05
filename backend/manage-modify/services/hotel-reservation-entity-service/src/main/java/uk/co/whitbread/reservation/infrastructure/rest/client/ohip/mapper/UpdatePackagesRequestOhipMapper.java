package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackagesRequestDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.basket.BasketItemDto;
import uk.co.whitbread.reservation.domain.model.in.ReservationPackagesRequest;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationPackagesByIdRequest;

@Mapper(componentModel = "spring")
public abstract class UpdatePackagesRequestOhipMapper {

  @Mapping(expression = "java(mapReservationsIdToDto(basketDto))", target = "reservationsId")
  public abstract ReservationPackagesRequestDto toDto(
      ReservationPackagesRequest reservationPackagesRequest, @Context BasketDto basketDto);

  @Mapping(expression = "java(mapReservationIdsToDto(reservationPackagesRequestByIdRequest))",
      target = "reservationsId")
  public abstract ReservationPackagesRequestDto toDto(
      UpdateReservationPackagesByIdRequest reservationPackagesRequestByIdRequest);

  protected List<String> mapReservationsIdToDto(BasketDto basketDto) {
    List<String> reservationIds = new ArrayList<>();
    for (BasketItemDto basketItemDto : basketDto.getItems()) {
      reservationIds.add(basketItemDto.getSourceId());
    }

    return reservationIds;
  }

  protected List<String> mapReservationIdsToDto(
      UpdateReservationPackagesByIdRequest reservationPackagesRequestByIdRequest) {
    List<String> reservationIds = new ArrayList<>();

    reservationPackagesRequestByIdRequest.getPreviousRoomsSelections()
        .forEach(previousRoomSelection ->
            reservationIds.add(previousRoomSelection.getReservationId()));

    return reservationIds;
  }
}
