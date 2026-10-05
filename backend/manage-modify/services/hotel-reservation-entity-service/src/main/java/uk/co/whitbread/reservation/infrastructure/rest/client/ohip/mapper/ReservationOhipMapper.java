package uk.co.whitbread.reservation.infrastructure.rest.client.ohip.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UpdateReservationsRequestDto;
import uk.co.whitbread.reservation.domain.model.in.Reservation;
import uk.co.whitbread.reservation.domain.model.in.UpdateReservationsRequest;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReservationOhipMapper {

  @Mapping(target = "adults", source = "adultsNumber")
  @Mapping(target = "children", source = "childrenNumber")
  @Mapping(target = "cotRequired", source = "cotRequired")
  @Mapping(target = "roomRates.start", source = "roomRates.startDate")
  @Mapping(target = "roomRates.end", source = "roomRates.endDate")
  @Mapping(target = "roomRates.roomType", source = "roomRates.pmsRoomType")
  ReservationDto toDto(Reservation reservation);

  @Mapping(source = "updateReservationsRequest.reservations", target = "updateReservationsRequest")
  UpdateReservationsRequestDto toUpdateReservationsRequestDto(UpdateReservationsRequest updateReservationsRequest);
}
